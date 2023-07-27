package com.jeonghyeon.taxiproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.jeonghyeon.taxiproject.R;
import com.jeonghyeon.taxiproject.activity.MainActivity;
import com.jeonghyeon.taxiproject.api.API;
import com.jeonghyeon.taxiproject.dto.request.PostAddRequestDto;
import com.jeonghyeon.taxiproject.dto.response.ResponseDto;
import com.jeonghyeon.taxiproject.token.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddPostFragment extends Fragment {

    private String status, date;
    private int groupCount = 0;

    private EditText etInputTitle, etContent;
    private ImageButton btnNowLocation;
    private Button btnDeparture, btnArrival, btnDepartureDate, btnDepartureTime, btnWriteComplete;
    private TokenManager tokenManager;
    private EditText etDeparture, etArrival;
    private Button btn_recruitmentComplete, btn_recruiting, btn_recruitmentStatus;
    private Button btn_number, btn_ok, btn_departureDate, btn_departureTime, btn_timeok;
    private NumberPicker numberPicker, np_minute, np_hours;

    RelativeLayout layout_time, layout_numberok;

    CalendarView calendarView;


    int rcount = 0;
    int ncount = 0;
    int ccount = 0;
    int tcount = 0;


    public AddPostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_post, container, false);
        tokenManager = new TokenManager(requireContext());

        etInputTitle = view.findViewById(R.id.et_inputTitle);
        etDeparture = view.findViewById(R.id.et_departure);
        etArrival = view.findViewById(R.id.et_arrival);
        etContent = view.findViewById(R.id.et_input);
        btnDeparture = view.findViewById(R.id.btn_departure);
        btnArrival = view.findViewById(R.id.btn_arrival);
        btnDepartureDate = view.findViewById(R.id.btn_departureDate);
        btnDepartureTime = view.findViewById(R.id.btn_departureTime);
        btnWriteComplete = view.findViewById(R.id.btn_writeComplete);
        btn_recruitmentStatus = (Button) view.findViewById(R.id.btn_recruitmentStatus);
        btn_recruiting = (Button) view.findViewById(R.id.btn_recruiting);
        btn_recruitmentComplete = (Button) view.findViewById(R.id.btn_recruitmentComplete);
        numberPicker = (NumberPicker) view.findViewById(R.id.numberpicker);
        btn_number = (Button) view.findViewById(R.id.btn_number);
        btn_ok = (Button) view.findViewById(R.id.btn_ok);
        btn_timeok = (Button) view.findViewById(R.id.btn_timeok);
        calendarView = (CalendarView) view.findViewById(R.id.calendarView);
        np_minute = (NumberPicker) view.findViewById(R.id.np_minute);
        np_hours = (NumberPicker) view.findViewById(R.id.np_hours);
        layout_time = (RelativeLayout) view.findViewById(R.id.layout_time);
        layout_numberok = (RelativeLayout) view.findViewById(R.id.layout_numberok);
        btn_departureDate = (Button) view.findViewById(R.id.btn_departureDate);
        btn_departureTime = (Button) view.findViewById(R.id.btn_departureTime);


        MainActivity mainActivity = (MainActivity) getActivity();
        etDeparture.setText(mainActivity.getDepartureLocation());
        etArrival.setText(mainActivity.getArrivalLocation());

        mainActivity.updateTextView("카풀 > 글쓰기");

        etDeparture.setEnabled(false);
        etArrival.setEnabled(false);

        //시간 버튼 클릭 시


        layout_time.setVisibility(View.INVISIBLE);
        np_hours.setVisibility(View.INVISIBLE);
        np_minute.setVisibility(View.INVISIBLE);
        btn_timeok.setVisibility(View.INVISIBLE);


        // 시 np 설정
        np_hours.setMaxValue(23);
        np_hours.setMinValue(0);
        np_hours.setOnLongPressUpdateInterval(100);


