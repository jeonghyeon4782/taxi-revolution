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
        holder.btEdit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Guardian guardian = guardians.get(holder.getAdapterPosition());

                final int sID = guardian.getGuardianId();
                String sText = guardian.getGuardianNum();

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_update_guardian);

                int width = WindowManager.LayoutParams.MATCH_PARENT;
                int height = WindowManager.LayoutParams.WRAP_CONTENT;

                dialog.getWindow().setLayout(width, height);

                dialog.show();

                final EditText editText = dialog.findViewById(R.id.dialog_edit_text);
                Button bt_update = dialog.findViewById(R.id.bt_update);

                editText.setText(sText);

                // 수정 버튼 클릭 시
                bt_update.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        String uText = editText.getText().toString().trim();

                        // 공백인 경우
                        if (uText.isEmpty()) {
                            Toast.makeText(context, "보호자 번호를 입력하세요", Toast.LENGTH_SHORT).show();
                        }
                        // 11자리가 아닌 경우
                        else if (uText.length() != 11) {
                            Toast.makeText(context, "보호자 번호는 11자리여야 합니다", Toast.LENGTH_SHORT).show();
                        }
                        // 숫자가 아닌 문자를 포함하는 경우
                        else if (!uText.matches("\\d+")) {
                            Toast.makeText(context, "보호자 번호는 숫자로만 입력해야 합니다", Toast.LENGTH_SHORT).show();
                        }
                        // 모든 조건을 만족하는 경우
                        else {
                            dialog.dismiss();
                            database.guardianDao().update(sID, uText);

                            guardians.clear();
                            guardians.addAll(database.guardianDao().getAll());
                            notifyDataSetChanged();
                            Toast.makeText(context, "보호자 번호 수정 완료", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

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
