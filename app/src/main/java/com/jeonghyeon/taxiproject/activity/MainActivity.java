package com.jeonghyeon.taxiproject.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jeonghyeon.taxiproject.R;
import com.jeonghyeon.taxiproject.RoomDB;
import com.jeonghyeon.taxiproject.adapter.GuardianAdapter;
import com.jeonghyeon.taxiproject.model.Guardian;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView bottomNavigationView;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQUEST_SMS_PERMISSION = 2;
    private static final long LOCATION_UPDATE_INTERVAL = 10000; // 10 seconds

    EditText etGuardianNum;
    Button btnAddGuardian, btnSendSms;
    RecyclerView recyclerView;
    List<Guardian> guardians = new ArrayList<>();
    RoomDB database;
    GuardianAdapter adapter;

    FusedLocationProviderClient fusedLocationClient;
    LocationCallback locationCallback;
    boolean isUpdatingLocation = false;
    boolean isFirstLocationUpdate = true; // 첫 번째 위치 업데이트 여부

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        etGuardianNum = findViewById(R.id.et_guardianNum);
        btnAddGuardian = findViewById(R.id.btn_addGuardian);
        btnSendSms = findViewById(R.id.btn_sendSms);
        recyclerView = findViewById(R.id.recycler_view);

        database = RoomDB.getInstance(this);
        guardians = database.guardianDao().getAll();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GuardianAdapter(MainActivity.this, guardians);
        recyclerView.setAdapter(adapter);

        btnAddGuardian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String guardianNum = etGuardianNum.getText().toString().trim();
                if (!guardianNum.equals("")) {
                    Guardian data = new Guardian();
                    data.setGuardianNum(guardianNum);
                    database.guardianDao().insert(data);

                    etGuardianNum.setText("");

                    guardians.clear();
                    guardians.addAll(database.guardianDao().getAll());
                    adapter.notifyDataSetChanged();
                }
            }
        });

        btnSendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUpdatingLocation) {
                    stopLocationUpdates();
                    isFirstLocationUpdate = true;
                    btnSendSms.setText("문자 전송");
                } else {
                    startLocationUpdates();
                    btnSendSms.setText("전송 종료");
                }
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    if (isFirstLocationUpdate) {
                        sendFirstLocationSms(latitude, longitude);
                        isFirstLocationUpdate = false;
                    } else {
                        sendSmsWithLocation(latitude, longitude);
                    }
                }
            }
        };
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // 위치 권한이 거부되었을 때 사용자에게 설명을 보여줄 수 있습니다.
                // 필요에 따라 사용자에게 권한이 필요한 이유를 설명하는 다이얼로그 또는 메시지를 표시할 수 있습니다.
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }

        // SMS 권한을 확인하고 요청합니다.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SMS_PERMISSION);
        } else {
            requestLocationUpdates();
        }
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
        isUpdatingLocation = false;

        LastLocationSms();
    }

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

    private void LastLocationSms() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 위치 권한이 없는 경우 처리
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            sendLastLocationSms(latitude, longitude);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 위치 정보를 가져올 수 없는 경우 처리
                    }
                });
    }

    private void sendLastLocationSms(double latitude, double longitude) {
        String message = "하차 완료했습니다.";
        String address = "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;
        for (Guardian guardian : guardians) {
            String phoneNumber = guardian.getGuardianNum();
            SmsManager.getDefault().sendTextMessage(phoneNumber, null, message, null, null);
            SmsManager.getDefault().sendTextMessage(phoneNumber, null, address, null, null);
        }
        showToast("하차 문자 메시지 전송 완료");
    }

    private void sendFirstLocationSms(double latitude, double longitude) {
        String message = "탑승 완료했습니다.";
        String address = "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;
        for (Guardian guardian : guardians) {
            String phoneNumber = guardian.getGuardianNum();
            SmsManager.getDefault().sendTextMessage(phoneNumber, null, message, null, null);
            SmsManager.getDefault().sendTextMessage(phoneNumber, null, address, null, null);
        }
        showToast("탑승 문자 메시지 전송 완료");
    }

    private void sendSmsWithLocation(double latitude, double longitude) {
        String message = "현재 위치입니다";
        String address = "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;
        for (Guardian guardian : guardians) {
            String phoneNumber = guardian.getGuardianNum();
            SmsManager.getDefault().sendTextMessage(phoneNumber, null, address, null, null);
        }
        showToast("문자 메시지 전송 완료");
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

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

    // 하단 내비게이션바
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_record:
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                break;
//            case R.id.action_message:
//                startActivity(new Intent(MainActivity.this, SearchActivity.class));
//                break;
            case R.id.action_taxi:
                startActivity(new Intent(MainActivity.this, RecognitionActivity.class));
                break;
//            case R.id.action_taxiStop:
//                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
//                break;
//            case R.id.action_settings:
//                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
//                break;
        }

        return true;
    }
}