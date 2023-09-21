package com.jeonghyeon.taxiproject.fragment;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.jeonghyeon.taxiproject.adapter.CommentAdapter;
import com.jeonghyeon.taxiproject.api.API;
import com.jeonghyeon.taxiproject.dto.request.CommentAddRequestDto;
import com.jeonghyeon.taxiproject.dto.request.CommentUpdateRequestDto;
import com.jeonghyeon.taxiproject.dto.request.PostAddRequestDto;
import com.jeonghyeon.taxiproject.dto.response.CommentResponseDto;
import com.jeonghyeon.taxiproject.dto.response.ResponseDto;
import com.jeonghyeon.taxiproject.token.TokenManager;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailPostFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView_arrival, mapView_departure;
    private GoogleMap googleMap_arrival, googleMap_departure;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;

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

    private EditText commentEditText;
    private Button postCommentButton;

    private boolean isEditingComment = false; // 댓글 수정 모드 여부를 나타내는 변수


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
        commentEditText = view.findViewById(R.id.commentEditText);
        postCommentButton = view.findViewById(R.id.postCommentButton);
        tokenManager = new TokenManager(requireContext());
        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);

        // FusedLocationProviderClient 초기화
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // 어댑터 초기화
        commentAdapter = new CommentAdapter(new ArrayList<>());
        recyclerView.setAdapter(commentAdapter);

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
        // SimpleDateFormat을 사용하여 날짜 및 시간을 원하는 형식으로 포맷
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시mm분", Locale.getDefault());
        try {
            Date fDepartureTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(departureTime);
            departureTimeTextView.setText(dateFormat.format(fDepartureTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
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

        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commentEditText.getText().toString().trim().isEmpty()) {
                    // 텍스트가 공백인 경우, 토스트 메시지 표시
                    showToast("댓글을 입력하세요");
                } else {
                    // 텍스트가 공백이 아닌 경우, 댓글을 추가하는 API 호출
                    addCommentAPI(postId, commentEditText.getText().toString());
                }
            }
        });

        // 댓글 수정 버튼 클릭 이벤트 처리
        commentAdapter.setOnCommentUpdateListener(new CommentAdapter.OnCommentUpdateListener() {
            @Override
            public void onUpdateComment(String content, Long commentId) {
                // 댓글 수정 버튼 클릭 시 호출될 메서드를 여기에 구현
                updateCommentAPI(content, commentId);
            }
        });

        // 댓글 수정 버튼 클릭 이벤트 처리
        commentAdapter.setOnCommentDeleteListener(new CommentAdapter.OnCommentDeleteListener() {
            @Override
            public void onDeleteComment(Long commentId) {
                deleteCommentAPI(commentId);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCommentsByPostIdAPI(postId);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        getCommentsByPostIdAPI(postId);

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

    private void getCommentsByPostIdAPI(Long postId) {
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
            Call<ResponseDto<List<CommentResponseDto>>> call = apiService.getCommentsByPostId("Bearer " + accessToken, postId);
            call.enqueue(new Callback<ResponseDto<List<CommentResponseDto>>>() {
                @Override
                public void onResponse(Call<ResponseDto<List<CommentResponseDto>>> call, Response<ResponseDto<List<CommentResponseDto>>> response) {
                    if (response.isSuccessful()) {
                        ResponseDto<List<CommentResponseDto>> responseDto = response.body();
                        int statusCode = responseDto.getStatus();

                        if (statusCode == 200) {
                            List<CommentResponseDto> commentResponseDtos = responseDto.getData();
                            // 정렬된 댓글 목록을 어댑터에 설정합니다.
                            commentAdapter.setData(commentResponseDtos);
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
                            String msg = responseDto.getMsg();
                            showToast(msg);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseDto<List<CommentResponseDto>>> call, Throwable t) {
                    showToast("api 호출 실패");
                }
            });
        }
    }

    private void addCommentAPI(Long postId, String content) {
        String accessToken = tokenManager.getAccessToken();
        CommentAddRequestDto requestDto = new CommentAddRequestDto(content);

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
            // Retrofit 객체 생성
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://121.200.87.205:8000/") // 스프링부트 API의 기본 URL을 설정
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // API 인터페이스 생성
            API apiService = retrofit.create(API.class);

            // API 호출
            Call<ResponseDto<Boolean>> call = apiService.addComment("Bearer " + accessToken, postId, requestDto);
            call.enqueue(new Callback<ResponseDto<Boolean>>() {
                @Override
                public void onResponse(Call<ResponseDto<Boolean>> call, Response<ResponseDto<Boolean>> response) {
                    if (response.isSuccessful()) {
                        ResponseDto<Boolean> responseDto = response.body();
                        int statusCode = responseDto.getStatus();
                        String msg = responseDto.getMsg();

                        if (statusCode == 200) {
                            showToast("댓글 작성 완료");
                            getCommentsByPostIdAPI(postId);
                            commentEditText.setText("");
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
                            showToast(msg);
                        } else {
                            showToast(msg);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseDto<Boolean>> call, Throwable t) {
                    showToast("API 호출 실패");
                }
            });
        }
    }

    // 댓글 수정
    private void updateCommentAPI(String content, Long commentId) {
        String accessToken = tokenManager.getAccessToken();
        CommentUpdateRequestDto requestDto = new CommentUpdateRequestDto(content);

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
            // Retrofit 객체 생성
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://121.200.87.205:8000/") // 스프링부트 API의 기본 URL을 설정
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // API 인터페이스 생성
            API apiService = retrofit.create(API.class);

            // API 호출
            Call<ResponseDto<Boolean>> call = apiService.updateComment("Bearer " + accessToken, requestDto, commentId);
            call.enqueue(new Callback<ResponseDto<Boolean>>() {
                @Override
                public void onResponse(Call<ResponseDto<Boolean>> call, Response<ResponseDto<Boolean>> response) {
                    if (response.isSuccessful()) {
                        ResponseDto<Boolean> responseDto = response.body();
                        int statusCode = responseDto.getStatus();
                        String msg = responseDto.getMsg();

                        if (statusCode == 200) {
                            showToast("댓글 수정 완료");
                            getCommentsByPostIdAPI(postId);
                            commentEditText.setText("");
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
                            showToast(msg);
                        } else {
                            showToast(msg);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseDto<Boolean>> call, Throwable t) {
                    showToast("API 호출 실패");
                }
            });
        }
    }

    // 댓글 수정
    private void deleteCommentAPI(Long commentId) {
        String accessToken = tokenManager.getAccessToken();
        CommentUpdateRequestDto requestDto = new CommentUpdateRequestDto(content);

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
            // Retrofit 객체 생성
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://121.200.87.205:8000/") // 스프링부트 API의 기본 URL을 설정
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // API 인터페이스 생성
            API apiService = retrofit.create(API.class);

            // API 호출
            Call<ResponseDto<Boolean>> call = apiService.deleteComment("Bearer " + accessToken, commentId);
            call.enqueue(new Callback<ResponseDto<Boolean>>() {
                @Override
                public void onResponse(Call<ResponseDto<Boolean>> call, Response<ResponseDto<Boolean>> response) {
                    if (response.isSuccessful()) {
                        ResponseDto<Boolean> responseDto = response.body();
                        int statusCode = responseDto.getStatus();
                        String msg = responseDto.getMsg();

                        if (statusCode == 200) {
                            showToast(msg);
                            getCommentsByPostIdAPI(postId);
                            commentEditText.setText("");
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
                            showToast(msg);
                        } else {
                            showToast(msg);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseDto<Boolean>> call, Throwable t) {
                    showToast("API 호출 실패");
                }
            });
        }
    }
}