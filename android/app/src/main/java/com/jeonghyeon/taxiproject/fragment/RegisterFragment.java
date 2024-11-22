package com.jeonghyeon.taxiproject.fragment;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.jeonghyeon.taxiproject.R;
import com.jeonghyeon.taxiproject.activity.MainActivity;
import com.jeonghyeon.taxiproject.api.API;
import com.jeonghyeon.taxiproject.dto.request.RegisterRequestDto;
import com.jeonghyeon.taxiproject.dto.response.ResponseDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterFragment extends Fragment {

    private EditText etId, etPassword, etNickname, etGender;
    private Button btnRegister, btnDuplicateCheckMemberId, btnDuplicateCheckNickname, btnShowPassword;
    private RadioGroup rdoGender;
    private int gender = 0; // 여성 0 남성 1
    
    // 중복 검사 true / false
    private Boolean duplicateCheckMemberId = false;
    private Boolean duplicateCheckNickname = false;

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
        btnRegister = view.findViewById(R.id.btn_register);
        btnDuplicateCheckMemberId = view.findViewById(R.id.btn_duplicateCheckMemberId);
        btnDuplicateCheckNickname = view.findViewById(R.id.btn_duplicateCheckNickname);
        rdoGender = view.findViewById(R.id.rdo_gender);
        btnShowPassword = view.findViewById(R.id.btn_showPassword);

        // MainActivity 인스턴스 가져오기
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.updateTextView("회원가입");

        etId.setText("");
        etNickname.setText("");
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

        // 라디오 버튼
        rdoGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rdo_woman:
                        gender = 0;
                        break;
                    case R.id.rdo_man:
                        gender = 1;
                        break;
                }
            }
        });

        // 아이디 중복 검사
        btnDuplicateCheckMemberId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidId(etId.getText().toString())) {
                    fetchDuplicateCheckMemberIdFromAPI(etId.getText().toString());
                }
            }
        });

        // 닉네임 중복 검사
        btnDuplicateCheckNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidNickname(etNickname.getText().toString())) {
                    fetchDuplicateCheckNicknameFromAPI(etNickname.getText().toString());
                }
            }
        });

        // 회원가입
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!duplicateCheckMemberId) {
                    showToast("아이디 중복검사를 해주세요");
                } else if (!duplicateCheckNickname) {
                    showToast("닉네임 중복검사를 해주세요");
                } else if (isValidPassword(etPassword.getText().toString())) {
                    fetchDataFromAPI(etId.getText().toString(), etPassword.getText().toString(), etNickname.getText().toString(), gender) ;
                }
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
        RegisterRequestDto registerRequestDto = new RegisterRequestDto(username, password, nickname, gender);

        // API 호출
        Call<ResponseDto<String>> call = apiService.register(registerRequestDto);
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

    private void fetchDuplicateCheckMemberIdFromAPI(String memberId) {
        // Retrofit 객체 생성
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://121.200.87.205:8000/") // 스프링부트 API의 기본 URL을 설정
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // API 인터페이스 생성
        API apiService = retrofit.create(API.class);

        // API 호출
        Call<ResponseDto<Boolean>> call = apiService.duplicateCheckMemberId(memberId);
        call.enqueue(new Callback<ResponseDto<Boolean>>() {
            @Override
            public void onResponse(Call<ResponseDto<Boolean>> call, Response<ResponseDto<Boolean>> response) {
                if (response.isSuccessful()) {
                    ResponseDto<Boolean> responseDto = response.body();
                    int statusCode = responseDto.getStatus();
                    String msg = responseDto.getMsg();

                    if (statusCode == 200) {
                        duplicateCheckMemberId = responseDto.getData();
                        showToast(msg);
                    } else {
                        duplicateCheckMemberId = false;
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

    private void fetchDuplicateCheckNicknameFromAPI(String nickname) {
        // Retrofit 객체 생성
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://121.200.87.205:8000/") // 스프링부트 API의 기본 URL을 설정
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // API 인터페이스 생성
        API apiService = retrofit.create(API.class);

        // API 호출
        Call<ResponseDto<Boolean>> call = apiService.duplicateCheckNickname(nickname);
        call.enqueue(new Callback<ResponseDto<Boolean>>() {
            @Override
            public void onResponse(Call<ResponseDto<Boolean>> call, Response<ResponseDto<Boolean>> response) {
                if (response.isSuccessful()) {
                    ResponseDto<Boolean> responseDto = response.body();
                    int statusCode = responseDto.getStatus();
                    String msg = responseDto.getMsg();

                    if (statusCode == 200) {
                        duplicateCheckNickname = responseDto.getData();
                        showToast(msg);
                    } else {
                        duplicateCheckNickname = false;
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

    // toast 보여주기
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    // 아이디 유효성 검사
    public boolean isValidId(String id) {
        if (id.length() < 5 || id.length() > 12) {
            showToast("아이디는 5자 이상 12자 이하여야 합니다");
            return false;
        }

        // 문자, 숫자, 공백 확인
        for (char c : id.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                if (Character.isWhitespace(c)) {
                    showToast("아이디는 공백이 포함될 수 없습니다");
                } else {
                    showToast("아이디는 영어 소문자, 대문자, 숫자만 포함될 수 있습니다");
                }
                return false;
            }
        }

        return true;
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
}