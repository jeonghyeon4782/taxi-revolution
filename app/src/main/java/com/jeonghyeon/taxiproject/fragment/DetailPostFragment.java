package com.jeonghyeon.taxiproject.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jeonghyeon.taxiproject.R;

public class DetailPostFragment extends Fragment {

    private Long postId; // primary key
    private String title; // 제목
    private String content; // 내용
    private String departureLocation; // 출발지
    private String destinationLocation; // 도착지
    private String recruitmentStatus; // 모집상태
    private int remainSeat; // 남은 좌석
    private int allSeat; // 총 좌석
    private String departureTime; // 출발시간
    private String createTime; // 생성시간
    private String nickname; // 글쓴이 아이디
    private int gender; // 여자 : 0

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


    public DetailPostFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_post, container, false);

        recruitmentStatusTextView = view.findViewById(R.id.recruitmentStatusTextView);
        createTimeTextView = view.findViewById(R.id.createTimeTextView);
        departureTimeTextView = view.findViewById(R.id.departureTimeTextView);
        departureLocationTextView = view.findViewById(R.id.departureLocationTextView);
        destinationLocationTextView = view.findViewById(R.id.destinationLocationTextView);
        memberIdTextView = view.findViewById(R.id.memberIdTextView);
        allSeatTextView = view.findViewById(R.id.allSeatTextView);
        remainSeatTextView = view.findViewById(R.id.remainSeatTextView);
        titleTextView = view.findViewById(R.id.titleTextView);
        genderImageView = view.findViewById(R.id.genderImageView);
        contentTextView = view.findViewById(R.id.contentTextView);

        Bundle args = getArguments();
        if (args != null) {
            postId = args.getLong("postId");
            title = args.getString("title");
            content = args.getString("content");
            departureLocation = args.getString("departureLocation");
            destinationLocation = args.getString("destinationLocation");
            recruitmentStatus = args.getString("recruitmentStatus");
            departureTime = args.getString("departureTime");
            createTime = args.getString("createTime");
            remainSeat = args.getInt("remainSeat");
            allSeat = args.getInt("allSeat");
            gender = args.getInt("gender");
            nickname = args.getString("nickname");
        }

        titleTextView.setText(title);
        contentTextView.setText(content);
        departureLocationTextView.setText(departureLocation);
        destinationLocationTextView.setText(destinationLocation);
        recruitmentStatusTextView.setText(recruitmentStatus);
        departureTimeTextView.setText(departureTime);
        createTimeTextView.setText(createTime);
        remainSeatTextView.setText(String.valueOf(remainSeat));
        allSeatTextView.setText(String.valueOf(allSeat));
        memberIdTextView.setText(nickname);

        if (gender == 0) {
            genderImageView.setImageResource(R.drawable.baseline_face_4_24);
        } else {
            genderImageView.setImageResource(R.drawable.baseline_face_6_24);
        }

        return view;
    }

    // toast 보여주기
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}