//        분 np 설정
        np_minute.setMaxValue(59);
        np_minute.setMinValue(0);
        np_minute.setOnLongPressUpdateInterval(100);



        // 시간
        btn_departureTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (date == null) {
                    showToast("날짜를 입력하세요");
                } else {
                    if(tcount == 0) {
                        layout_time.setVisibility(View.VISIBLE);
                        np_hours.setVisibility(View.VISIBLE);
                        np_minute.setVisibility(View.VISIBLE);
                        btn_timeok.setVisibility(View.VISIBLE);
                        tcount=1;
                    }
                    else{
                        layout_time.setVisibility(View.INVISIBLE);
                        np_hours.setVisibility(View.INVISIBLE);
                        np_minute.setVisibility(View.INVISIBLE);
                        btn_timeok.setVisibility(View.INVISIBLE);
                        tcount=0;
                    }
                }
            }
        });

        //완료버튼 클릭시
        btn_timeok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = date + " " + String.valueOf(np_hours.getValue() + ":" + String.valueOf(np_minute.getValue()) + ":00");
                btn_departureTime.setText(String.valueOf(np_hours.getValue() +"시 "+ String.valueOf(np_minute.getValue())+"분"));
                np_hours.setVisibility(View.INVISIBLE);
                np_minute.setVisibility(View.INVISIBLE);
                layout_time.setVisibility(View.INVISIBLE);
                btn_timeok.setVisibility(View.INVISIBLE);
                tcount=0;
            }
        });






        btn_recruiting.setVisibility(View.INVISIBLE);
        btn_recruitmentComplete.setVisibility(View.INVISIBLE);


//        모집상태 버튼 클릭시

        btn_recruitmentStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etArrival.getText().toString().isEmpty() || etDeparture.getText().toString().isEmpty()) {
                    showToast("출발지와 목적지부터 선택하세요");
                } else {
                    if(rcount == 0) {
                        btn_recruiting.setVisibility(View.VISIBLE);
                        btn_recruitmentComplete.setVisibility(View.VISIBLE);
                        rcount=1;
                    }
                    else{
                        btn_recruiting.setVisibility(View.INVISIBLE);
                        btn_recruitmentComplete.setVisibility(View.INVISIBLE);
                        rcount=0;
                    }
                }
            }
        });

        //      모집중 버튼 클릭시
        btn_recruiting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = btn_recruiting.getText().toString();
                btn_recruitmentStatus.setText(btn_recruiting.getText());
                btn_recruiting.setVisibility(View.INVISIBLE);
                btn_recruitmentComplete.setVisibility(View.INVISIBLE);
                rcount=0;
            }
        });

        //     모집완료 버튼 클릭시
        btn_recruitmentComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = btn_recruitmentComplete.getText().toString();
                btn_recruitmentStatus.setText(btn_recruitmentComplete.getText());
                btn_recruiting.setVisibility(View.INVISIBLE);
                btn_recruitmentComplete.setVisibility(View.INVISIBLE);
                rcount=0;
            }
        });

        //인원 버튼 클릭시

        layout_numberok.setVisibility(View.INVISIBLE);
        numberPicker.setVisibility(View.INVISIBLE);
        btn_ok.setVisibility(View.INVISIBLE);


        btn_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etArrival.getText().toString().isEmpty() || etDeparture.getText().toString().isEmpty()) {
                    showToast("출발지와 목적지부터 선택하세요");
                } else {
                    if(ncount == 0) {
                        layout_numberok.setVisibility(View.VISIBLE);
                        numberPicker.setVisibility(View.VISIBLE);
                        btn_ok.setVisibility(View.VISIBLE);
                        ncount=1;
                    }
                    else{
                        layout_numberok.setVisibility(View.INVISIBLE);
                        numberPicker.setVisibility(View.INVISIBLE);
                        btn_ok.setVisibility(View.INVISIBLE);
                        ncount=0;
                    }
                }
            }
        });


        numberPicker.setMaxValue(6);
        numberPicker.setMinValue(2);
        numberPicker.setOnLongPressUpdateInterval(100);
//        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//            @Override
//            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//
//            }
//        });


        //완료버튼 클릭시
        btn_ok.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                groupCount = numberPicker.getValue();
                btn_number.setText(String.valueOf(numberPicker.getValue()+"명"));
                layout_numberok.setVisibility(View.INVISIBLE);
                btn_ok.setVisibility(View.INVISIBLE);
                numberPicker.setVisibility(View.INVISIBLE);
                ncount=0;
            }
        });

        calendarView.setVisibility(View.INVISIBLE);

