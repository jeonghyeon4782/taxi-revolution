package com.jeonghyeon.taxiproject.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jeonghyeon.taxiproject.R;
import com.jeonghyeon.taxiproject.activity.MainActivity;
import com.jeonghyeon.taxiproject.adapter.PostAdapter;
import com.jeonghyeon.taxiproject.api.API;
import com.jeonghyeon.taxiproject.dto.response.PostResponseDto;
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

public class NoticeBoardFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private TokenManager tokenManager; // TokenManager 객체 추가
    private EditText etEt1, etEt2;
    private ImageButton btnBtn1, btnBtn2;
    private Button btn_distancesort, btn_departureTime, btn_new, btn_distance; // 정렬 버튼 , 출발시간순 , 최신순 , 거리순
    private int dcount = 0;
    private Button btnWrite, btnMyPost;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final float MAX_DISTANCE = 1000; // 1km

    public NoticeBoardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice_board, container, false);

        etEt1 = view.findViewById(R.id.et_departure);
        etEt2 = view.findViewById(R.id.et_arrival);
        btnBtn1 = view.findViewById(R.id.btn_departure);
        btnBtn2 = view.findViewById(R.id.btn_arrival);
        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.refresh_layout);

        tokenManager = new TokenManager(requireContext());
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter = new PostAdapter();
        recyclerView.setAdapter(postAdapter);
        btn_distancesort = (Button) view.findViewById(R.id.btn_distancesort);
        btn_departureTime = (Button) view.findViewById(R.id.btn_departureTime);
        btn_new = (Button) view.findViewById(R.id.btn_new);
        btn_distance = (Button) view.findViewById(R.id.btn_distance);
        btnWrite = view.findViewById(R.id.btn_write);
        btnMyPost = view.findViewById(R.id.btn_mypost);


        btn_departureTime.setVisibility(View.INVISIBLE);
        btn_new.setVisibility(View.INVISIBLE);
        btn_distance.setVisibility(View.INVISIBLE);

        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.updateTextView("카풀");
        etEt1.setText(null);
        etEt2.setText(null);

        if (!isAccessTokenAvailable()) {
            btnMyPost.setVisibility(View.GONE);
            btnWrite.setVisibility(View.GONE);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchPosts();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        postAdapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(PostResponseDto post) {
                // Handle click event here
                openDetailPostFragment(post);
            }
        });

        btnMyPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                MyPostFragment myPostFragment = new MyPostFragment();
                if (mainActivity != null) {
                    mainActivity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.containers, myPostFragment)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();

                // RidingFragment 생성 및 설정
                AddPostFragment addPostFragment = new AddPostFragment();

                if (mainActivity != null) {
                    // MainActivity의 프래그먼트 매니저를 사용하여 RidingCheckFragment를 containers에 추가
                    mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.containers, addPostFragment).addToBackStack(null).commit();
                }
            }
        });

        btn_distancesort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dcount == 0) {
                    btn_departureTime.setVisibility(View.VISIBLE);
                    btn_new.setVisibility(View.VISIBLE);
                    btn_distance.setVisibility(View.VISIBLE);
                    dcount = 1;
                } else {
                    btn_departureTime.setVisibility(View.INVISIBLE);
                    btn_new.setVisibility(View.INVISIBLE);
                    btn_distance.setVisibility(View.INVISIBLE);
                    dcount = 0;
                }
            }
        });

        //      최신순 버튼 클릭시
        btn_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_distancesort.setText(btn_new.getText());
                btn_departureTime.setVisibility(View.INVISIBLE);
                btn_new.setVisibility(View.INVISIBLE);
                btn_distance.setVisibility(View.INVISIBLE);
                dcount = 0;
                sortByCreateTimeInRecyclerView();
            }
        });

        // 거리순 버튼 클릭시
        btn_distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_distancesort.setText(btn_distance.getText());
                btn_departureTime.setVisibility(View.INVISIBLE);
                btn_new.setVisibility(View.INVISIBLE);
                btn_distance.setVisibility(View.INVISIBLE);
                dcount = 0;

                // 위치 정보를 가져와서 RecyclerView를 거리순으로 정렬
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (lastKnownLocation != null) {
                        sortByDistanceInRecyclerView(lastKnownLocation);
                    } else {
                        showToast("위치 정보를 가져올 수 없습니다. GPS를 활성화해주세요.");
                    }
                } else {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }
        });

        //     출발시간순 버튼 클릭시
        btn_departureTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_distancesort.setText(btn_departureTime.getText());
                btn_departureTime.setVisibility(View.INVISIBLE);
                btn_new.setVisibility(View.INVISIBLE);
                btn_distance.setVisibility(View.INVISIBLE);
                dcount = 0;
                sortByDepartureTimeInRecyclerView();
            }
        });

        fetchPosts();

        // btnBtn1 버튼 클릭 시
        btnBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredLocation = etEt1.getText().toString();
                if (!enteredLocation.isEmpty()) {
                    // 지오코딩 작업 수행하여 위도와 경도 얻기
                    Geocoder geocoder = new Geocoder(requireContext());
                    try {
                        List<Address> addresses = geocoder.getFromLocationName(enteredLocation, 1);
                        if (!addresses.isEmpty()) {
                            Address address = addresses.get(0);
                            double latitude = address.getLatitude();
                            double longitude = address.getLongitude();
                            filterPostsByDepartureLocation1(latitude, longitude);
                            sortByCreateTimeInRecyclerView();
                            btn_distancesort.setText("최신순");
                            etEt1.setText("");
                            etEt2.setText("");
                            showToast("출발지 검색 완료");

                        } else {
                            showToast("위치를 찾을 수 없습니다");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        showToast("지오코딩 오류");
                    }
                } else {
                    showToast("위치를 입력하세요");
                }
            }
        });

        // btnBtn2 버튼 클릭 시
        btnBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredLocation = etEt2.getText().toString();
                if (!enteredLocation.isEmpty()) {
                    // 지오코딩 작업 수행하여 위도와 경도 얻기
                    Geocoder geocoder = new Geocoder(requireContext());
                    try {
                        List<Address> addresses = geocoder.getFromLocationName(enteredLocation, 1);
                        if (!addresses.isEmpty()) {
                            Address address = addresses.get(0);
                            double latitude = address.getLatitude();
                            double longitude = address.getLongitude();
                            filterPostsByDepartureLocation2(latitude, longitude);
                            sortByCreateTimeInRecyclerView();
                            btn_distancesort.setText("최신순");
                            etEt1.setText("");
                            etEt2.setText("");
                            showToast("도착지 검색 완료");
                        } else {
                            showToast("위치를 찾을 수 없습니다");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        showToast("지오코딩 오류");
                    }
                } else {
                    showToast("위치를 입력하세요");
                }
            }
        });

        return view;
    }

    private void fetchPosts() {
        String accessToken = tokenManager.getAccessToken();
        // Retrofit 객체 생성
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://121.200.87.205:8000/") // 스프링부트 API의 기본 URL을 설정
                .addConverterFactory(GsonConverterFactory.create()).build();

        // API 인터페이스 생성
        API apiService = retrofit.create(API.class);

        // API 호출
        Call<ResponseDto<List<PostResponseDto>>> call = apiService.getAllPost();
        call.enqueue(new Callback<ResponseDto<List<PostResponseDto>>>() {
            @Override
            public void onResponse(Call<ResponseDto<List<PostResponseDto>>> call, Response<ResponseDto<List<PostResponseDto>>> response) {
                if (response.isSuccessful()) {
                    ResponseDto<List<PostResponseDto>> responseDto = response.body();
                    if (responseDto != null && responseDto.getData() != null) {
                        List<PostResponseDto> postList = responseDto.getData();

                        // Filter the posts with recruitmentStatus "모집완료"
                        List<PostResponseDto> filteredPosts = new ArrayList<>();
                        for (PostResponseDto post : postList) {
                            if ("모집중".equals(post.getRecruitmentStatus())) {
                                filteredPosts.add(post);
                            }
                        }

                        // postList를 RecyclerView에 표시하는 코드 작성
                        postAdapter.setPosts(filteredPosts);
                        postAdapter.setBackUpPosts(filteredPosts);
                        sortByCreateTimeInRecyclerView();
                    }
                } else {
                    showToast("API 호출 실패");
                }
            }

            @Override
            public void onFailure(Call<ResponseDto<List<PostResponseDto>>> call, Throwable t) {
                showToast("API 호출 실패");
            }
        });
    }


    // 거리순 정렬
    private void sortByDistanceInRecyclerView(Location currentLocation) {
        List<PostResponseDto> currentPosts = postAdapter.getPosts();
        Geocoder geocoder = new Geocoder(requireContext());

        // Calculate distance for each post from your current location
        for (PostResponseDto post : currentPosts) {
            String departureAddress = post.getDepartureLocation();

            try {
                List<Address> departureAddresses = geocoder.getFromLocationName(departureAddress, 1);
                if (departureAddresses != null && departureAddresses.size() > 0) {
                    Address departureLocation = departureAddresses.get(0);
                    double departureLatitude = departureLocation.getLatitude();
                    double departureLongitude = departureLocation.getLongitude();

                    // Calculate distance between your location and departure location using Location.distanceTo method
                    Location departureLocationObj = new Location("");
                    departureLocationObj.setLatitude(departureLatitude);
                    departureLocationObj.setLongitude(departureLongitude);

                    float distance = currentLocation.distanceTo(departureLocationObj);
                    post.setDistance(distance);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Sort the currentPosts list based on distance in ascending order
        Collections.sort(currentPosts, new Comparator<PostResponseDto>() {
            @Override
            public int compare(PostResponseDto post1, PostResponseDto post2) {
                return Float.compare(post1.getDistance(), post2.getDistance());
            }
        });

        // Update the RecyclerView with the sorted list
        postAdapter.setPosts(currentPosts);
    }

    // 최신순 정렬
    private void sortByCreateTimeInRecyclerView() {
        List<PostResponseDto> currentPosts = postAdapter.getPosts();

        Collections.sort(currentPosts, new Comparator<PostResponseDto>() {
            @Override
            public int compare(PostResponseDto post1, PostResponseDto post2) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                try {
                    Date date1 = sdf.parse(post1.getCreateTime());
                    Date date2 = sdf.parse(post2.getCreateTime());

                    return date2.compareTo(date1); // 정렬을 내림차순으로 변경
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        postAdapter.setPosts(currentPosts);
    }

    // 출발 시간 순 정렬
    private void sortByDepartureTimeInRecyclerView() {
        List<PostResponseDto> currentPosts = postAdapter.getPosts();
        List<PostResponseDto> futurePosts = new ArrayList<>();
        List<PostResponseDto> pastPosts = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date now = new Date(); // 현재 시간

        for (PostResponseDto post : currentPosts) {
            try {
                Date departureTime = sdf.parse(post.getDepartureTime());

                // Compare the departure time with the current time
                if (departureTime.after(now)) {
                    // If the departure time is in the future, add it to the futurePosts list
                    futurePosts.add(post);
                } else {
                    // If the departure time is in the past, add it to the pastPosts list
                    pastPosts.add(post);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(futurePosts, new Comparator<PostResponseDto>() {
            @Override
            public int compare(PostResponseDto post1, PostResponseDto post2) {
                try {
                    Date date1 = sdf.parse(post1.getDepartureTime());
                    Date date2 = sdf.parse(post2.getDepartureTime());

                    long diff1 = Math.abs(date1.getTime() - now.getTime());
                    long diff2 = Math.abs(date2.getTime() - now.getTime());

                    if (diff1 < diff2) {
                        return -1; // date1 is closer to the current time
                    } else if (diff1 > diff2) {
                        return 1; // date2 is closer to the current time
                    } else {
                        return 0; // Both dates are the same distance from the current time
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        List<PostResponseDto> sortedPosts = new ArrayList<>();
        sortedPosts.addAll(futurePosts);
        sortedPosts.addAll(pastPosts);

        postAdapter.setPosts(sortedPosts);
    }

    // toast 보여주기
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();

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

    private boolean isAccessTokenAvailable() {
        String accessToken = tokenManager.getAccessToken();
        return accessToken != null;
    }

    private void openDetailPostFragment(PostResponseDto post) {
        // Create DetailPostFragment instance and pass the postID as an argument
        DetailPostFragment detailPostFragment = new DetailPostFragment();
        Bundle args = new Bundle();
        args.putLong("postId", post.getPostId());
        args.putString("title", post.getTitle());
        args.putString("content", post.getContent());
        args.putString("departureLocation", post.getDepartureLocation());
        args.putString("nickname", post.getNickname());
        args.putString("destinationLocation", post.getDestinationLocation());
        args.putString("recruitmentStatus", post.getRecruitmentStatus());
        args.putString("departureTime", post.getDepartureTime());
        args.putString("createTime", post.getCreateTime());
        args.putInt("remainSeat", post.getRemainSeat());
        args.putInt("allSeat", post.getAllSeat());
        args.putInt("gender", post.getGender());
        detailPostFragment.setArguments(args);

        // Replace the current fragment with DetailPostFragment
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.containers, detailPostFragment).addToBackStack(null).commit();
        }
    }

    // 출발지 검색 주소 비교
    private void filterPostsByDepartureLocation1(double latitude, double longitude) {
        List<PostResponseDto> filteredPosts = new ArrayList<>();
        // 지오코더 초기화
        Geocoder geocoder = new Geocoder(requireContext());

        Location userLocation = new Location("");
        userLocation.setLatitude(latitude);
        userLocation.setLongitude(longitude);

        for (PostResponseDto post : postAdapter.getBackUpPosts()) {
            try {
                List<Address> departureAddresses = geocoder.getFromLocationName(post.getDepartureLocation(), 1);
                if (!departureAddresses.isEmpty()) {
                    Address departureLocation = departureAddresses.get(0);
                    double departureLatitude = departureLocation.getLatitude();
                    double departureLongitude = departureLocation.getLongitude();

                    // 출발지와 사용자 위치 간의 거리 계산
                    Location departureLocationObj = new Location("");
                    departureLocationObj.setLatitude(departureLatitude);
                    departureLocationObj.setLongitude(departureLongitude);

                    float distance = userLocation.distanceTo(departureLocationObj);

                    // 일정 거리 이내의 게시글을 필터링
                    if (distance <= MAX_DISTANCE) { // MAX_DISTANCE는 원하는 거리로 설정하세요
                        filteredPosts.add(post);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 게시글 리스트 업데이트 및 어댑터에 알리기
        postAdapter.setPosts(filteredPosts);
    }

    // 도착지 검색 주소 비교
    private void filterPostsByDepartureLocation2(double latitude, double longitude) {
        List<PostResponseDto> filteredPosts = new ArrayList<>();
        // 지오코더 초기화
        Geocoder geocoder = new Geocoder(requireContext());

        Location userLocation = new Location("");
        userLocation.setLatitude(latitude);
        userLocation.setLongitude(longitude);

        for (PostResponseDto post : postAdapter.getBackUpPosts()) {
            try {
                List<Address> arrivalAddresses = geocoder.getFromLocationName(post.getDestinationLocation(), 1);
                if (!arrivalAddresses.isEmpty()) {
                    Address arrivalLocation = arrivalAddresses.get(0);
                    double arrivalLatitude = arrivalLocation.getLatitude();
                    double arrivalLongitude = arrivalLocation.getLongitude();

                    // 출발지와 사용자 위치 간의 거리 계산
                    Location arrivalLocationObj = new Location("");
                    arrivalLocationObj.setLatitude(arrivalLatitude);
                    arrivalLocationObj.setLongitude(arrivalLongitude);

                    float distance = userLocation.distanceTo(arrivalLocationObj);

                    // 일정 거리 이내의 게시글을 필터링
                    if (distance <= MAX_DISTANCE) { // MAX_DISTANCE는 원하는 거리로 설정하세요
                        filteredPosts.add(post);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 게시글 리스트 업데이트 및 어댑터에 알리기
        postAdapter.setPosts(filteredPosts);
    }
}