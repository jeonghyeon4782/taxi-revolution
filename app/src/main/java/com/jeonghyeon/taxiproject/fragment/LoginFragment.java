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
import com.jeonghyeon.taxiproject.dto.response.MemberResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoginFragment extends Fragment {

    private EditText etId, etPassword;
    private Button btnLogin, btnResister;

    public LoginFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        etId = view.findViewById(R.id.et_id);
        etPassword = view.findViewById(R.id.et_password);
        btnLogin = view.findViewById(R.id.btn_login);
        btnResister = view.findViewById(R.id.btn_resister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchDataFromAPI(etId.getText().toString(), etPassword.getText().toString());
            }
        });

        btnResister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();

                // RidingFragment 생성 및 설정
                ResisterFragment resisterFragment = new ResisterFragment();

                if (mainActivity != null) {
                    // MainActivity의 프래그먼트 매니저를 사용하여 RidingCheckFragment를 containers에 추가
                    mainActivity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.containers, resisterFragment)
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
        Call<MemberResponse> call = apiService.login(loginRequest);
        call.enqueue(new Callback<MemberResponse>() {
            @Override
            public void onResponse(Call<MemberResponse> call, Response<MemberResponse> response) {
                if (response.isSuccessful()) {
                    MemberResponse memberResponse = response.body();
                    String token = memberResponse.getData();
                    showToast(token);

                } else {
                    // API 호출이 실패한 경우에 대한 처리 작성
                }
            }

            @Override
            public void onFailure(Call<MemberResponse> call, Throwable t) {
                // API 호출이 실패한 경우에 대한 처리 작성
            }
        });
    }

    // toast 보여주기
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}