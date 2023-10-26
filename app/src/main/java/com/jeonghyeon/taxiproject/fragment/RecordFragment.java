package com.jeonghyeon.taxiproject.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jeonghyeon.taxiproject.R;
import com.jeonghyeon.taxiproject.roomDB.RoomDB;
import com.jeonghyeon.taxiproject.adapter.RecordAdapter;
import com.jeonghyeon.taxiproject.domain.Record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecordFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecordAdapter recordAdapter;
    private List<Record> recordList;
    private RoomDB database;
    private TextView tvEmptyList;
    private SwipeRefreshLayout swipeRefreshLayout;

    public RecordFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = RoomDB.getInstance(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.refresh_layout);

        recyclerView = view.findViewById(R.id.recyclerView);
        tvEmptyList = view.findViewById(R.id.tv_emptyList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recordList = new ArrayList<>();
        recordAdapter = new RecordAdapter(recordList, requireContext());
        recyclerView.setAdapter(recordAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchRecordsFromDatabase();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // RoomDB에서 Record 정보 가져오기
        fetchRecordsFromDatabase();

        // 뒤로가기 버튼 이벤트를 감지하는 리스너를 설정
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            private boolean doubleBackToExitPressedOnce = false;

            @Override
            public void handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    // 앱 종료
                    requireActivity().finish();
                } else {
                    doubleBackToExitPressedOnce = true;
                    Toast.makeText(requireContext(), "뒤로가기 버튼을 한 번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();

                    // 2초 후에 doubleBackToExitPressedOnce 변수를 초기화
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);
                }
            }
        });
    }

    private void fetchRecordsFromDatabase() {
        recordList.clear();

        List<Record> records = database.recordDao().getAll();

        // 리스트를 뒤집어서 최신 탑승 기록이 상단에 표시되도록 함
        Collections.reverse(records);

        recordList.addAll(records);

        if (recordList.isEmpty()) {
            tvEmptyList.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyList.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        recordAdapter.notifyDataSetChanged();
    }
}