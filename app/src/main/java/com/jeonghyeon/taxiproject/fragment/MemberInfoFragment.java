package com.jeonghyeon.taxiproject.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.jeonghyeon.taxiproject.R;
import com.jeonghyeon.taxiproject.activity.MainActivity;
import com.jeonghyeon.taxiproject.api.API;
import com.jeonghyeon.taxiproject.dto.info.MemberInfo;
import com.jeonghyeon.taxiproject.dto.info.TokenInfo;
import com.jeonghyeon.taxiproject.dto.request.LoginRequest;
import com.jeonghyeon.taxiproject.dto.response.ResponseDto;
import com.jeonghyeon.taxiproject.token.TokenManager;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MemberInfoFragment extends Fragment {

    private EditText etId, etNickname, etGender;

    private TokenManager tokenManager; // TokenManager 객체 추가

    public MemberInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tokenManager = new TokenManager(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_member_info, container, false);
        etId = view.findViewById(R.id.et_id);
        etNickname = view.findViewById(R.id.et_nickname);
        etGender = view.findViewById(R.id.et_gender);

        fetchDataFromAPI();

        return view;
    }

    private void fetchDataFromAPI() {
        String accessToken = tokenManager.getAccessToken();

        if (accessToken == null) {
            navigateToLoginPage();
        } else {
            // Retrofit 객체 생성
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.85.20:8000/") // 스프링부트 API의 기본 URL을 설정
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // API 인터페이스 생성
            API apiService = retrofit.create(API.class);

            // API 호출
            Call<ResponseDto<MemberInfo>> call = apiService.getMyInfo("Bearer " + accessToken);
            call.enqueue(new Callback<ResponseDto<MemberInfo>>() {
                @Override
                public void onResponse(Call<ResponseDto<MemberInfo>> call, Response<ResponseDto<MemberInfo>> response) {
                    if (response.isSuccessful()) {
                        ResponseDto<MemberInfo> responseDto = response.body();
                        int statusCode = responseDto.getStatus();

                        if (statusCode == 200) {
                            MemberInfo memberInfo = responseDto.getData();
                            etId.setText(memberInfo.getMemberId().toString());
                            etNickname.setText(memberInfo.getNickName().toString());
                            etGender.setText(String.valueOf(memberInfo.getGender()));

                        } else if (response.code() == 401) { // 만료된 토큰이라면?
                            navigateToLoginPage();
                        } else {
                            String msg = responseDto.getMsg();
                            showToast(msg);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseDto<MemberInfo>> call, Throwable t) {
                    showToast("API 요청 실패");
                }
            });
        }
    }

    private void navigateToLoginPage() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            LoginFragment loginFragment = new LoginFragment();
            mainActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containers, loginFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    // toast 보여주기
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}