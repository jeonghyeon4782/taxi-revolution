package com.jeonghyeon.taxiproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jeonghyeon.taxiproject.R;
import com.jeonghyeon.taxiproject.activity.MainActivity;
import com.jeonghyeon.taxiproject.adapter.MyPostAdapter;
import com.jeonghyeon.taxiproject.adapter.PostAdapter;
import com.jeonghyeon.taxiproject.api.API;
import com.jeonghyeon.taxiproject.dto.request.PostAddRequestDto;
import com.jeonghyeon.taxiproject.dto.response.PostResponseDto;
import com.jeonghyeon.taxiproject.dto.response.ResponseDto;
import com.jeonghyeon.taxiproject.token.TokenManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

public class MyPostFragment extends Fragment {

    private TokenManager tokenManager;
    private RecyclerView recyclerView;
    private MyPostAdapter postAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    public MyPostFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_post, container, false);
        tokenManager = new TokenManager(requireContext());
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.refresh_layout);

        // 어댑터 초기화
        postAdapter = new MyPostAdapter();
        recyclerView.setAdapter(postAdapter);

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.updateTextView("카풀 > 내가쓴글");

        getMyPostAPI();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMyPostAPI();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        postAdapter.setOnDeleteClickListener(new MyPostAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(PostResponseDto post) {
                deletePostAPI(post.getPostId());
            }
        });

        postAdapter.setOnModifyClickListener(new MyPostAdapter.OnModifyClickListener() {
            @Override
            public void onModifyClick(PostResponseDto post) {
                openModifyFragment(post);
            }
        });

        postAdapter.setOnItemClickListener(new MyPostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(PostResponseDto post) {
                openDetailPostFragment(post);
            }
        });

        return view;
    }

    private void getMyPostAPI() {
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
            Call<ResponseDto<List<PostResponseDto>>> call = apiService.getAllMyPost("Bearer " + accessToken);
            call.enqueue(new Callback<ResponseDto<List<PostResponseDto>>>() {
                @Override
                public void onResponse(Call<ResponseDto<List<PostResponseDto>>> call, Response<ResponseDto<List<PostResponseDto>>> response) {
                    if (response.isSuccessful()) {
                        ResponseDto<List<PostResponseDto>> responseDto = response.body();
                        int statusCode = responseDto.getStatus();

                        if (statusCode == 200) {
                            List<PostResponseDto> postList = responseDto.getData();
                            postAdapter.setData(postList);
                            sortByCreateTimeInRecyclerView();
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
                public void onFailure(Call<ResponseDto<List<PostResponseDto>>> call, Throwable t) {
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
                            getMyPostAPI();
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

    private void openModifyFragment(PostResponseDto post) {
        ModifyFragment modifyFragment = new ModifyFragment();
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
        modifyFragment.setArguments(args);

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.containers, modifyFragment).addToBackStack(null).commit();
        }
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
}