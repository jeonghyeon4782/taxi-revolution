package com.jeonghyeon.taxiproject.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jeonghyeon.taxiproject.R;
import com.jeonghyeon.taxiproject.RoomDB;
import com.jeonghyeon.taxiproject.model.Guardian;

import java.util.List;

public class GuardianAdapter extends RecyclerView.Adapter<GuardianAdapter.ViewHolder>
{
    private List<Guardian> guardians;
    private Context context;
    private RoomDB database;

    public GuardianAdapter(Context context, List<Guardian> guardians)
    {
        this.context = context;
        this.guardians = guardians;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position)
    {
        final Guardian data = guardians.get(position);
        database = RoomDB.getInstance(context);
        holder.textView.setText(data.getGuardianNum());
        holder.btEdit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Guardian guardian = guardians.get(holder.getAdapterPosition());

                final int sID = guardian.getGuardianId();
                String sText = guardian.getGuardianNum();

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_update);

                int width = WindowManager.LayoutParams.MATCH_PARENT;
                int height = WindowManager.LayoutParams.WRAP_CONTENT;

                dialog.getWindow().setLayout(width, height);

                dialog.show();

                final EditText editText = dialog.findViewById(R.id.dialog_edit_text);
                Button bt_update = dialog.findViewById(R.id.bt_update);

                editText.setText(sText);

                bt_update.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dialog.dismiss();
                        String uText = editText.getText().toString().trim();

                        database.guardianDao().update(sID, uText);

                        guardians.clear();
                        guardians.addAll(database.guardianDao().getAll());
                        notifyDataSetChanged();
                    }
                });
            }
        });

        /* 삭제 클릭 */
        holder.btDelete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Guardian guardian = guardians.get(holder.getAdapterPosition());

                database.guardianDao().delete(guardian);

                int position = holder.getAdapterPosition();
                guardians.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, guardians.size());
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return guardians.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textView;
        ImageView btEdit, btDelete;

        public ViewHolder(@NonNull View view)
        {
            super(view);
            textView = view.findViewById(R.id.text_view);
            btEdit = view.findViewById(R.id.bt_edit);
            btDelete = view.findViewById(R.id.bt_delete);
        }
    }
}
