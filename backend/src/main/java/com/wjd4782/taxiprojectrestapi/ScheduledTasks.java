package com.wjd4782.taxiprojectrestapi;

import com.wjd4782.taxiprojectrestapi.domain.Post;
import com.wjd4782.taxiprojectrestapi.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final PostRepository postRepository;

    @Scheduled(cron = "0 * * * * ?") // 매 분마다 실행하도록 설정 (매 시간 0분)
    public void updatePostStatus() {
        List<Post> posts = postRepository.findAll();
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        for (Post post : posts) {
            if (post.getDepartureTime().before(currentTime)) {
                post.setRecruitmentStatus("모집완료");
                postRepository.save(post);
            }
        }
    }
}