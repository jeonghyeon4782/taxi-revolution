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

import com.jeonghyeon.taxiproject.R;
import com.jeonghyeon.taxiproject.activity.MainActivity;
import com.jeonghyeon.taxiproject.api.API;
import com.jeonghyeon.taxiproject.dto.request.PostUpdateRequestDto;
import com.jeonghyeon.taxiproject.dto.response.ResponseDto;
import com.jeonghyeon.taxiproject.token.TokenManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ModifyFragment extends Fragment implements ArrivalSearchDialogFragment.OnCompleteListener, DepartureSearchDialogFragment.OnCompleteListener {

    private String date;
    private Long postId; // primary key
    private String title; // 제목
    private String content; // 내용
    private String departureLocation; // 출발지
    private String destinationLocation; // 도착지
    private String recruitmentStatus; // 모집상태
    private int remainSeat; // 남은 좌석
    private int allSeat; // 총 좌석
    private String departureTime; // 출발시간
    private String createTime; // 생성시간
    private String nickname; // 글쓴이 아이디
    private int gender; // 여자 : 0

    private String status;
    private String groupCount;

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


    public ModifyFragment() {
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

        etDeparture.setEnabled(false);
        etArrival.setEnabled(false);

        Bundle args = getArguments();
        if (args != null) {
            postId = args.getLong("postId");
            title = args.getString("title");
            content = args.getString("content");
            departureLocation = args.getString("departureLocation");
            destinationLocation = args.getString("destinationLocation");
            recruitmentStatus = args.getString("recruitmentStatus");
            departureTime = args.getString("departureTime");
            createTime = args.getString("createTime");
            remainSeat = args.getInt("remainSeat");
            allSeat = args.getInt("allSeat");
            gender = args.getInt("gender");
            nickname = args.getString("nickname");
        }

        String[] dateTimeParts = departureTime.split(" ");
        String datePart = dateTimeParts[0];
        String timePart = dateTimeParts[1];

        MainActivity mainActivity = (MainActivity) getActivity();

        etDeparture.setText(departureLocation);
        etArrival.setText(destinationLocation);
        etInputTitle.setText(title);
        etContent.setText(content);
        btn_recruitmentStatus.setText(recruitmentStatus);
        btn_number.setText(String.valueOf(allSeat));

        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy / M / d", Locale.getDefault());
        try {
            Date date = inputDateFormat.parse(datePart);
            String formattedDate = outputDateFormat.format(date);
            btn_departureDate.setText(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat inputTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        SimpleDateFormat outputTimeFormat = new SimpleDateFormat("H시 m분", Locale.getDefault());
        try {
            Date time = inputTimeFormat.parse(timePart);
            String formattedTime = outputTimeFormat.format(time);
            btn_departureTime.setText(formattedTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mainActivity.updateTextView("카풀 > 글수정");

        //시간 버튼 클릭 시
        layout_time.setVisibility(View.INVISIBLE);
        np_hours.setVisibility(View.INVISIBLE);
        np_minute.setVisibility(View.INVISIBLE);
        btn_timeok.setVisibility(View.INVISIBLE);

        // 시 np 설정
        np_hours.setMaxValue(23);
        np_hours.setMinValue(0);
        np_hours.setOnLongPressUpdateInterval(100);

        // 분 np 설정
        np_minute.setMaxValue(59);
        np_minute.setMinValue(0);
        np_minute.setOnLongPressUpdateInterval(100);


        // 시간
        btn_departureTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tcount == 0) {
                    layout_time.setVisibility(View.VISIBLE);
                    np_hours.setVisibility(View.VISIBLE);
                    np_minute.setVisibility(View.VISIBLE);
                    btn_timeok.setVisibility(View.VISIBLE);
                    tcount = 1;
                } else {
                    layout_time.setVisibility(View.INVISIBLE);
                    np_hours.setVisibility(View.INVISIBLE);
                    np_minute.setVisibility(View.INVISIBLE);
                    btn_timeok.setVisibility(View.INVISIBLE);
                    tcount = 0;
                }
            }

        });

        //완료버튼 클릭시
        btn_timeok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_departureTime.setText(String.valueOf(np_hours.getValue() + "시 " + String.valueOf(np_minute.getValue()) + "분"));
                np_hours.setVisibility(View.INVISIBLE);
                np_minute.setVisibility(View.INVISIBLE);
                layout_time.setVisibility(View.INVISIBLE);
                btn_timeok.setVisibility(View.INVISIBLE);
                tcount = 0;
            }
        });


        btn_recruiting.setVisibility(View.INVISIBLE);
        btn_recruitmentComplete.setVisibility(View.INVISIBLE);


