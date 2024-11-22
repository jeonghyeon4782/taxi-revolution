package com.jeonghyeon.taxiproject.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jeonghyeon.taxiproject.R;
import com.jeonghyeon.taxiproject.roomDB.RoomDB;
import com.jeonghyeon.taxiproject.activity.MainActivity;
import com.jeonghyeon.taxiproject.domain.Record;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AlightingCheckFragment extends Fragment {

    public AlightingCheckFragment() {
        // Required empty public constructor
    }

    private Context context;
    private Button btnAlighting;
    private EditText etVehicleNum, etBoardingTime, etBoardingLocation, etAlightingTime, etAlightingLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int PERMISSION_REQUEST_LOCATION = 123;

    private String vehicleNumber; // 차량번호
    private String boardingLocation; // 승차위치
    private String boardingTime; // 승차시간
    private String alightingTime; // 하차시간
    private String alightingLocation; // 하차위치

    // RoomDB 선언
    private RoomDB database;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alighting_check, container, false);

        etVehicleNum = view.findViewById(R.id.et_vehicleNum);
        etBoardingTime = view.findViewById(R.id.et_boardingTime);
        etBoardingLocation = view.findViewById(R.id.et_boardingLocation);
        etAlightingTime = view.findViewById(R.id.et_alightingTime);
        etAlightingLocation = view.findViewById(R.id.et_alightingLocation);
        btnAlighting = view.findViewById(R.id.btn_alighting);

        // DB 객체 생성
        database = RoomDB.getInstance(requireContext());

        // FusedLocationProviderClient 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        if (context == null) {
            context = requireContext();
        }

        // 하차 위치 가져오기
        getLastLocation();

        // 하차 시간 가져오기
        alightingTime = getLastTime();
        etAlightingTime.setText(alightingTime);

        // MainActivity 인스턴스 가져오기
        MainActivity mainActivity = (MainActivity) getActivity();

        // 메인 엑티비티 로고 변경
        if (mainActivity != null) {
            mainActivity.updateTextView("승차 > 하차 확인");
        }

        // 이전 Fragment에서 전달된 데이터 가져오기
        Bundle args = getArguments();
        if (args != null) {
            vehicleNumber = args.getString("vehicleNumber");
            boardingLocation = args.getString("boardingLocation");
            boardingTime = args.getString("boardingTime");

            etVehicleNum.setText(vehicleNumber);
            etBoardingTime.setText(boardingTime);
            etBoardingLocation.setText(boardingLocation);
        }

        btnAlighting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // MainActivity 인스턴스 가져오기
                MainActivity mainActivity = (MainActivity) getActivity();
                // RidingFragment 생성 및 설정

                boardingLocation = etBoardingLocation.getText().toString();
                boardingTime = etBoardingTime.getText().toString();
                vehicleNumber = etVehicleNum.getText().toString();
                boardingTime = etBoardingTime.getText().toString();
                alightingTime = etAlightingTime.getText().toString();

                long boardingTimestamp = convertTimeStringToTimestamp(boardingTime);
                long alightingTimestamp = convertTimeStringToTimestamp(alightingTime);

                Record record = new Record(boardingTimestamp, alightingTimestamp, vehicleNumber, boardingLocation, alightingLocation);

                // DB에 Record 저장
                database.recordDao().insert(record);

                // MainActivity에서 bottomNavigationView 가져오기
                BottomNavigationView bottomNavigationView = mainActivity.publicBottomNavigationView();

                // "Record" 아이템 선택
                bottomNavigationView.setSelectedItemId(R.id.action_record);
                Toast.makeText(context, "탑승기록 추가 완료", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void getLastLocation() {
        // 위치 권한 확인
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // 위치 권한이 허용된 경우
            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        // 위치 정보를 가져온 경우
                        alightingLocation = getAddressFromLocation(location);
                        etAlightingLocation.setText(alightingLocation);
                    } else {
                        // 위치 정보를 가져오지 못한 경우
                        etAlightingLocation.setText("위치를 가져오지 못했습니다.");
                    }
                }
            });
        } else {
            // 위치 권한이 허용되지 않은 경우
            requestLocationPermission();
        }
    }

    private void requestLocationPermission() {
        // 위치 권한 요청
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_REQUEST_LOCATION);
    }

    private String getAddressFromLocation(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                return address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    private String getLastTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date currentTime = new Date();
        return sdf.format(currentTime);
    }

    private long convertTimeStringToTimestamp(String timeString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date date = sdf.parse(timeString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}