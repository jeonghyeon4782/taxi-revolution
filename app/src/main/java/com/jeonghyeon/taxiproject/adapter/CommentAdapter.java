package com.jeonghyeon.taxiproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    private OnCommentUpdateListener commentUpdateListener;
    private OnCommentDeleteListener commentDeleteListener;
    private boolean isEditing = false; // 수정 가능 상태 여부

    public interface OnCommentDeleteListener {
        void onDeleteComment(Long commentId);
    }

    public void setOnCommentDeleteListener(OnCommentDeleteListener listener) {
        this.commentDeleteListener = listener;
    }

    public interface OnCommentUpdateListener {
        void onUpdateComment(String content, Long commentId);
    }

    public void setOnCommentUpdateListener(OnCommentUpdateListener listener) {
        this.commentUpdateListener = listener;
    }

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
        private EditText contentText;
        private TextView timestampTextView;
        private ImageView genderImageView;
        private Button btnDelete, btnUpdate;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            nicknameTextView = itemView.findViewById(R.id.txt_nickname);
            contentText = itemView.findViewById(R.id.et_comment);
            timestampTextView = itemView.findViewById(R.id.createTimeTextView);
            genderImageView = itemView.findViewById(R.id.genderImageView);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnUpdate = itemView.findViewById(R.id.btn_modify);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (commentDeleteListener != null) {
                        Long commentId = commentList.get(getAdapterPosition()).getCommentId();
                        commentDeleteListener.onDeleteComment(commentId);
                    }
                }
            });

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isEditing) {
                        // 완료 버튼 클릭 시
                        String updatedContent = contentText.getText().toString().trim();
                        if (updatedContent.isEmpty()) {
                            // updatedContent가 공백인 경우, 토스트 메시지 표시
                            Toast.makeText(itemView.getContext(), "댓글 내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                        } else {
                            // updatedContent가 공백이 아닌 경우, 수정 내용을 처리
                            Long commentId = commentList.get(getAdapterPosition()).getCommentId();
                            if (commentUpdateListener != null) {
                                commentUpdateListener.onUpdateComment(updatedContent, commentId);
                            }
                            btnUpdate.setText("수정"); // 버튼 텍스트 변경
                            contentText.setEnabled(false); // 수정할 수 없는 상태로 변경
                            isEditing = !isEditing; // 상태 토글
                        }
                    } else {
                        // 수정 버튼 클릭 시
                        contentText.setEnabled(true); // 수정 가능한 상태로 변경
                        btnUpdate.setText("완료"); // 버튼 텍스트 변경
                        isEditing = !isEditing; // 상태 토글
                    }
                }
            });
        }

        public void bind(CommentResponseDto comment) {
            nicknameTextView.setText(comment.getNickname());
            contentText.setText(comment.getContent());
            contentText.setEnabled(false); // 수정할 수 없는 상태로 변경
            if (comment.getGender() == 0) {
                genderImageView.setImageResource(R.drawable.baseline_face_4_24);
            } else {
                genderImageView.setImageResource(R.drawable.baseline_face_6_24);
            }
            // 시간을 "yyyy-MM-dd HH:mm:ss" 형식으로 포맷팅
            String formattedTimestamp = formatDate(comment.getCreateAt(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
            timestampTextView.setText(formattedTimestamp);

            // authority 값에 따라 버튼 가시성 설정
            if (comment.getAuthority() == 1) {
                btnDelete.setVisibility(View.VISIBLE);
                btnUpdate.setVisibility(View.VISIBLE);
            } else {
                btnDelete.setVisibility(View.GONE);
                btnUpdate.setVisibility(View.GONE);
            }
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
