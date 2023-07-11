package com.jeonghyeon.taxiproject.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.jeonghyeon.taxiproject.activity.MainActivity;
import com.jeonghyeon.taxiproject.api.API;
import com.jeonghyeon.taxiproject.dto.response.TaxiStandResponse;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TaxiStandFragment extends Fragment implements OnMapReadyCallback {

    // 지도 표시
    private MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ImageButton btnPosition;
    private EditText etPotion;

    public TaxiStandFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_taxi_stand, container, false);

        // 실시간 지도 표시
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        btnPosition = view.findViewById(R.id.positionButton);
        etPotion = view.findViewById(R.id.positionEditText);

        // FusedLocationProviderClient 초기화
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // MainActivity 인스턴스 가져오기
        MainActivity mainActivity = (MainActivity) getActivity();

        // 메인 엑티비티 로고 변경
        if (mainActivity != null) {
            mainActivity.updateTextView("택시승강장");
        }

        btnPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = etPotion.getText().toString();
                if (!address.isEmpty()) {
                    LatLng location = getLocationFromAddress(address);
                    if (location != null) {
                        // 내 위치로 표시되었던 마커 제거
                        googleMap.clear();
                        // 새로운 마커 추가
                        googleMap.addMarker(new MarkerOptions().position(location).title(address));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
                        // 스프링부트에서 받아온 데이터로 표시한 마커 유지
                        fetchDataFromAPI();
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

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
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // 위치 권한이 허용되었는지 확인
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true); // 내 위치를 블루 닷으로 표시

            // 현재 위치로 지도 이동
            getCurrentLocation();

            // API에서 데이터를 가져와서 지도에 마커를 추가
            fetchDataFromAPI();
        } else {
            // 위치 권한을 요청합니다.
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            // 현재 위치로 지도 이동
                            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
                        }
                    });
        } else {
            // 위치 권한이 없는 경우 권한 요청
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    // 위치 권한 요청 결과 처리
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {

            }
        }
    }

    private LatLng getLocationFromAddress(String strAddress) {
        Geocoder geocoder = new Geocoder(requireContext());
        List<Address> addressList;
        LatLng latLng = null;

        try {
            addressList = geocoder.getFromLocationName(strAddress, 1);
            if (addressList != null && !addressList.isEmpty()) {
                Address address = addressList.get(0);
                latLng = new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return latLng;
    }

    private void fetchDataFromAPI() {
        // Retrofit 객체 생성
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.85.20:8000/") // 스프링부트 API의 기본 URL을 설정
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // API 인터페이스 생성
        API apiService = retrofit.create(API.class);

        // API 호출
        Call<List<TaxiStandResponse>> call = apiService.findAllTaxiStand();
        call.enqueue(new Callback<List<TaxiStandResponse>>() {
            @Override
            public void onResponse(Call<List<TaxiStandResponse>> call, Response<List<TaxiStandResponse>> response) {
                if (response.isSuccessful()) {
                    List<TaxiStandResponse> taxiStands = response.body();

                    for (TaxiStandResponse taxiStand : taxiStands) {
                        // 지도에 마커 추가
                        double latitude = Double.parseDouble(String.valueOf(taxiStand.getLatitude()));
                        double longitude = Double.parseDouble(String.valueOf(taxiStand.getLongitude()));
                        LatLng position = new LatLng(latitude, longitude);

                        // 마커 이미지 설정
                        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.taxistand);
                        googleMap.addMarker(new MarkerOptions().position(position).title("Taxi Stand").icon(icon));
                    }
                } else {
                    // API 호출이 실패한 경우에 대한 처리 작성
                }
            }

            @Override
            public void onFailure(Call<List<TaxiStandResponse>> call, Throwable t) {
                // API 호출이 실패한 경우에 대한 처리 작성
            }
        });
    }
}