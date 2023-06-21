package com.jeonghyeon.taxiproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jeonghyeon.taxiproject.R;
import com.jeonghyeon.taxiproject.RoomDB;
import com.jeonghyeon.taxiproject.adapter.RecordAdapter;
import com.jeonghyeon.taxiproject.model.Record;

import java.util.ArrayList;
import java.util.List;

public class RecordFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecordAdapter recordAdapter;
    private List<Record> recordList;
    private RoomDB database;
    private TextView tvEmptyList;

    public RecordFragment() {
        // Required empty public constructor
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

        recyclerView = view.findViewById(R.id.recyclerView);
        tvEmptyList = view.findViewById(R.id.tv_emptyList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recordList = new ArrayList<>();
        recordAdapter = new RecordAdapter(recordList, requireContext());
        recyclerView.setAdapter(recordAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // RoomDB에서 Record 정보 가져오기
        fetchRecordsFromDatabase();
    }

    private void fetchRecordsFromDatabase() {
        // RoomDB에서 Record 정보를 가져오는 작업을 수행합니다.

        // 레코드 목록을 비웁니다.
        recordList.clear();

        // RoomDB에서 모든 Record 가져오기
        List<Record> records = database.recordDao().getAll();

        // 가져온 Record들을 recordList에 추가합니다.
        recordList.addAll(records);

        if (recordList.isEmpty()) {
            // 탑승 기록이 없을 경우 텍스트뷰를 표시하고, 리사이클러뷰는 숨깁니다.
            tvEmptyList.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            // 탑승 기록이 있을 경우 텍스트뷰를 숨기고, 리사이클러뷰를 표시합니다.
            tvEmptyList.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        // 어댑터에 데이터 변경을 알립니다.
        recordAdapter.notifyDataSetChanged();
    }
}