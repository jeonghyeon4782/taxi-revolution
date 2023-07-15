package com.jeonghyeon.taxiproject.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jeonghyeon.taxiproject.R;
import com.jeonghyeon.taxiproject.activity.MainActivity;
import com.jeonghyeon.taxiproject.api.API;
import com.jeonghyeon.taxiproject.dto.request.LoginRequest;
import com.jeonghyeon.taxiproject.dto.response.ResponseDto;
import com.jeonghyeon.taxiproject.dto.info.TokenInfo;
import com.jeonghyeon.taxiproject.token.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoginFragment extends Fragment {

    private EditText etId, etPassword;
    private Button btnLogin, btnRegister;

    private TokenManager tokenManager; // TokenManager 객체 추가


    public LoginFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tokenManager = new TokenManager(requireContext()); // TokenManager 초기화

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        etId = view.findViewById(R.id.et_id);
        etPassword = view.findViewById(R.id.et_password);
        btnLogin = view.findViewById(R.id.btn_login);
        btnRegister = view.findViewById(R.id.btn_register);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchDataFromAPI(etId.getText().toString(), etPassword.getText().toString());
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();

                // RidingFragment 생성 및 설정
                RegisterFragment registerFragment = new RegisterFragment();

                if (mainActivity != null) {
                    // MainActivity의 프래그먼트 매니저를 사용하여 RidingCheckFragment를 containers에 추가
                    mainActivity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.containers, registerFragment)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        return view;
    }

    private void fetchDataFromAPI(String username, String password) {

        // Retrofit 객체 생성
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.85.20:8000/") // 스프링부트 API의 기본 URL을 설정
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // API 인터페이스 생성
        API apiService = retrofit.create(API.class);

        // 로그인 요청 데이터 생성
        LoginRequest loginRequest = new LoginRequest(username, password);

        // API 호출
        Call<ResponseDto<TokenInfo>> call = apiService.login(loginRequest);
        call.enqueue(new Callback<ResponseDto<TokenInfo>>() {
            @Override
            public void onResponse(Call<ResponseDto<TokenInfo>> call, Response<ResponseDto<TokenInfo>> response) {
                if (response.isSuccessful()) {
                    ResponseDto<TokenInfo> responseDto = response.body();
                    int statusCode = responseDto.getStatus();

                    if (statusCode == 200) {
                        TokenInfo tokenInfo = responseDto.getData();
                        String accessToken = tokenInfo.getAccessToken();
                        String refreshToken = tokenInfo.getRefreshToken();

                        tokenManager.deleteTokens();
                        tokenManager.saveTokens(accessToken, refreshToken);

                        MainActivity mainActivity = (MainActivity) getActivity();

                        // RidingFragment 생성 및 설정
                        MemberInfoFragment memberInfoFragment = new MemberInfoFragment();

                        if (mainActivity != null) {
                            // MainActivity의 프래그먼트 매니저를 사용하여 RidingCheckFragment를 containers에 추가
                            mainActivity.getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.containers, memberInfoFragment)
                                    .addToBackStack(null)
                                    .commit();
                        }

                    } else {
                        String msg = responseDto.getMsg();
                        showToast(msg);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseDto<TokenInfo>> call, Throwable t) {

            }
        });
    }

    // toast 보여주기
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}