package com.jeonghyeon.taxiproject.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.jeonghyeon.taxiproject.R;
import com.jeonghyeon.taxiproject.api.API;
import com.jeonghyeon.taxiproject.dto.response.MemberResponseDto;
import com.jeonghyeon.taxiproject.dto.response.ResponseDto;
import com.jeonghyeon.taxiproject.fragment.AlightingCheckFragment;
import com.jeonghyeon.taxiproject.fragment.BoardingCheckFragment;
import com.jeonghyeon.taxiproject.fragment.GuardianFragment;
import com.jeonghyeon.taxiproject.fragment.LoginFragment;
import com.jeonghyeon.taxiproject.fragment.MemberInfoFragment;
import com.jeonghyeon.taxiproject.fragment.RecognizeFragment;
import com.jeonghyeon.taxiproject.fragment.RecordFragment;
import com.jeonghyeon.taxiproject.fragment.RidingFragment;
import com.jeonghyeon.taxiproject.fragment.TaxiStandFragment;
import com.jeonghyeon.taxiproject.token.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private PowerManager.WakeLock wakeLock;

    // 하단 메뉴바 선언
    private BottomNavigationView bottomNavigationView;

    // Fragment 선언
    private LoginFragment loginFragment;
    private GuardianFragment guardianFragment;
    private RecognizeFragment recognizeFragment;
    private RecordFragment recordFragment;
    private TaxiStandFragment taxiStandFragment;
    private AlightingCheckFragment alightingCheckFragment; // 하차 확인 화면
    private BoardingCheckFragment boardingCheckFragment; // 승차 확인 화면
    private RidingFragment ridingFragment; // 승차 중 화면

    private TextView logo;

    // main.xml 요소 선언
    private FrameLayout containers;
    private ImageView infoImageView, chatImageView, logoutImageView;

    private ImageView leftIconImageView;
    private Handler handler;
    private AnimatorSet animatorSet;

    private TokenManager tokenManager;
    private String memberId;
    private String nickname;
    private int gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 화면 꺼짐 방지 설정
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MyApp:MyWakeLockTag");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Fragment 객체 초기화
        loginFragment = new LoginFragment();
        guardianFragment = new GuardianFragment();
        recognizeFragment = new RecognizeFragment();
        recordFragment = new RecordFragment();
        taxiStandFragment = new TaxiStandFragment();
        boardingCheckFragment = new BoardingCheckFragment();
        alightingCheckFragment = new AlightingCheckFragment();
        ridingFragment = new RidingFragment();

        // main.xml 요소 초기화
        leftIconImageView = findViewById(R.id.leftIconImageView);
        infoImageView = findViewById(R.id.infoImageView);
        chatImageView = findViewById(R.id.chatImageView);
        logoutImageView = findViewById(R.id.logoutImageView);
        containers = findViewById(R.id.containers);
        logo = findViewById(R.id.txt_logo);

        // 하단 메뉴바 초기화
        bottomNavigationView = findViewById(R.id.bottom_navigationview);

        // TokenManager 초기화
        tokenManager = new TokenManager(getApplicationContext());

        // Handler 초기화
        handler = new Handler();

        // 내 정보 버튼 클릭 시
        infoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomNavigationView.getMenu().setGroupCheckable(0, false, true);
                fetchDataFromAPI();
            }
        });

        // 로그아웃 버튼 클릭 시
        logoutImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tokenManager.getAccessToken() != null) {
                    tokenManager.deleteTokens();
                    Toast.makeText(MainActivity.this, "로그아웃 완료", Toast.LENGTH_SHORT).show();
                    // R.id.action_taxi 선택
                    bottomNavigationView.setSelectedItemId(R.id.action_taxi);
                }
            }
        });

        // 채팅 버튼 클릭 시
        chatImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 채팅 버튼을 클릭했을 때의 동작 구현
            }
        });

        // BottomNavationVIew 기능구현
        NavigationBarView navigationBarView = findViewById(R.id.bottom_navigationview);
        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_record:
                        // 내비게이션 선택 가능
                        bottomNavigationView.getMenu().setGroupCheckable(0, true, true);
                        logo.setText("탑승기록");
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, recordFragment).commit();
                        return true;
                    case R.id.action_guardianNum:
                        bottomNavigationView.getMenu().setGroupCheckable(0, true, true);
                        logo.setText("보호자번호");
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, guardianFragment).commit();
                        return true;
                    case R.id.action_board: // 합승 게시판
                        bottomNavigationView.getMenu().setGroupCheckable(0, true, true);
                        return true;
                    case R.id.action_taxi: // 승차
                        bottomNavigationView.getMenu().setGroupCheckable(0, true, true);
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, recognizeFragment).commit();
                        return true;
                    case R.id.action_taxiStop: // 택시 승차장
                        bottomNavigationView.getMenu().setGroupCheckable(0, true, true);
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, taxiStandFragment).commit();
                        return true;
                }
                return false;
            }
        });

        // 앱 시작 시 recognizeFragment를 선택하도록 설정
        navigationBarView.setSelectedItemId(R.id.action_taxi);
    }

    public void updateTextView(String newText) {
        logo.setText(newText);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // WakeLock 활성화
        wakeLock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // WakeLock 해제
        wakeLock.release();
    }

    // 바텀 메뉴바 리턴 함수
    public BottomNavigationView publicBottomNavigationView() {
        return bottomNavigationView;
    }

    // 내 정보 화면 이동 시
    private void fetchDataFromAPI() {
        String accessToken = tokenManager.getAccessToken();

        if (accessToken == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.containers, loginFragment).commit();
            Toast.makeText(MainActivity.this, "로그인이 필요합니다", Toast.LENGTH_SHORT).show();
        } else {
            // Retrofit 객체 생성
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://121.200.87.205:8000/") // 스프링부트 API의 기본 URL을 설정
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // API 인터페이스 생성
            API apiService = retrofit.create(API.class);

            // API 호출
            Call<ResponseDto<MemberResponseDto>> call = apiService.getMyInfo("Bearer " + accessToken);
            call.enqueue(new Callback<ResponseDto<MemberResponseDto>>() {
                @Override
                public void onResponse(Call<ResponseDto<MemberResponseDto>> call, Response<ResponseDto<MemberResponseDto>> response) {
                    if (response.isSuccessful()) {
                        ResponseDto<MemberResponseDto> responseDto = response.body();
                        int statusCode = responseDto.getStatus();

                        if (statusCode == 200) {
                            MemberResponseDto memberResponseDto = responseDto.getData();
                            memberId = memberResponseDto.getMemberId();
                            nickname = memberResponseDto.getNickName();
                            gender = memberResponseDto.getGender();

                            // RidingFragment 생성 및 설정
                            MemberInfoFragment memberInfoFragment = new MemberInfoFragment();

                            Bundle args = new Bundle();
                            args.putString("memberId", memberId);
                            args.putString("nickname", nickname);
                            args.putString("gender", String.valueOf(gender));
                            memberInfoFragment.setArguments(args);

                            getSupportFragmentManager().beginTransaction().replace(R.id.containers, memberInfoFragment).commit();

                        } else if (statusCode == 423) { // 만료된 토큰이라면?
                            getSupportFragmentManager().beginTransaction().replace(R.id.containers, loginFragment).commit();
                            Toast.makeText(MainActivity.this, "로그인이 필요합니다", Toast.LENGTH_SHORT).show();
                        } else {
                            String msg = responseDto.getMsg();
                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseDto<MemberResponseDto>> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "API 요청 실패", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}