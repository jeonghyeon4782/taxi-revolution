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
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jeonghyeon.taxiproject.R;
import com.jeonghyeon.taxiproject.roomDB.RoomDB;
import com.jeonghyeon.taxiproject.activity.MainActivity;
import com.jeonghyeon.taxiproject.domain.Frequency;
import com.jeonghyeon.taxiproject.domain.Guardian;

import java.util.ArrayList;
import java.util.List;

public class RidingFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener{

    // 위치, SMS, 문자주기
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQUEST_SMS_PERMISSION = 2;
    private static long LOCATION_UPDATE_INTERVAL = 0; // 1000은 1초 60000은 1분

    // 지도 표시
    private MapView mapView;
    private GoogleMap googleMap;

    // 가장 최근에 업데이트한 위도, 경도 정보
    private double cLatitude; // 위도
    private double cLongitude; // 경도

    // SOS 버튼
    Button police_btn,fire_btn,sos_btn;
    int count = 0;


    private Button btnStartStop;
    // 보호자 번호 관련 요소
    private List<Guardian> guardians = new ArrayList<>();
    // 문자 주기 관련
    private Frequency frequency;
    // RoomDB 선언
    private RoomDB database;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    // true/false 요소
    private boolean isUpdatingLocation = false;
    private boolean isFirstLocationUpdate = true;

    // 데이터를 받고 전달할 변수
    private String vehicleNumber, boardingTime, boardingLocation;

    public RidingFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // 프래그먼트를 위한 레이아웃을 인플레이션합니다.
        View view = inflater.inflate(R.layout.fragment_riding, container, false);

        // 실시간 지도 표시
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        btnStartStop = view.findViewById(R.id.btn_startAndStop);
        // DB 객체 생성
        database = RoomDB.getInstance(requireContext());
        // 모든 보호자 번호 가져오기
        guardians = database.guardianDao().getAll();
        // 문자 주기 객체 초기화
        frequency = database.frequencyDao().getFrequency();

        // 문자주기 DB에서 가져오기
        LOCATION_UPDATE_INTERVAL = Integer.parseInt(frequency.getFrequencyNum()) * 60000 * 6;

        // MainActivity 인스턴스 가져오기
        MainActivity mainActivity = (MainActivity) getActivity();

        // 메인 엑티비티 로고 변경
        if (mainActivity != null) {
            mainActivity.updateTextView("승차 > 승차중");
        }

        //SOS 버튼 생성 및 이벤트작성
        police_btn =(Button)view.findViewById(R.id.police_btn);
        fire_btn =(Button)view.findViewById(R.id.fire_btn);
        police_btn.setVisibility(View.INVISIBLE);
        fire_btn.setVisibility(View.INVISIBLE);
        sos_btn =(Button)view.findViewById(R.id.sos_btn);
        police_btn =(Button)view.findViewById(R.id.police_btn);

