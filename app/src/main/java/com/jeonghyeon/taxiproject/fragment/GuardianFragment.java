package com.jeonghyeon.taxiproject.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.jeonghyeon.taxiproject.R;
import com.jeonghyeon.taxiproject.RoomDB;
import com.jeonghyeon.taxiproject.activity.MainActivity;
import com.jeonghyeon.taxiproject.adapter.GuardianAdapter;
import com.jeonghyeon.taxiproject.model.Frequency;
import com.jeonghyeon.taxiproject.model.Guardian;

import java.util.ArrayList;
import java.util.List;

public class GuardianFragment extends Fragment {

    // 권한
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQUEST_SMS_PERMISSION = 2;
    private static long LOCATION_UPDATE_INTERVAL; // 1000은 1초 60000은 1분

    // 화면 요소
    private EditText etGuardianNum;
    private Button btnAddGuardian, btnSendSms;
    private RecyclerView recyclerView;
    private TextView etNumber;
    private TextView etNumber2;
    private Button btnIncrease;
    private Button btnDecrease;
    
    // 보호자 번호 관련 요소
    private List<Guardian> guardians = new ArrayList<>();

    // 문자 주기 관련
    private Frequency frequency;
    
    // RoomDB 선언
    private RoomDB database;

    private GuardianAdapter adapter;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    
    // true/false 요소
    private boolean isUpdatingLocation = false;
    private boolean isFirstLocationUpdate = true;

    public GuardianFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // 뷰 변수 초기화
        View view = inflater.inflate(R.layout.fragment_guardian, container, false);

