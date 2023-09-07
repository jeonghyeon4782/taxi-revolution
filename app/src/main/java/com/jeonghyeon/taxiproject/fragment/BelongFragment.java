package com.jeonghyeon.taxiproject.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jeonghyeon.taxiproject.R;
import com.jeonghyeon.taxiproject.activity.MainActivity;
import com.jeonghyeon.taxiproject.adapter.MyPostAdapter;
import com.jeonghyeon.taxiproject.adapter.ParentAdapter;
import com.jeonghyeon.taxiproject.api.API;
import com.jeonghyeon.taxiproject.dto.response.BelongPostResponseDto;
import com.jeonghyeon.taxiproject.dto.response.PostResponseDto;
import com.jeonghyeon.taxiproject.dto.response.ResponseDto;
import com.jeonghyeon.taxiproject.token.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class BelongFragment extends Fragment {

    private TokenManager tokenManager;
    private ParentAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    public BelongFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_belong, container, false);
        tokenManager = new TokenManager(requireContext());
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.refresh_layout);
        adapter = new ParentAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        getBelongPostAPI();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBelongPostAPI();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        adapter.setOnDeleteClickListener(new ParentAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(BelongPostResponseDto post) {
                // 이 곳에서 Authority가 0인 경우와 1인 경우에 따라 다른 동작을 수행하세요.
                if (post.getAuthority() == 0) {
                    deletePostAPI(post.getPostId());
                    getBelongPostAPI();
                } else {
                    deleteBelongAPI(post.getBelongId());
                    getBelongPostAPI();
                }
            }
        });

        return view;
    }

    private void getBelongPostAPI() {
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
            Call<ResponseDto<List<BelongPostResponseDto>>> call = apiService.getBelongPost("Bearer " + accessToken);
            call.enqueue(new Callback<ResponseDto<List<BelongPostResponseDto>>>() {
                @Override
                public void onResponse(Call<ResponseDto<List<BelongPostResponseDto>>> call, Response<ResponseDto<List<BelongPostResponseDto>>> response) {
                    if (response.isSuccessful()) {
                        ResponseDto<List<BelongPostResponseDto>> responseDto = response.body();
                        int statusCode = responseDto.getStatus();

                        if (statusCode == 200) {
                            // 성공 시
                            List<BelongPostResponseDto> allData = responseDto.getData();
                            List<BelongPostResponseDto> filteredData = new ArrayList<>();

                            for (BelongPostResponseDto item : allData) {
                                if (item.isRecruitmentOpen()) {
                                    filteredData.add(item);
                                }
                            }

                            adapter.setData(filteredData);
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
                public void onFailure(Call<ResponseDto<List<BelongPostResponseDto>>> call, Throwable t) {
                    showToast("api 호출 실패");
                }
            });
        }
    }

    // toast 보여주기
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void deletePostAPI(Long postId) {
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
            Call<ResponseDto<Boolean>> call = apiService.deletePost("Bearer " + accessToken, postId);
            call.enqueue(new Callback<ResponseDto<Boolean>>() {
                @Override
                public void onResponse(Call<ResponseDto<Boolean>> call, Response<ResponseDto<Boolean>> response) {
                    if (response.isSuccessful()) {
                        ResponseDto<Boolean> responseDto = response.body();
                        int statusCode = responseDto.getStatus();
                        String msg = responseDto.getMsg();
                        if (statusCode == 200) {
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

    private void deleteBelongAPI(Long belongId) {
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
            Call<ResponseDto<Boolean>> call = apiService.deleteBelong("Bearer " + accessToken, belongId);
            call.enqueue(new Callback<ResponseDto<Boolean>>() {
                @Override
                public void onResponse(Call<ResponseDto<Boolean>> call, Response<ResponseDto<Boolean>> response) {
                    if (response.isSuccessful()) {
                        ResponseDto<Boolean> responseDto = response.body();
                        int statusCode = responseDto.getStatus();
                        String msg = responseDto.getMsg();
                        if (statusCode == 200) {
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