        sos_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count == 0) {
                    police_btn.setVisibility(View.VISIBLE);
                    fire_btn.setVisibility(View.VISIBLE);
                    count=1;
                }
                else{
                    police_btn.setVisibility(View.INVISIBLE);
                    fire_btn.setVisibility(View.INVISIBLE);
                    count=0;
                }
            }
        });

        police_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "경찰이 필요한 긴급상황입니다. 차량번호 : " + vehicleNumber;
                String address = "https://www.google.com/maps/search/?api=1&query=" + cLatitude + "," + cLongitude;
                for (Guardian guardian : guardians) {
                    String phoneNumber = guardian.getGuardianNum();
                    SmsManager.getDefault().sendTextMessage(phoneNumber, null, message, null, null);
                    SmsManager.getDefault().sendTextMessage(phoneNumber, null, address, null, null);
                }
                SmsManager.getDefault().sendTextMessage("01062084786", null, message, null, null);
                SmsManager.getDefault().sendTextMessage("01062084786", null, address, null, null);
                showToast("긴급 메시지 전송 완료");
            }
        });

        fire_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "소방관이 필요한 긴급상황입니다. 차량번호 : " + vehicleNumber;
                String address = "https://www.google.com/maps/search/?api=1&query=" + cLatitude + "," + cLongitude;
                for (Guardian guardian : guardians) {
                    String phoneNumber = guardian.getGuardianNum();
                    SmsManager.getDefault().sendTextMessage(phoneNumber, null, message, null, null);
                    SmsManager.getDefault().sendTextMessage(phoneNumber, null, address, null, null);
                }
                SmsManager.getDefault().sendTextMessage("01062084786", null, message, null, null);
                SmsManager.getDefault().sendTextMessage("01062084786", null, address, null, null);
                showToast("긴급 메시지 전송 완료");
            }
        });

        // 이전 Fragment에서 전달된 데이터 가져오기
        Bundle args = getArguments();
        if (args != null) {
            vehicleNumber = args.getString("vehicleNumber");
            boardingLocation = args.getString("boardingLocation");
            boardingTime = args.getString("boardingTime");
        }

        // 문자 전송 버튼 클릭 시
        btnStartStop.setOnClickListener(v -> {
            // 하차 버튼 클릭 시
            if (isUpdatingLocation) {
                // RidingFragment 생성 및 설정
                AlightingCheckFragment alightingCheckFragment = new AlightingCheckFragment();

                Bundle arg = new Bundle();
                arg.putString("vehicleNumber", vehicleNumber);
                arg.putString("boardingLocation", boardingLocation);
                arg.putString("boardingTime", boardingTime);
                alightingCheckFragment.setArguments(arg);

                if (mainActivity != null) {
                    // MainActivity의 프래그먼트 매니저를 사용하여 RidingCheckFragment를 containers에 추가
                    mainActivity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.containers, alightingCheckFragment)
                            .addToBackStack(null)
                            .commit();
                }

                stopLocationUpdates();
                isUpdatingLocation = false;
                isFirstLocationUpdate = true;
                btnStartStop.setText("문자 전송");
            } else {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // 위치 권한이 없는 경우
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                } else if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    // SMS 권한이 없는 경우
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.SEND_SMS}, REQUEST_SMS_PERMISSION);
                } else {
                    if (guardians.isEmpty()) {
                        showToast("보호자 번호를 추가해야 합니다.");
                    } else {
                        // 전송 버튼 클릭 시
                        startLocationUpdates();
                        isUpdatingLocation = true;
                        btnStartStop.setText("하차");
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
                    isFirstLocationUpdate = false;
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        cLatitude = latitude;
                        cLongitude = longitude;
                        sendSmsWithLocation(latitude, longitude);
                    }
            }
        };
        return view;
    }

    // 위치 업데이트 시작
    private void startLocationUpdates() {

        fusedLocationClient.removeLocationUpdates(locationCallback);

        // 위치 권한이 있는지 확인합니다.
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                // 위치 권한이 거부된 경우 사용자에게 설명을 표시할 수 있습니다.
            }
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
        // SMS 권한이 있는지 확인합니다.
        else if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.SEND_SMS}, REQUEST_SMS_PERMISSION);
        } else {
            if (guardians.isEmpty()) {
                showToast("보호자 번호를 추가해야 합니다.");
            } else {
                requestLocationUpdates();
                btnStartStop.setText("하차");
            }
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
        LocationRequest locationRequest1 = locationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
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
        showToast("하차 문자 전송 완료");
    }

    // 주기적 문자 전송
    private void sendSmsWithLocation(double latitude, double longitude) {
        String message = "현재 위치입니다. 탑승한 차량 번호 :  " + vehicleNumber;
        String address = "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;
        for (Guardian guardian : guardians) {
            String phoneNumber = guardian.getGuardianNum();
            SmsManager.getDefault().sendTextMessage(phoneNumber, null, message, null, null);
            SmsManager.getDefault().sendTextMessage(phoneNumber, null, address, null, null);
        }
        showToast("위치 전송 완료");
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
            btnStartStop.setText("전송 시작");
        } else {
            startLocationUpdates();
            btnStartStop.setText("하차");
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setOnCameraIdleListener(this);

        // 내 위치 활성화
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);

            // 파란 점(bluedot) 이미지 설정
            BitmapDescriptor dotIcon = BitmapDescriptorFactory.fromResource(R.drawable.taximarker);

            // 파란 점(bluedot) 마커 생성
            googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    // 기존 파란 점 마커 제거
                    googleMap.clear();

                    // 파란 점(bluedot) 마커 생성
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(new LatLng(location.getLatitude(), location.getLongitude()))
                            .icon(dotIcon)
                            .anchor(0.5f, 0.5f); // 마커의 앵커 포인트를 중앙으로 설정

                    // 마커 추가
                    googleMap.addMarker(markerOptions);
                }
            });

            // 현재 위치로 이동
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    LatLng currentLatLng = new LatLng(latitude, longitude);
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();


        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {

            @Override
            public void handleOnBackPressed() {
                if (isUpdatingLocation) {
                    fusedLocationClient.removeLocationUpdates(locationCallback);
                    isUpdatingLocation = false;
                }
                navigateBack();
            }
        });
    }

    // 이전 프래그먼트로 이동하는 메서드
    public void navigateBack() {
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.popBackStack();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onCameraIdle() {
        LatLng center = googleMap.getCameraPosition().target;
    }

}