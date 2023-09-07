package com.jeonghyeon.taxiproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jeonghyeon.taxiproject.R;
import com.jeonghyeon.taxiproject.dto.response.CommentResponseDto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>{
    private List<CommentResponseDto> commentList;

    // 생성자를 추가하여 commentList를 초기화합니다.
    public CommentAdapter(List<CommentResponseDto> commentList) {
        this.commentList = commentList != null ? commentList : new ArrayList<>();
    }

    public void setData(List<CommentResponseDto> commentList) {
        // commentList가 null인 경우 빈 리스트로 초기화합니다.
        this.commentList = commentList != null ? commentList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_comment, parent, false);
        return new CommentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentResponseDto comment = commentList.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        private TextView nicknameTextView;
        private TextView contentTextView;
        private TextView timestampTextView;
        private ImageView genderImageView;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            nicknameTextView = itemView.findViewById(R.id.txt_nickname);
            contentTextView = itemView.findViewById(R.id.et_comment);
            timestampTextView = itemView.findViewById(R.id.createTimeTextView);
            genderImageView = itemView.findViewById(R.id.genderImageView);
        }

        public void bind(CommentResponseDto comment) {
            nicknameTextView.setText(comment.getNickname());
            contentTextView.setText(comment.getContent());
            if (comment.getGender() == 0) {
                genderImageView.setImageResource(R.drawable.baseline_face_4_24);
            } else {
                genderImageView.setImageResource(R.drawable.baseline_face_6_24);
            }
            // 시간을 "yyyy-MM-dd HH:mm:ss" 형식으로 포맷팅
            String formattedTimestamp = formatDate(comment.getCreateAt(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
            timestampTextView.setText(formattedTimestamp);
        }
    }

    // 날짜 포맷 변경 메서드
    private String formatDate(String inputDate, String inputFormat, String outputFormat) {
        try {
            SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputFormat);
            SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat);
            Date date = inputDateFormat.parse(inputDate);
            return outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return inputDate; // 변환 실패 시 원본 값을 반환
        }
    }
}