//날짜 버튼 클릭 시

        btn_departureDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etArrival.getText().toString().isEmpty() || etDeparture.getText().toString().isEmpty()) {
                    showToast("출발지와 목적지부터 선택하세요");
                } else {
                    if(ccount == 0) {
                        calendarView.setVisibility(View.VISIBLE);
                        ccount=1;
                    }
                    else{
                        calendarView.setVisibility(View.INVISIBLE);
                        ccount=0;
                    }
                }
            }
        });


        //달력 클릭시
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener()
        {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
            {
                date = String.format("%d-%d-%d", year, month + 1, dayOfMonth);
                btn_departureDate.setText(String.format("%d / %d / %d", year, month + 1, dayOfMonth));
                calendarView.setVisibility(View.INVISIBLE);
                ccount = 0;
            }
        });


        btnWriteComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etInputTitle.getText().toString().isEmpty()
                        || etContent.getText().toString().isEmpty()
                        || etDeparture.getText().toString().isEmpty()
                        || etArrival.getText().toString().isEmpty()
                        || status.isEmpty()
                        || groupCount == 0
                        || date.isEmpty()) {
                    showToast("모든 항목을 입력해주세요."); // Show toast if any field is empty
                    return;
                }

                fetchAddPost(etInputTitle.getText().toString(), etContent.getText().toString(), etDeparture.getText().toString(), etArrival.getText().toString(), status, groupCount, date);

                MainActivity mainActivity = (MainActivity) getActivity();

                // RidingFragment 생성 및 설정
                NoticeBoardFragment noticeBoardFragment = new NoticeBoardFragment();

                if (mainActivity != null) {
                    // MainActivity의 프래그먼트 매니저를 사용하여 RidingCheckFragment를 containers에 추가
                    mainActivity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.containers, noticeBoardFragment)
                            .addToBackStack(null)
                            .commit();
                    showToast("게시글 작성 완료");
                }
            }
        });

        btnDeparture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity mainActivity = (MainActivity) getActivity();

                // RidingFragment 생성 및 설정
                DepartureSearchFragment departureSearchFragment = new DepartureSearchFragment();

                if (mainActivity != null) {
                    // MainActivity의 프래그먼트 매니저를 사용하여 RidingCheckFragment를 containers에 추가
                    mainActivity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.containers, departureSearchFragment)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        btnArrival.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();

                // RidingFragment 생성 및 설정
                ArrivalSearchFragment arrivalSearchFragment = new ArrivalSearchFragment();

                if (mainActivity != null) {
                    // MainActivity의 프래그먼트 매니저를 사용하여 RidingCheckFragment를 containers에 추가
                    mainActivity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.containers, arrivalSearchFragment)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        return view;
    }

    private void fetchAddPost(String title, String content, String departureLocation, String destinationLocation, String recruitmentStatus, int allSeat, String departureTime) {
        String accessToken = tokenManager.getAccessToken();
        PostAddRequestDto requestDto = new PostAddRequestDto(title, content, departureLocation, destinationLocation, recruitmentStatus, allSeat, departureTime);

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
                    .baseUrl("http://121.200.87.205:8000/") // 스프링부트 API의 기본 URL을 설정
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // API 인터페이스 생성
            API apiService = retrofit.create(API.class);

            // API 호출
            Call<ResponseDto<Boolean>> call = apiService.addPost("Bearer " + accessToken, requestDto);
            call.enqueue(new Callback<ResponseDto<Boolean>>() {
                @Override
                public void onResponse(Call<ResponseDto<Boolean>> call, Response<ResponseDto<Boolean>> response) {
                    if (response.isSuccessful()) {
                        ResponseDto<Boolean> responseDto = response.body();
                        if (responseDto != null && responseDto.getData() != null) {
                            Boolean aBoolean = responseDto.getData();

                        }
                    } else {
                        showToast("API 호출 실패");
                    }
                }

                @Override
                public void onFailure(Call<ResponseDto<Boolean>> call, Throwable t) {
                    showToast("API 호출 실패");
                }
            });
        }
    }

    // toast 보여주기
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}