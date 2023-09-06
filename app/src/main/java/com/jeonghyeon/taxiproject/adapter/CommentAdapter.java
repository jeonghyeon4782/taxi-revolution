package com.jeonghyeon.taxiproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jeonghyeon.taxiproject.R;
import com.jeonghyeon.taxiproject.dto.response.CommentResponseDto;

import java.util.ArrayList;
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

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            nicknameTextView = itemView.findViewById(R.id.txt_nickname);
            contentTextView = itemView.findViewById(R.id.et_comment);
            timestampTextView = itemView.findViewById(R.id.createTimeTextView);
        }

        public void bind(CommentResponseDto comment) {
            nicknameTextView.setText(comment.getNickname());
            contentTextView.setText(comment.getContent());
            timestampTextView.setText(comment.getCreateAt());
        }
    }

}
