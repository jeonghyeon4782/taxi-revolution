package com.jeonghyeon.taxiproject.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jeonghyeon.taxiproject.R;
import com.jeonghyeon.taxiproject.roomDB.RoomDB;
import com.jeonghyeon.taxiproject.adapter.GuardianAdapter;
import com.jeonghyeon.taxiproject.domain.Frequency;
import com.jeonghyeon.taxiproject.domain.Guardian;

import java.util.ArrayList;
import java.util.List;

public class GuardianFragment extends Fragment {

    // 화면 요소
    private EditText etGuardianNum;
    private ImageView btnAddGuardian;
    private RecyclerView recyclerView;
    private TextView txtFrequency;
    private ImageView btnIncrease, btnDecrease;

    // 보호자 번호 관련 요소
    private List<Guardian> guardians = new ArrayList<>();
    // 문자 주기 관련
    private Frequency frequency;
    // RoomDB 선언
    private RoomDB database;
    // 어뎁터 선언
    private GuardianAdapter adapter;

    public GuardianFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // 뷰 변수 초기화
        View view = inflater.inflate(R.layout.fragment_guardian, container, false);

        // 뷰 요소 초기화
        etGuardianNum = view.findViewById(R.id.et_guardianNum);
        btnAddGuardian = view.findViewById(R.id.btn_addGuardian);
        recyclerView = view.findViewById(R.id.recycler_view);
        txtFrequency = view.findViewById(R.id.txt_frequency);
        btnIncrease = view.findViewById(R.id.btn_increase);
        btnDecrease = view.findViewById(R.id.btn_decrease);

        // DB 객체 생성
        database = RoomDB.getInstance(requireContext());

        guardians = database.guardianDao().getAll();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new GuardianAdapter(requireContext(), guardians);
        recyclerView.setAdapter(adapter);

        // 문자 주기 객체 초기화
        frequency = database.frequencyDao().getFrequency();

        // 만약 frequency가 비어있다면 새로운 객체 생성 후 텍스트 편집
        if (frequency == null) {
            frequency = new Frequency();
            frequency.setFrequencyNum("5");
            database.frequencyDao().insert(frequency);
            txtFrequency.setText("5"+"분");
        } else {
            txtFrequency.setText(frequency.getFrequencyNum() + "분");
        }

        // + 버튼 클릭 시
        btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valueString = txtFrequency.getText().toString();
                valueString = valueString.replaceAll("[^0-9]", ""); // "분" 제거
                int value = Integer.parseInt(valueString);

                if (value >= 10) {
                    showToast("문자 주기의 최대 시간은 10분 입니다");
                } else {
                    value += 1;
                    frequency.setFrequencyNum(String.valueOf(value));
                    txtFrequency.setText(value + "분");

                    // DB에 업데이트
                    database.frequencyDao().update(frequency);
                }
            }
        });

        // - 버튼 클릭 시
        btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valueString = txtFrequency.getText().toString();
                valueString = valueString.replaceAll("[^0-9]", ""); // "분" 제거
                int value = Integer.parseInt(valueString);

                if (value <= 1) {
                    showToast("문자 주기의 최소 시간은 1분 입니다");
                } else {
                    value -= 1;
                    frequency.setFrequencyNum(String.valueOf(value));
                    txtFrequency.setText(value + "분");

                    // DB에 업데이트
                    database.frequencyDao().update(frequency);
                }
            }
        });

        // 추가 버튼을 클릭 시
        btnAddGuardian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String guardianNum = etGuardianNum.getText().toString().trim();

                // guardianNum이 공백인 경우
                if (guardianNum.isEmpty()) {
                    showToast("보호자 번호를 입력하세요");
                }
                // guardianNum이 11자리가 아닌 경우
                else if (guardianNum.length() != 11) {
                    showToast("보호자 번호는 11자리여야 합니다");
                }
                // guardianNum이 숫자가 아닌 문자를 포함하는 경우
                else if (!guardianNum.matches("\\d+")) {
                    showToast("보호자 번호는 숫자로만 입력해야 합니다");
                }
                // 모든 조건을 만족하는 경우
                else {
                    Guardian data = new Guardian();
                    data.setGuardianNum(guardianNum);
                    database.guardianDao().insert(data);

                    etGuardianNum.setText("");

                    guardians.clear();
                    guardians.addAll(database.guardianDao().getAll());
                    adapter.notifyDataSetChanged();
                    showToast("보호자 번호 추가 완료");
                }
            }
        });

        return view;
    }

    // toast 보여주기
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onResume() {
        super.onResume();

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
}