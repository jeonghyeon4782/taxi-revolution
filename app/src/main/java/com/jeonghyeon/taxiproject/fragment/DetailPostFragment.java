package com.jeonghyeon.taxiproject.fragment;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jeonghyeon.taxiproject.R;
import com.jeonghyeon.taxiproject.activity.MainActivity;
import com.jeonghyeon.taxiproject.api.API;
import com.jeonghyeon.taxiproject.dto.response.BelongPostResponseDto;
import com.jeonghyeon.taxiproject.dto.response.ResponseDto;
import com.jeonghyeon.taxiproject.token.TokenManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailPostFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView_arrival, mapView_departure;
    private GoogleMap googleMap_arrival, googleMap_departure;
    private FusedLocationProviderClient fusedLocationProviderClient;

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
    private Button btnEnter;
    private TokenManager tokenManager;


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
        btnEnter = view.findViewById(R.id.btn_Enter);
        tokenManager = new TokenManager(requireContext());

        // FusedLocationProviderClient 초기화
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        mapView_arrival = view.findViewById(R.id.map_arrival);
        mapView_arrival.onCreate(savedInstanceState);
        mapView_arrival.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap_arrival = googleMap;
                DetailPostFragment.this.onMapReady(googleMap); // onMapReady 호출
            }
        });

        mapView_departure = view.findViewById(R.id.map_departure);
        mapView_departure.onCreate(savedInstanceState);
        mapView_departure.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap_departure = googleMap;
                DetailPostFragment.this.onMapReady(googleMap); // onMapReady 호출
            }
        });

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

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBelongAPI();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView_arrival.onResume();
        mapView_departure.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView_arrival.onPause();
        mapView_departure.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView_arrival.onDestroy();
        mapView_departure.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView_arrival.onLowMemory();
        mapView_arrival.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (map == googleMap_arrival) {
            LatLng destinationLatLng = getLocationFromAddress(destinationLocation);

            if (destinationLatLng != null) {
                map.addMarker(new MarkerOptions()
                        .position(destinationLatLng)
                        .title("도착지"));

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLatLng, 16));
            } else {
                showToast("도착지 주소를 찾을 수 없습니다.");
            }
        } else if (map == googleMap_departure) {
            LatLng departureLatLng = getLocationFromAddress(departureLocation);

            if (departureLatLng != null) {
                map.addMarker(new MarkerOptions()
                        .position(departureLatLng)
                        .title("출발지"));

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(departureLatLng, 16));
            } else {
                showToast("출발지 주소를 찾을 수 없습니다.");
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

    // toast 보여주기
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void addBelongAPI() {
        String accessToken = tokenManager.getAccessToken();
        if (accessToken == null) {
            MainActivity mainActivity = (MainActivity) getActivity();
            LoginFragment loginFragment = new LoginFragment();
            if (mainActivity != null) {
                mainActivity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.containers, loginFragment)
                        .addToBackStack(null)
                        .commit();
            }
            showToast("로그인이 필요합니다");
        } else {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://121.200.87.205:8000/") // 스프링부트 API의 기본 URL을 설정
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            API apiService = retrofit.create(API.class);
            Call<ResponseDto<Boolean>> call = apiService.addBelong("Bearer " + accessToken, postId);
            call.enqueue(new Callback<ResponseDto<Boolean>>() {
                @Override
                public void onResponse(Call<ResponseDto<Boolean>> call, Response<ResponseDto<Boolean>> response) {
                    if (response.isSuccessful()) {
                        ResponseDto<Boolean> responseDto = response.body();
                        int statusCode = responseDto.getStatus();
                        String msg = responseDto.getMsg();
                        if (statusCode == 200) {
                            MainActivity mainActivity = (MainActivity) getActivity();
                            BelongFragment belongFragment = new BelongFragment();
                            if (mainActivity != null) {
                                mainActivity.getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.containers, belongFragment)
                                        .addToBackStack(null)
                                        .commit();
                            }
                         showToast(msg);
                        } else if (statusCode == 423) { // 만료된 토큰이라면?
                            MainActivity mainActivity = (MainActivity) getActivity();
                            LoginFragment loginFragment = new LoginFragment();
                            if (mainActivity != null) {
                                mainActivity.getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.containers, loginFragment)
                                        .addToBackStack(null)
                                        .commit();
                            }
                            showToast("로그인이 필요합니다");
                        } else {
                            showToast(msg);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseDto<Boolean>> call, Throwable t) {
                    showToast("api 호출 실패");
                }
            });
        }
    }
}