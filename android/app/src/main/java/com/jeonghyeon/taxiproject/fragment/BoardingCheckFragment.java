package com.jeonghyeon.taxiproject.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.jeonghyeon.taxiproject.R;
import com.jeonghyeon.taxiproject.activity.MainActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BoardingCheckFragment extends Fragment {
    private Button riding;
    private EditText etVehicleNum, etBoardingTime, etBoardingLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int PERMISSION_REQUEST_LOCATION = 123;

    private String vehicleNumber, boardingTime, boardingLocation;

    public BoardingCheckFragment() {
        // 필수 생성자
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_boarding_check, container, false);

        // 버튼 초기화
        riding = view.findViewById(R.id.btn_boarding);

        // EditText 초기화
        etVehicleNum = view.findViewById(R.id.et_vehicleNum);
        etBoardingTime = view.findViewById(R.id.et_boardingTime);
        etBoardingLocation = view.findViewById(R.id.et_boardingLocation);

        // FusedLocationProviderClient 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // 현재 위치 가져오기
        getCurrentLocation();

        // 현재 시간 가져오기
        boardingTime = getCurrentTime();
        etBoardingTime.setText(boardingTime);

        // MainActivity 인스턴스 가져오기
        MainActivity mainActivity = (MainActivity) getActivity();

        // 메인 엑티비티 로고 변경
        if (mainActivity != null) {
            mainActivity.updateTextView("승차 > 승차 확인");
        }

        // 이전 Fragment에서 전달된 데이터 가져오기
        Bundle args = getArguments();
        if (args != null) {
            vehicleNumber = args.getString("vehicleNumber");
            etVehicleNum.setText(vehicleNumber);
        }

        riding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // MainActivity 인스턴스 가져오기
                MainActivity mainActivity = (MainActivity) getActivity();

                // RidingFragment 생성 및 설정
                RidingFragment ridingFragment = new RidingFragment();

                vehicleNumber = etVehicleNum.getText().toString();
                boardingLocation = etBoardingLocation.getText().toString();
                boardingTime = etBoardingTime.getText().toString();

                Bundle args = new Bundle();
                args.putString("vehicleNumber", vehicleNumber);
                args.putString("boardingLocation", boardingLocation);
                args.putString("boardingTime", boardingTime);
                ridingFragment.setArguments(args);

                if (mainActivity != null) {
                    // MainActivity의 프래그먼트 매니저를 사용하여 RidingCheckFragment를 containers에 추가
                    mainActivity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.containers, ridingFragment)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        return view;
    }

    private void getCurrentLocation() {
        // 위치 권한 확인
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // 위치 권한이 허용된 경우
            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        // 위치 정보를 가져온 경우
                        boardingLocation = getAddressFromLocation(location);
                        etBoardingLocation.setText(boardingLocation);
                    } else {
                        // 위치 정보를 가져오지 못한 경우
                        etBoardingLocation.setText("위치 검색 실패! 직접 입력해 주세요.");
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

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date currentTime = new Date();
        return sdf.format(currentTime);
    }


}