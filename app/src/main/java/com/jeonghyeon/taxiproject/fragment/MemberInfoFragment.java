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
import com.jeonghyeon.taxiproject.dto.response.MemberResponseDto;
import com.jeonghyeon.taxiproject.dto.response.ResponseDto;
import com.jeonghyeon.taxiproject.token.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MemberInfoFragment extends Fragment {

    private EditText etId, etNickname, etGender;

    private TokenManager tokenManager; // TokenManager 객체 추가

    private String memberId, nickname, gender;

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

        Bundle args = getArguments();
        if (args != null) {
            memberId = args.getString("memberId");
            nickname = args.getString("nickname");
            gender = args.getString("gender");
            etId.setText(memberId);
            etNickname.setText(nickname);
            etGender.setText(gender);
        }

        return view;
    }

    // toast 보여주기
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}