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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.jeonghyeon.taxiproject.R;
import com.jeonghyeon.taxiproject.fragment.AlightingCheckFragment;
import com.jeonghyeon.taxiproject.fragment.BoardingCheckFragment;
import com.jeonghyeon.taxiproject.fragment.GuardianFragment;
import com.jeonghyeon.taxiproject.fragment.LoginFragment;
import com.jeonghyeon.taxiproject.fragment.RecognizeFragment;
import com.jeonghyeon.taxiproject.fragment.RecordFragment;
import com.jeonghyeon.taxiproject.fragment.RidingFragment;
import com.jeonghyeon.taxiproject.fragment.TaxiStandFragment;

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
    private ImageView infoImageView, chatImageView;

    private ImageView leftIconImageView;
    private Handler handler;
    private AnimatorSet animatorSet;

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
        containers = findViewById(R.id.containers);
        logo = findViewById(R.id.txt_logo);

        // 하단 메뉴바 초기화
        bottomNavigationView = findViewById(R.id.bottom_navigationview);

        // Handler 초기화
        handler = new Handler();

        // 애니메이션 설정
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(leftIconImageView, "scaleX", 1f, 1.5f, 1f);
        scaleXAnimator.setDuration(1000);
        scaleXAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        scaleXAnimator.setRepeatMode(ObjectAnimator.REVERSE);

        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(leftIconImageView, "scaleY", 1f, 1.5f, 1f);
        scaleYAnimator.setDuration(1000);
        scaleYAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        scaleYAnimator.setRepeatMode(ObjectAnimator.REVERSE);

        animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator);

        // 내 정보 버튼 클릭 시
        infoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 내 정보 버튼을 클릭했을 때의 동작 구현
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
                        logo.setText("탑승기록");
                        startAnimation();
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, recordFragment).commit();
                        return true;
                    case R.id.action_guardianNum:
                        startAnimation();
                        logo.setText("보호자번호");
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, guardianFragment).commit();
                        return true;
                    case R.id.action_board:
                        startAnimation();
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, loginFragment).commit();
                        return true;
                    case R.id.action_taxi:
                        startAnimation();
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, recognizeFragment).commit();
                        return true;
                    case R.id.action_taxiStop:
                        startAnimation();
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

    private void startAnimation() {
        if (animatorSet != null) {
            // 이미 실행 중인 애니메이션을 취소하고 초기화
            animatorSet.cancel();
            animatorSet.removeAllListeners();
            animatorSet.end();
        }

        // 애니메이션 시작
        leftIconImageView.setVisibility(View.VISIBLE);

        ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(leftIconImageView, "translationY", 0f, -50f, 0f);
        translationYAnimator.setDuration(1000);
        translationYAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        translationYAnimator.setRepeatMode(ObjectAnimator.REVERSE);

        animatorSet = new AnimatorSet();
        animatorSet.play(translationYAnimator);
        animatorSet.start();

        // 2초 후 애니메이션 정지
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 애니메이션 정지
                animatorSet.cancel();
            }
        }, 2000);
    }

    // 바텀 메뉴바 리턴 함수
    public BottomNavigationView publicBottomNavigationView() {
        return bottomNavigationView;
    }
}