        // 뷰 요소 초기화
        etGuardianNum = view.findViewById(R.id.et_guardianNum);
        btnAddGuardian = view.findViewById(R.id.btn_addGuardian);
        btnSendSms = view.findViewById(R.id.btn_sendSms);
        recyclerView = view.findViewById(R.id.recycler_view);
        etNumber = view.findViewById(R.id.et_number);
        etNumber2 = view.findViewById(R.id.et_number2);
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
            etNumber2.setText("5");
        } else {
            etNumber2.setText(frequency.getFrequencyNum());
            LOCATION_UPDATE_INTERVAL = Integer.parseInt(frequency.getFrequencyNum()) * 60000;
        }

        // + 버튼 클릭 시
        btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = Integer.parseInt(etNumber2.getText().toString());
                if (value >= 10) {
                    showToast("문자 주기의 최대 시간은 10분 입니다.");
                } else {
                    value += 1;
                    frequency.setFrequencyNum(String.valueOf(value));
                    LOCATION_UPDATE_INTERVAL += 60000;
                    etNumber2.setText(String.valueOf(value));

                    // DB에 업데이트
                    database.frequencyDao().update(frequency);
                }
            }
        });

        // - 버튼 클릭 시
        btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = Integer.parseInt(etNumber2.getText().toString());
                if (value <= 1) {
                    showToast("문자 주기의 최소 시간은 1분 입니다.");
                } else {
                    value -= 1;
                    frequency.setFrequencyNum(String.valueOf(value));
                    LOCATION_UPDATE_INTERVAL -= 60000;
                    etNumber2.setText(String.valueOf(value));

                    // DB에 업데이트
                    database.frequencyDao().update(frequency);
                }
            }
        });

        // 추가 버튼을 클릭 시
        btnAddGuardian.setOnClickListener(v -> {
            String guardianNum = etGuardianNum.getText().toString().trim();

            // guardianNum이 공백인 경우
            if (guardianNum.isEmpty()) {
                showToast("보호자 번호를 입력하세요.");
            }
            // guardianNum이 13자리가 아닌 경우
            else if (guardianNum.length() != 11) {
                showToast("보호자 번호는 11자리여야 합니다.");
            }
            // guardianNum이 숫자가 아닌 문자를 포함하는 경우
            else if (!guardianNum.matches("\\d+")) {
                showToast("보호자 번호는 숫자로만 입력해야 합니다.");
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
            }
        });

        // 문자 전송 버튼 클릭 시
        btnSendSms.setOnClickListener(v -> {
            if (isUpdatingLocation) {
                stopLocationUpdates();
                isFirstLocationUpdate = true;
                btnSendSms.setText("문자 전송");
            } else {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // 위치 권한이 없는 경우
                    showToast("위치 권한이 필요합니다.");
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                } else if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    // SMS 권한이 없는 경우
                    showToast("SMS 권한이 필요합니다.");
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.SEND_SMS}, REQUEST_SMS_PERMISSION);
                } else {
                    if (guardians.isEmpty()) {
                        showToast("보호자 번호를 추가해야 합니다.");
                    } else {
                        startLocationUpdates();
                        btnSendSms.setText("전송 종료");
                    }
                }
            }
        });

        // 위치 업데이트를 시작하기 위한 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    // 만약 첫 위치 업데이트라면?
                    if (isFirstLocationUpdate) {
                        sendFirstLocationSms(latitude, longitude);
                        isFirstLocationUpdate = false;
                    } else {
                        sendSmsWithLocation(latitude, longitude);
                    }
                }
            }
        };
        return view;
    }

    // 위치 업데이트를 시작하기 전에 필요한 권한을 확인
    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show explanation to the user if location permission is denied.
            }
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.SEND_SMS}, REQUEST_SMS_PERMISSION);
        } else {
            requestLocationUpdates();
        }
    }

    // 위치 업데이트 중지
    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
        isUpdatingLocation = false;

        LastLocationSms();
    }

    // 위치 업데이트 시작
    private void requestLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            isUpdatingLocation = true;
        } catch (SecurityException e) {
            e.printStackTrace();
            showToast("위치 권한이 거부되어 위치 업데이트를 시작할 수 없습니다.");
        }
    }

    // 마지막 위치 업데이트 
    private void LastLocationSms() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        sendLastLocationSms(latitude, longitude);
                    }
                })
                .addOnFailureListener(e -> {
                    // Failed to obtain location information
                });
    }

    // 마지막 문자 전송
    private void sendLastLocationSms(double latitude, double longitude) {
        String message = "하차 완료했습니다.";
        String address = "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;
        for (Guardian guardian : guardians) {
            String phoneNumber = guardian.getGuardianNum();
            SmsManager.getDefault().sendTextMessage(phoneNumber, null, message, null, null);
            SmsManager.getDefault().sendTextMessage(phoneNumber, null, address, null, null);
        }
        showToast(String.valueOf(LOCATION_UPDATE_INTERVAL));
//        showToast("하차 문자 메시지 전송 완료");
    }

    // 첫 문자 전송
    private void sendFirstLocationSms(double latitude, double longitude) {
        String message = "탑승 완료했습니다.";
        String address = "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;
        for (Guardian guardian : guardians) {
            String phoneNumber = guardian.getGuardianNum();
            SmsManager.getDefault().sendTextMessage(phoneNumber, null, message, null, null);
            SmsManager.getDefault().sendTextMessage(phoneNumber, null, address, null, null);
        }
        showToast(String.valueOf(LOCATION_UPDATE_INTERVAL));
//        showToast("탑승 문자 메시지 전송 완료");
    }

    // 위치 정보 업데이트
    private void sendSmsWithLocation(double latitude, double longitude) {
        String message = "현재 위치입니다";
        String address = "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;
        for (Guardian guardian : guardians) {
            String phoneNumber = guardian.getGuardianNum();
            SmsManager.getDefault().sendTextMessage(phoneNumber, null, address, null, null);
        }
        showToast(String.valueOf(LOCATION_UPDATE_INTERVAL));
//        showToast("문자 메시지 전송 완료");
    }

    // toast 보여주기
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    // 권한 요청 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                handleLocationPermissionResult();
            } else {
                showToast("위치 권한이 거부되어 위치 업데이트를 시작할 수 없습니다.");
            }
        } else if (requestCode == REQUEST_SMS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                handleSmsPermissionResult();
            } else {
                showToast("SMS 권한이 거부되어 SMS를 보낼 수 없습니다.");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // 메서드와 권한 결과에 따라 처리
    private void handleLocationPermissionResult() {
        requestLocationUpdates();
    }

    private void handleSmsPermissionResult() {
        if (isUpdatingLocation) {
            stopLocationUpdates();
            btnSendSms.setText("문자 전송");
        } else {
            startLocationUpdates();
            btnSendSms.setText("전송 종료");
        }
    }

}