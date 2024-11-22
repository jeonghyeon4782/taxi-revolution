package com.jeonghyeon.taxiproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jeonghyeon.taxiproject.R;
import com.jeonghyeon.taxiproject.dto.response.BelongPostResponseDto;
import com.jeonghyeon.taxiproject.dto.response.PostResponseDto;

import java.util.ArrayList;
import java.util.List;

public class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.ViewHolder> {
    private List<BelongPostResponseDto> dataList = new ArrayList<>();
    private ParentAdapter.OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(BelongPostResponseDto post);
    }

    public void setOnDeleteClickListener(ParentAdapter.OnDeleteClickListener listener) {
        onDeleteClickListener = listener;
    }

    public ParentAdapter(List<BelongPostResponseDto> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_parent, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BelongPostResponseDto item = dataList.get(position);
        holder.bind(item);

        holder.setChildAdapterParentDto(item);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setData(List<BelongPostResponseDto> newData) {
        dataList.clear();
        dataList.addAll(newData);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RecyclerView belongMembersRecyclerView;
        private ChildAdapter memberAdapter;
        private TextView recruitmentStatusTextView;
        private TextView departureLocationTextView;
        private TextView destinationLocationTextView;
        private TextView allSeatTextView;
        private TextView remainSeatTextView;
        private TextView titleTextView;
        private Button cancelBtn;

        public void setChildAdapterParentDto(BelongPostResponseDto parentDto) {
            // ChildAdapter 객체 초기화
            memberAdapter = new ChildAdapter(itemView.getContext(), parentDto.getBelongMembers());
            memberAdapter.setParentDto(parentDto);
            belongMembersRecyclerView.setAdapter(memberAdapter);
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            belongMembersRecyclerView = itemView.findViewById(R.id.belongMembersRecyclerView);
            belongMembersRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            recruitmentStatusTextView = itemView.findViewById(R.id.recruitmentStatusTextView);
            departureLocationTextView = itemView.findViewById(R.id.departureLocationTextView);
            destinationLocationTextView = itemView.findViewById(R.id.destinationLocationTextView);
            allSeatTextView = itemView.findViewById(R.id.allSeatTextView);
            remainSeatTextView = itemView.findViewById(R.id.remainSeatTextView);
            cancelBtn = itemView.findViewById(R.id.myButton);

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onDeleteClickListener != null) {
                        onDeleteClickListener.onDeleteClick(dataList.get(getAdapterPosition())); // 수정된 부분
                    }
                }
            });
        }

        public void bind(BelongPostResponseDto item) {
            titleTextView.setText(item.getTitle());
            recruitmentStatusTextView.setText(item.getRecruitmentStatus());
            departureLocationTextView.setText(item.getDepartureLocation());
            destinationLocationTextView.setText(item.getDestinationLocation());
            allSeatTextView.setText(String.valueOf(item.getAllSeat()));
            remainSeatTextView.setText(String.valueOf(item.getRemainSeat()));

            // 게시물에 소속된 멤버 목록을 표시
            ChildAdapter memberAdapter = new ChildAdapter(itemView.getContext(), item.getBelongMembers());
            belongMembersRecyclerView.setAdapter(memberAdapter);

            // authority 값에 따라 버튼 텍스트 설정
            if (item.getAuthority() == 0) {
                cancelBtn.setText("삭제");
            } else {
                cancelBtn.setText("나가기");
            }
        }
    }
}

