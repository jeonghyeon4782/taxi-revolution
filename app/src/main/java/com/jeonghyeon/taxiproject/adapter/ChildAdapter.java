package com.jeonghyeon.taxiproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jeonghyeon.taxiproject.R;
import com.jeonghyeon.taxiproject.activity.MainActivity;
import com.jeonghyeon.taxiproject.api.API;
import com.jeonghyeon.taxiproject.dto.response.BelongMemberResponseDto;
import com.jeonghyeon.taxiproject.dto.response.BelongPostResponseDto;
import com.jeonghyeon.taxiproject.dto.response.ResponseDto;
import com.jeonghyeon.taxiproject.fragment.LoginFragment;
import com.jeonghyeon.taxiproject.token.TokenManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ViewHolder>{
    private List<BelongMemberResponseDto> memberList;
    private BelongPostResponseDto parentDto;
    private TokenManager tokenManager;
    private Context context; // Context를 저장할 멤버 변수

    // 생성자에 Context와 데이터를 전달받도록 수정
    public ChildAdapter(Context context, List<BelongMemberResponseDto> memberList) {
        this.context = context; // Context를 저장
        this.memberList = memberList;
        this.tokenManager = new TokenManager(context); // TokenManager 초기화
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_child, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BelongMemberResponseDto member = memberList.get(position);
        holder.bind(member);

        // BelongMemberResponseDto의 authority 값이 1이고, 부모의 BelongPostResponseDto의 authority 값이 1인 경우에만 btnLink를 안보이도록 처리
        if (member.getAuthority() == 1 && parentDto != null && parentDto.getAuthority() == 1) {
            holder.btnLink.setVisibility(View.GONE);
        } else {
            holder.btnLink.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nicknameTextView;
        private ImageView genderImageView;
        private ImageButton btnLink;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nicknameTextView = itemView.findViewById(R.id.txt_nickname);
            genderImageView = itemView.findViewById(R.id.genderImageView);
            btnLink = itemView.findViewById(R.id.btn_link);

            // btnLink 버튼의 클릭 이벤트 처리
            btnLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BelongMemberResponseDto member = memberList.get(getAdapterPosition());
                    deleteBelongAPI(member.getBelongId());
                }
            });
        }

        public void bind(BelongMemberResponseDto member) {
            nicknameTextView.setText(member.getNickname());
            if (member.getAuthority() == 0) {
                btnLink.setImageResource(R.drawable.baseline_star_24);
            } else {
                btnLink.setImageResource(R.drawable.baseline_cancel_24);
            }
            if (member.getGender() == 0) {
                genderImageView.setImageResource(R.drawable.baseline_face_4_24);
            } else {
                genderImageView.setImageResource(R.drawable.baseline_face_6_24);
            }
        }
    }

    public void setChildAdapterParentDto(BelongPostResponseDto parentDto) {
        this.parentDto = parentDto;
        notifyDataSetChanged(); // 부모 데이터가 설정될 때마다 RecyclerView를 새로 고침
    }

    // setParentDto 메서드 정의
    public void setParentDto(BelongPostResponseDto parentDto) {
        this.parentDto = parentDto;
    }

    private void deleteBelongAPI(Long belongId) {
        String accessToken = tokenManager.getAccessToken();

        if (accessToken == null) {
            MainActivity mainActivity = (MainActivity) context;
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
            Call<ResponseDto<Boolean>> call = apiService.deleteBelong("Bearer " + accessToken, belongId);
            call.enqueue(new Callback<ResponseDto<Boolean>>() {
                @Override
                public void onResponse(Call<ResponseDto<Boolean>> call, Response<ResponseDto<Boolean>> response) {
                    if (response.isSuccessful()) {
                        ResponseDto<Boolean> responseDto = response.body();
                        int statusCode = responseDto.getStatus();
                        String msg = responseDto.getMsg();
                        if (statusCode == 200) {
                            showToast(msg);
                        } else if (statusCode == 423) { // 만료된 토큰이라면?
                            MainActivity mainActivity = (MainActivity) context;
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
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
