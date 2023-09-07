package com.jeonghyeon.taxiproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jeonghyeon.taxiproject.R;
import com.jeonghyeon.taxiproject.dto.response.PostResponseDto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyPostAdapter extends RecyclerView.Adapter<MyPostAdapter.ViewHolder>{
    private List<PostResponseDto> postList;
    private OnDeleteClickListener onDeleteClickListener;
    private OnModifyClickListener onModifyClickListener;

    public void setPosts(List<PostResponseDto> posts) {
        this.postList = posts;
        notifyDataSetChanged();
    }

    public List<PostResponseDto> getPosts() {
        return postList;
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(PostResponseDto post);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        onDeleteClickListener = listener;
    }

    public interface OnModifyClickListener {
        void onModifyClick(PostResponseDto post);
    }


    public void setOnModifyClickListener(OnModifyClickListener listener) {
        onModifyClickListener = listener;
    }

    public void setData(List<PostResponseDto> postList) {
        this.postList = postList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_mypost, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PostResponseDto post = postList.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return postList != null ? postList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView recruitmentStatusTextView;
        private TextView contentTextView;
        private TextView createTimeTextView;
        private TextView departureTimeTextView;
        private TextView departureLocationTextView;
        private TextView destinationLocationTextView;
        private TextView memberIdTextView;
        private TextView allSeatTextView;
        private TextView remainSeatTextView;
        private TextView titleTextView;
        private ImageView genderImageView;
        private Button deleteBtn, modifyBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recruitmentStatusTextView = itemView.findViewById(R.id.recruitmentStatusTextView);
            createTimeTextView = itemView.findViewById(R.id.createTimeTextView);
            departureTimeTextView = itemView.findViewById(R.id.departureTimeTextView);
            departureLocationTextView = itemView.findViewById(R.id.departureLocationTextView);
            destinationLocationTextView = itemView.findViewById(R.id.destinationLocationTextView);
            memberIdTextView = itemView.findViewById(R.id.memberIdTextView);
            allSeatTextView = itemView.findViewById(R.id.allSeatTextView);
            remainSeatTextView = itemView.findViewById(R.id.remainSeatTextView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            genderImageView = itemView.findViewById(R.id.genderImageView);
            deleteBtn = itemView.findViewById(R.id.btn_delete);
            modifyBtn = itemView.findViewById(R.id.btn_modify);

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onDeleteClickListener != null) {
                        onDeleteClickListener.onDeleteClick(postList.get(getAdapterPosition()));
                    }
                }
            });

            modifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onModifyClickListener != null) {
                        onModifyClickListener.onModifyClick(postList.get(getAdapterPosition()));
                    }
                }
            });
        }

        public void bind(PostResponseDto post) {
            recruitmentStatusTextView.setText(post.getRecruitmentStatus());
            createTimeTextView.setText(post.getCreateTime());
            departureLocationTextView.setText(post.getDepartureLocation());
            destinationLocationTextView.setText(post.getDestinationLocation());
            memberIdTextView.setText(post.getNickname());
            allSeatTextView.setText(String.valueOf(post.getAllSeat()));
            remainSeatTextView.setText(String.valueOf(post.getRemainSeat()));
            titleTextView.setText(post.getTitle());
            if (post.getGender() == 0) {
                genderImageView.setImageResource(R.drawable.baseline_face_4_24);
            } else {
                genderImageView.setImageResource(R.drawable.baseline_face_6_24);
            }
            // SimpleDateFormat을 사용하여 날짜 및 시간을 원하는 형식으로 포맷
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시mm분", Locale.getDefault());
            try {
                Date departureTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(post.getCreateTime());
                departureTimeTextView.setText(dateFormat.format(departureTime));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}