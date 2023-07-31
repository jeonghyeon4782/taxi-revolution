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
import com.jeonghyeon.taxiproject.domain.Record;
import com.jeonghyeon.taxiproject.dto.response.PostResponseDto;
import com.jeonghyeon.taxiproject.roomDB.RoomDB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.RecordViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(PostResponseDto post);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    private List<PostResponseDto> posts;
    private List<PostResponseDto> backUpPosts; // 원본 복원

    public void setPosts(List<PostResponseDto> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    public void setBackUpPosts(List<PostResponseDto> posts) {
        this.backUpPosts = new ArrayList<>(posts); // 리스트의 깊은 복사 생성
        notifyDataSetChanged();
    }

    public List<PostResponseDto> getPosts() {
        return posts;
    }

    public List<PostResponseDto> getBackUpPosts() {
        return backUpPosts;
    }

    public void filterPosts(String inputText) {
        if (inputText.isEmpty()) {
            setPosts(backUpPosts);
        } else {
            List<PostResponseDto> filteredList = new ArrayList<>();
            for (PostResponseDto post : backUpPosts) {
                if (post.getContent().toLowerCase().contains(inputText.toLowerCase()) ||
                        post.getTitle().toLowerCase().contains(inputText.toLowerCase()) ||
                        post.getDepartureLocation().toLowerCase().contains(inputText.toLowerCase()) ||
                        post.getDestinationLocation().toLowerCase().contains(inputText.toLowerCase())) {
                    filteredList.add(post);
                }
            }
            setPosts(filteredList);
        }
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_post, parent, false);
        RecordViewHolder viewHolder = new RecordViewHolder(view);

        // Set the click listener for each item view
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(posts.get(position));
                    }
                }
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        PostResponseDto post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts != null ? posts.size() : 0;
    }

    static class RecordViewHolder extends RecyclerView.ViewHolder {

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

        public RecordViewHolder(@NonNull View itemView) {
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
        }

        public void bind(PostResponseDto post) {
            recruitmentStatusTextView.setText(post.getRecruitmentStatus());
            createTimeTextView.setText(post.getCreateTime());
            departureTimeTextView.setText(post.getDepartureTime());
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
        }
    }
}