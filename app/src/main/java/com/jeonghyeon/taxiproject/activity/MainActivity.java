package com.jeonghyeon.taxiproject.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationBarView;
import com.jeonghyeon.taxiproject.R;
import com.jeonghyeon.taxiproject.fragment.GuardianFragment;
import com.jeonghyeon.taxiproject.fragment.RidingFragment;

public class MainActivity extends AppCompatActivity {

    // Fragment 선언
    GuardianFragment guardianFragment;
    RidingFragment ridingFragment;

    // main.xml 요소 선언
    private FrameLayout containers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fragment 객체 생성
        guardianFragment = new GuardianFragment();
        ridingFragment = new RidingFragment();

        // containers 가져오기
        containers = findViewById(R.id.containers);

        // 초기화면 설정
        getSupportFragmentManager().beginTransaction().replace(R.id.containers, guardianFragment).commit();

        // BottomNavationVIew 기능구현
        NavigationBarView navigationBarView = findViewById(R.id.bottom_navigationview);
        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.action_guardianNum:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, guardianFragment).commit();
                        return true;
                    case R.id.action_taxi:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, ridingFragment).commit();
                        return true;
                }
                return false;
            }
        });
    }
}