//        모집상태 버튼 클릭시

        btn_recruitmentStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rcount == 0) {
                    btn_recruiting.setVisibility(View.VISIBLE);
                    btn_recruitmentComplete.setVisibility(View.VISIBLE);
                    rcount = 1;
                } else {
                    btn_recruiting.setVisibility(View.INVISIBLE);
                    btn_recruitmentComplete.setVisibility(View.INVISIBLE);
                    rcount = 0;
                }
            }
        });

        //      모집중 버튼 클릭시
        btn_recruiting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = btn_recruiting.getText().toString();
                btn_recruitmentStatus.setText(status);
                btn_recruiting.setVisibility(View.INVISIBLE);
                btn_recruitmentComplete.setVisibility(View.INVISIBLE);
                rcount = 0;
            }
        });

        //     모집완료 버튼 클릭시
        btn_recruitmentComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = btn_recruitmentComplete.getText().toString();
                btn_recruitmentStatus.setText(status);
                btn_recruiting.setVisibility(View.INVISIBLE);
                btn_recruitmentComplete.setVisibility(View.INVISIBLE);
                rcount = 0;
            }
        });

        //인원 버튼 클릭시

        layout_numberok.setVisibility(View.INVISIBLE);
        numberPicker.setVisibility(View.INVISIBLE);
        btn_ok.setVisibility(View.INVISIBLE);


        btn_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ncount == 0) {
                    layout_numberok.setVisibility(View.VISIBLE);
                    numberPicker.setVisibility(View.VISIBLE);
                    btn_ok.setVisibility(View.VISIBLE);
                    ncount = 1;
                } else {
                    layout_numberok.setVisibility(View.INVISIBLE);
                    numberPicker.setVisibility(View.INVISIBLE);
                    btn_ok.setVisibility(View.INVISIBLE);
                    ncount = 0;
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
                groupCount = String.valueOf(numberPicker.getValue());
                btn_number.setText(String.valueOf(groupCount));
                layout_numberok.setVisibility(View.INVISIBLE);
                btn_ok.setVisibility(View.INVISIBLE);
                numberPicker.setVisibility(View.INVISIBLE);
                ncount = 0;
            }
        });

        calendarView.setVisibility(View.INVISIBLE);

        //날짜 버튼 클릭 시
        btn_departureDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ccount == 0) {
                    calendarView.setVisibility(View.VISIBLE);
                    ccount = 1;
                } else {
                    calendarView.setVisibility(View.INVISIBLE);
                    ccount = 0;
                }
            }

        });


        //달력 클릭시
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                date = String.format("%d-%d-%d", year, month + 1, dayOfMonth);
                btn_departureDate.setText(String.format("%d / %d / %d", year, month + 1, dayOfMonth));
                calendarView.setVisibility(View.INVISIBLE);
                ccount = 0;
            }
        });


        // 작성 완료 버튼
        btnWriteComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String recruitmentStatus = btn_recruitmentStatus.getText().toString();
                String groupCountText = btn_number.getText().toString();
                String departureTimeText = btn_departureTime.getText().toString();
                String departureDateText = btn_departureDate.getText().toString();
                String departure = etDeparture.getText().toString();
                String arrival = etArrival.getText().toString();
                String title = etInputTitle.getText().toString();
                String date_api = null;

                if (recruitmentStatus.equals("모집상태")) {
                    showToast("모집상태를 선택하세요");
                    return; // 검사를 중단하고 더 이상 진행하지 않음
                }
                if (groupCountText.equals("인원")) {
                    showToast("인원수를 선택하세요");
                    return;
                }
                if (title.isEmpty()) {
                    showToast("제목을 입력하세요");
                    return;
                }
                if (departure.isEmpty()) {
                    showToast("출발지를 선택하세요");
                    return;
                }

                if (arrival.isEmpty()) {
                    showToast("도착지를 선택하세요");
                    return;
                }
                if (departureDateText.equals("날짜")) {
                    showToast("날짜를 선택하세요");
                    return;
                }
                if (departureTimeText.equals("시간")) {
                    showToast("시간을 선택하세요");
                    return;
                }


                MainActivity mainActivity = (MainActivity) getActivity();

                SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy / M / d", Locale.getDefault());

                try {
                    Date date = inputDateFormat.parse(departureDateText);
                    String formattedDate = outputDateFormat.format(date);
                    date_api = formattedDate;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                SimpleDateFormat outputTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                SimpleDateFormat inputTimeFormat = new SimpleDateFormat("H시 m분", Locale.getDefault());
                try {
                    Date time = inputTimeFormat.parse(departureTimeText);
                    String formattedTime = outputTimeFormat.format(time);
                    date_api = date_api + " " + formattedTime;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                updatePost(title, content, departure, arrival, recruitmentStatus, Integer.parseInt(groupCountText), date_api, postId);

                // RidingFragment 생성 및 설정
                MyPostFragment myPostFragment = new MyPostFragment();

                if (mainActivity != null) {
                    // MainActivity의 프래그먼트 매니저를 사용하여 RidingCheckFragment를 containers에 추가
                    mainActivity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.containers, myPostFragment)
                            .commit();
                }
            }
        });

        btnDeparture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DepartureSearchDialogFragment dialogFragment = new DepartureSearchDialogFragment();
                dialogFragment.show(getChildFragmentManager(), "departure_search_dialog");
            }
        });

        btnArrival.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrivalSearchDialogFragment dialogFragment = new ArrivalSearchDialogFragment();
                dialogFragment.show(getChildFragmentManager(), "arrival_search_dialog");
            }
        });

        return view;
    }

    private void updatePost(String title, String content, String departureLocation, String destinationLocation, String recruitmentStatus, int allSeat, String departureTime, Long PostId) {
        String accessToken = tokenManager.getAccessToken();
        PostUpdateRequestDto requestDto = new PostUpdateRequestDto(title, content, departureLocation, destinationLocation, recruitmentStatus, allSeat, departureTime);

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
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://121.200.87.205:8000/") // 스프링부트 API의 기본 URL을 설정
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            API apiService = retrofit.create(API.class);
            Call<ResponseDto<Boolean>> call = apiService.updatePost("Bearer " + accessToken, requestDto, PostId);
            call.enqueue(new Callback<ResponseDto<Boolean>>() {
                @Override
                public void onResponse(Call<ResponseDto<Boolean>> call, Response<ResponseDto<Boolean>> response) {
                    if (response.isSuccessful()) {
                        ResponseDto<Boolean> responseDto = response.body();
                        int statusCode = responseDto.getStatus();
                        String msg = responseDto.getMsg();

                        if (statusCode == 200) {
                            MainActivity mainActivity = (MainActivity) getActivity();
                            showToast("글 수정 완료");

                        } else if (statusCode == 423) { // 만료된 토큰이라면?
                            MainActivity mainActivity = (MainActivity) getActivity();
                            LoginFragment loginFragment = new LoginFragment();
                            if (mainActivity != null) {
                                mainActivity.getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.containers, loginFragment)
                                        .addToBackStack(null)
                                        .commit();
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

    // toast 보여주기
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    // onComplete 메서드를 구현합니다.
    @Override
    public void onArrivalComplete(String arrivalLocation) {
        // etArrival 필드의 값을 설정합니다.
        etArrival.setText(arrivalLocation);
    }

    // onComplete 메서드를 구현합니다.
    @Override
    public void onComplete(String departureLocation) {
        // etArrival 필드의 값을 설정합니다.
        etDeparture.setText(departureLocation);
    }
}