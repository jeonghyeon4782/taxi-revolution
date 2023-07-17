package com.jeonghyeon.taxiproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.jeonghyeon.taxiproject.R;
import com.jeonghyeon.taxiproject.activity.MainActivity;
import com.jeonghyeon.taxiproject.api.API;
import com.jeonghyeon.taxiproject.dto.request.RegisterRequest;
import com.jeonghyeon.taxiproject.dto.response.ResponseDto;
import com.jeonghyeon.taxiproject.dto.response.TokenResponseDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterFragment extends Fragment {

    private EditText etId, etPassword, etNickname, etGender;
    private Button btnRegister;

    public RegisterFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        etId = view.findViewById(R.id.et_id);
        etPassword = view.findViewById(R.id.et_password);
        etNickname = view.findViewById(R.id.et_nickname);
        etGender = view.findViewById(R.id.et_gender);
        btnRegister = view.findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchDataFromAPI(etId.getText().toString(), etPassword.getText().toString(), etNickname.getText().toString(), Integer.parseInt(etGender.getText().toString()));
            }
        });

        return view;
    }

    private void fetchDataFromAPI(String username, String password, String nickname, int gender) {
        // Retrofit 객체 생성
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://121.200.87.205:8000/") // 스프링부트 API의 기본 URL을 설정
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // API 인터페이스 생성
        API apiService = retrofit.create(API.class);

        // 로그인 요청 데이터 생성
        RegisterRequest registerRequest = new RegisterRequest(username, password, nickname, gender);

        // API 호출
        Call<ResponseDto<String>> call = apiService.register(registerRequest);
        call.enqueue(new Callback<ResponseDto<String>>() {
            @Override
            public void onResponse(Call<ResponseDto<String>> call, Response<ResponseDto<String>> response) {
                if (response.isSuccessful()) {
                    ResponseDto<String> responseDto = response.body();
                    int statusCode = responseDto.getStatus();

                    if (statusCode == 200) {
                        MainActivity mainActivity = (MainActivity) getActivity();

                        // RidingFragment 생성 및 설정
                        LoginFragment loginFragment = new LoginFragment();

                        if (mainActivity != null) {
                            // MainActivity의 프래그먼트 매니저를 사용하여 RidingCheckFragment를 containers에 추가
                            mainActivity.getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.containers, loginFragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                        
                        showToast("회원가입 완료");

                    } else {
                        String msg = responseDto.getMsg();
                        showToast(msg);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseDto<String>> call, Throwable t) {
                showToast("API 호출 실패");
            }
        });
    }

    // toast 보여주기
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}