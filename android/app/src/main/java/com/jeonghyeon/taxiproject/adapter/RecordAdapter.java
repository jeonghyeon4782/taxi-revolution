package com.jeonghyeon.taxiproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jeonghyeon.taxiproject.R;
import com.jeonghyeon.taxiproject.roomDB.RoomDB;
import com.jeonghyeon.taxiproject.domain.Record;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {

    private List<Record> recordList;
    private Context context;
    private RoomDB database;

    public RecordAdapter(List<Record> recordList, Context context) {
        this.recordList = recordList;
        this.context = context;
        database = RoomDB.getInstance(context);
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_record, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        Record record = recordList.get(position);
        // Record 객체의 정보를 아이템 뷰 홀더에 바인딩
        holder.bind(record);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Record record1 = recordList.get(holder.getAdapterPosition());

                // 데이터베이스에서 record 삭제
                database.recordDao().delete(record1);
                // recordList에서 삭제된 record 제거
                recordList.remove(record1);
                // 어댑터에 변경 사항 알림
                notifyDataSetChanged();
                Toast.makeText(context, "탑승 기록 삭제 완료", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    static class RecordViewHolder extends RecyclerView.ViewHolder {

        private TextView boardingTimeTextView;
        private TextView alightingTimeTextView;
        private TextView vehicleNumberTextView;
        private TextView boardingLocationTextView;
        private TextView alightingLocationTextView;
        private Button deleteButton;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            boardingTimeTextView = itemView.findViewById(R.id.boardingTimeTextView);
            alightingTimeTextView = itemView.findViewById(R.id.alightingTimeTextView);
            vehicleNumberTextView = itemView.findViewById(R.id.vehicleNumberTextView);
            boardingLocationTextView = itemView.findViewById(R.id.boardingLocationTextView);
            alightingLocationTextView = itemView.findViewById(R.id.alightingLocationTextView);
            deleteButton = itemView.findViewById(R.id.btn_delete);
        }

        public void bind(Record record) {
            // Record 객체의 정보를 뷰에 설정
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            String boardingTime = sdf.format(new Date(record.getBoardingTime()));
            String alightingTime = sdf.format(new Date(record.getAlightingTime()));
            String vehicleNumber = record.getVehicleNumber();
            String boardingLocation = record.getBoardingLocation();
            String alightingLocation = record.getAlightingLocation();

            boardingTimeTextView.setText(boardingTime);
            alightingTimeTextView.setText(alightingTime);
            vehicleNumberTextView.setText(vehicleNumber);
            boardingLocationTextView.setText(boardingLocation);
            alightingLocationTextView.setText(alightingLocation);

            // Record의 정보를 표시할 뷰들에 대한 설정 추가
        }
    }
}