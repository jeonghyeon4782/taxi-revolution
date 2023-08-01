package com.jeonghyeon.taxiproject.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jeonghyeon.taxiproject.R;
import com.jeonghyeon.taxiproject.activity.MainActivity;
import com.jeonghyeon.taxiproject.api.API;
import com.jeonghyeon.taxiproject.dto.response.ResponseDto;
import com.jeonghyeon.taxiproject.token.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MemberInfoFragment extends Fragment {

    private EditText etNickname, etPassword;
    private TextView etId, etGender;
    private TokenManager tokenManager; // TokenManager 객체 추가
    private String memberId, nickname, gender;
    private Button btnNicknameUpdate;
    private Button btnPasswordUpdate, btnDeleteMember, btnLogout;
    private boolean isEditingNickname;


    public MemberInfoFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_member_info, container, false);
        etId = view.findViewById(R.id.et_id);
        etGender = view.findViewById(R.id.et_gender);
        etPassword = view.findViewById(R.id.et_password);
        etNickname = view.findViewById(R.id.et_nickname);
        btnPasswordUpdate = view.findViewById(R.id.btn_passwordUpdate);
        btnNicknameUpdate = view.findViewById(R.id.btn_nicknameUpdate);
        btnDeleteMember = view.findViewById(R.id.btn_deleteMember);
        btnLogout = view.findViewById(R.id.btn_logout);

        // 생성자 추가
        tokenManager = new TokenManager(requireContext());
        
        Bundle args = getArguments();
        if (args != null) {
            memberId = args.getString("memberId");
            nickname = args.getString("nickname");
            gender = args.getString("gender");
            etId.setText(memberId);
            etNickname.setText(nickname);
            if (gender.equals("0")) {
                etGender.setText("여자");
            } else {
                etGender.setText("남자");
            }
        }

        // 모든 텍스트 비활성화
        etId.setEnabled(false);
        etNickname.setEnabled(false);
        etGender.setEnabled(false);

        // MainActivity 인스턴스 가져오기
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.updateTextView("내 정보");

        // 로그아웃 버튼 클릭 시
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tokenManager.getAccessToken() != null) {
                    tokenManager.deleteTokens();
                    showToast("로그아웃 완료");
                    // R.id.action_taxi 선택
                    MainActivity mainActivity = (MainActivity) getActivity();
                    LoginFragment loginFragment = new LoginFragment();
                    if (mainActivity != null) {
                        mainActivity.getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.containers, loginFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                }
            }
        });

        // 닉네임 수정
        btnNicknameUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 완료를 누를경우
                if (isEditingNickname) {
                    if (nickname.equals(etNickname.getText().toString())) {
                        btnNicknameUpdate.setText("수정");
                        etNickname.setEnabled(false);
                        isEditingNickname = false;
                        showToast("같은 닉네임입니다");
                    } else if (isValidNickname(etNickname.getText().toString())) {
                        fetchUpdateNickname(etNickname.getText().toString());
                    }
                } else {
                    // 닉네임 수정 시작: 입력 가능 상태로 변경
                    btnNicknameUpdate.setText("완료");
                    etNickname.setEnabled(true);
                    isEditingNickname = true;
                }
            }
        });

        btnPasswordUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidPassword(etPassword.getText().toString())) {
                    fetchUpdatePassword(etPassword.getText().toString());
                }
            }
        });

        btnDeleteMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchDeleteMember();
            }
        });

        return view;
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

    // 비밀번호 유효성 검사
    public boolean isValidPassword(String password) {
        if (password.length() < 6 || password.length() > 20) {
            showToast("비밀번호는 6자에서 20자 사이여야 합니다");
            return false;
        }

        // 영어 소문자, 숫자, 특수기호 포함 여부 확인
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecialCharacter = false;

        for (char c : password.toCharArray()) {
            if (Character.isWhitespace(c)) {
                showToast("비밀번호에 공백이 포함될 수 없습니다");
                return false;
            } else if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecialCharacter = true;
            }
        }

        if (!hasLowercase || !hasDigit || !hasSpecialCharacter) {
            showToast("비밀번호는 영어 소문자, 숫자, 특수기호를 반드시 포함해야 합니다");
            return false;
        }

        return true;
    }

    // 닉네임 유효성 검사
    public boolean isValidNickname(String nickname) {
        if (nickname.length() < 2 || nickname.length() > 8) {
            showToast("닉네임은 2자에서 8자 사이여야 합니다");
            return false;
        }

        // 공백 확인
        if (nickname.contains(" ")) {
            showToast("닉네임에 공백이 포함될 수 없습니다");
            return false;
        }

        // 특수기호 확인
        String specialCharacters = "~`!@#$%^&*()-_=+\\|[{]};:'\",<.>/?";
        for (char c : nickname.toCharArray()) {
            if (specialCharacters.contains(String.valueOf(c))) {
                showToast("닉네임에는 특수기호가 포함될 수 없습니다");
                return false;
            }
        }

        return true;
    }

    private void fetchUpdatePassword(String password) {
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
                    .baseUrl("http://58.121.164.22:8000/") // 스프링부트 API의 기본 URL을 설정
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // API 인터페이스 생성
            API apiService = retrofit.create(API.class);

            // API 호출
            Call<ResponseDto<Boolean>> call = apiService.updatePassword("Bearer " + accessToken, password);
            call.enqueue(new Callback<ResponseDto<Boolean>>() {
                @Override
                public void onResponse(Call<ResponseDto<Boolean>> call, Response<ResponseDto<Boolean>> response) {
                    if (response.isSuccessful()) {
                        ResponseDto<Boolean> responseDto = response.body();
                        int statusCode = responseDto.getStatus();
                        String msg = responseDto.getMsg();

                        if (statusCode == 200) {
                            etPassword.setText("");
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

    private void fetchUpdateNickname(String nickname) {
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
                    .baseUrl("http://58.121.164.22:8000/") // 스프링부트 API의 기본 URL을 설정
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // API 인터페이스 생성
            API apiService = retrofit.create(API.class);

            // API 호출
            Call<ResponseDto<Boolean>> call = apiService.updateNickname("Bearer " + accessToken, nickname);
            call.enqueue(new Callback<ResponseDto<Boolean>>() {
                @Override
                public void onResponse(Call<ResponseDto<Boolean>> call, Response<ResponseDto<Boolean>> response) {
                    if (response.isSuccessful()) {
                        ResponseDto<Boolean> responseDto = response.body();
                        int statusCode = responseDto.getStatus();
                        String msg = responseDto.getMsg();

                        if (statusCode == 200) {
                            btnNicknameUpdate.setText("수정");
                            etNickname.setEnabled(false);
                            isEditingNickname = false;
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

    private void fetchDeleteMember() {
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
                    .baseUrl("http://58.121.164.22:8000/") // 스프링부트 API의 기본 URL을 설정
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // API 인터페이스 생성
            API apiService = retrofit.create(API.class);

            // API 호출
            Call<ResponseDto<Boolean>> call = apiService.deleteMyInfo("Bearer " + accessToken);
            call.enqueue(new Callback<ResponseDto<Boolean>>() {
                @Override
                public void onResponse(Call<ResponseDto<Boolean>> call, Response<ResponseDto<Boolean>> response) {
                    if (response.isSuccessful()) {
                        ResponseDto<Boolean> responseDto = response.body();
                        int statusCode = responseDto.getStatus();
                        String msg = responseDto.getMsg();

                        if (statusCode == 200) {
                            tokenManager.deleteTokens();
                            MainActivity mainActivity = (MainActivity) getActivity();
                            LoginFragment loginFragment = new LoginFragment();
                            if (mainActivity != null) {
                                BottomNavigationView bottomNavigationView = mainActivity.publicBottomNavigationView();
                                bottomNavigationView.setSelectedItemId(R.id.action_taxi);
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