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
    private EditText etEt;
    private ImageButton btnBtn;
    private Button btn_distancesort, btn_departureTime, btn_new, btn_distance; // 정렬 버튼 , 출발시간순 , 최신순 , 거리순
    private int dcount = 0;
    private Button btnWrite;
    private LocationManager locationManager;
    private LocationListener locationListener;

    public NoticeBoardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice_board, container, false);

        etEt = view.findViewById(R.id.et_destinationLocation);
        btnBtn = view.findViewById(R.id.btn_destinationLocation);

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


        btn_departureTime.setVisibility(View.INVISIBLE);
        btn_new.setVisibility(View.INVISIBLE);
        btn_distance.setVisibility(View.INVISIBLE);

        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.updateTextView("카풀");

        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();

                // RidingFragment 생성 및 설정
                AddPostFragment addPostFragment = new AddPostFragment();

                if (mainActivity != null) {
                    // MainActivity의 프래그먼트 매니저를 사용하여 RidingCheckFragment를 containers에 추가
                    mainActivity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.containers, addPostFragment)
                            .addToBackStack(null)
                            .commit();
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

        btnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = etEt.getText().toString().trim();
                postAdapter.filterPosts(inputText);
                btn_distancesort.setText("최신순");
                sortByCreateTimeInRecyclerView();
            }
        });

        return view;
    }

    private void fetchPosts() {
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
            // Retrofit 객체 생성
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://121.200.87.205:8000/") // 스프링부트 API의 기본 URL을 설정
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // API 인터페이스 생성
            API apiService = retrofit.create(API.class);

            // API 호출
            Call<ResponseDto<List<PostResponseDto>>> call = apiService.getAllPost("Bearer " + accessToken);
            call.enqueue(new Callback<ResponseDto<List<PostResponseDto>>>() {
                @Override
                public void onResponse(Call<ResponseDto<List<PostResponseDto>>> call, Response<ResponseDto<List<PostResponseDto>>> response) {
                    if (response.isSuccessful()) {
                        ResponseDto<List<PostResponseDto>> responseDto = response.body();

                        int statusCode = responseDto.getStatus();
                        String msg = responseDto.getMsg();

                        if (statusCode == 200) {
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
                            msg = responseDto.getMsg();
                            showToast(msg);
                        }
                    }

                    else {
                        showToast("API 호출 실패");
                    }
                }

                @Override
                public void onFailure(Call<ResponseDto<List<PostResponseDto>>> call, Throwable t) {
                    showToast("API 호출 실패");
                }
            });
        }
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

        // Sort futurePosts in ascending order of departure time proximity to the current time
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

        // Combine futurePosts and pastPosts, with futurePosts first
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
}