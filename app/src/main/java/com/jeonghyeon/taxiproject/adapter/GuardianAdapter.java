package com.jeonghyeon.taxiproject.adapter;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jeonghyeon.taxiproject.R;
import com.jeonghyeon.taxiproject.roomDB.RoomDB;
import com.jeonghyeon.taxiproject.domain.Guardian;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_guardian, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position)
    {
        final Guardian data = guardians.get(position);
        database = RoomDB.getInstance(context);
        holder.textView.setText(data.getGuardianNum());

        // 삭제 버튼 클릭 시
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
                Toast.makeText(context, "보호자 번호 삭제 완료", Toast.LENGTH_SHORT).show();
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
        Button btDelete;

        public ViewHolder(@NonNull View view)
        {
            super(view);
            textView = view.findViewById(R.id.text_view);
            btDelete = view.findViewById(R.id.bt_delete);
        }
    }
}
