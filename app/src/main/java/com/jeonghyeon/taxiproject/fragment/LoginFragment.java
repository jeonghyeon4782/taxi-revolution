package com.jeonghyeon.taxiproject.fragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jeonghyeon.taxiproject.R;
import com.jeonghyeon.taxiproject.activity.MainActivity;
import com.jeonghyeon.taxiproject.api.API;
import com.jeonghyeon.taxiproject.dto.request.LoginRequestDto;
import com.jeonghyeon.taxiproject.dto.response.ResponseDto;
import com.jeonghyeon.taxiproject.dto.response.TokenResponseDto;
import com.jeonghyeon.taxiproject.token.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoginFragment extends Fragment {

    private EditText etId, etPassword;
    private Button btnLogin, btnRegister, btnShowPassword;

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

        // MainActivity 인스턴스 가져오기
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.updateTextView("로그인");

        etId = view.findViewById(R.id.et_id);
        etPassword = view.findViewById(R.id.et_password);
        btnLogin = view.findViewById(R.id.btn_login);
        btnRegister = view.findViewById(R.id.btn_register);
        btnShowPassword = view.findViewById(R.id.btn_showPassword);

        etId.setText("");
        etPassword.setText("");

        btnShowPassword.setOnClickListener(new View.OnClickListener() {
            private boolean isPasswordVisible = false;

            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    // 비밀번호 보이기 모드에서 비밀번호 가리기 모드로 변경
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    btnShowPassword.setText("표시");
                    isPasswordVisible = false;
                } else {
                    // 비밀번호 가리기 모드에서 비밀번호 보이기 모드로 변경
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    btnShowPassword.setText("가리기");
                    isPasswordVisible = true;
                }

                // 커서 위치 조정
                etPassword.setSelection(etPassword.getText().length());
            }
        });

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
                .baseUrl("http://58.121.164.22:8000/") // 스프링부트 API의 기본 URL을 설정
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // API 인터페이스 생성
        API apiService = retrofit.create(API.class);

        // 로그인 요청 데이터 생성
        LoginRequestDto loginRequestDto = new LoginRequestDto(username, password);

        // API 호출
        Call<ResponseDto<TokenResponseDto>> call = apiService.login(loginRequestDto);
        call.enqueue(new Callback<ResponseDto<TokenResponseDto>>() {
            @Override
            public void onResponse(Call<ResponseDto<TokenResponseDto>> call, Response<ResponseDto<TokenResponseDto>> response) {
                if (response.isSuccessful()) {
                    ResponseDto<TokenResponseDto> responseDto = response.body();
                    int statusCode = responseDto.getStatus();
                    String msg = responseDto.getMsg();

                    if (statusCode == 200) {
                        TokenResponseDto tokenResponseDto = responseDto.getData();
                        String accessToken = tokenResponseDto.getAccessToken();
                        String refreshToken = tokenResponseDto.getRefreshToken();

                        tokenManager.deleteTokens();
                        tokenManager.saveTokens(accessToken, refreshToken);

                        MainActivity mainActivity = (MainActivity) getActivity();
                        BottomNavigationView bottomNavigationView = mainActivity.publicBottomNavigationView();
                        bottomNavigationView.setSelectedItemId(R.id.action_taxi);

                        showToast("로그인 성공");


                    } else {
                        showToast(msg);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseDto<TokenResponseDto>> call, Throwable t) {
                showToast("API 호출 실패");
            }
        });
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