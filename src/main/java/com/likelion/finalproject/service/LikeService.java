package com.likelion.finalproject.service;

import com.likelion.finalproject.domain.entity.Alarm;
import com.likelion.finalproject.domain.entity.Like;
import com.likelion.finalproject.domain.entity.Post;
import com.likelion.finalproject.domain.entity.User;
import com.likelion.finalproject.repository.AlarmRepository;
import com.likelion.finalproject.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.likelion.finalproject.domain.enums.AlarmType.NEW_LIKE_ON_POST;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final AlarmRepository alarmRepository;
    private final Services service;

    public String increaseLike(Integer postId, String userName) {
        Post post = service.validateGetPostById(postId);
        User user = service.validateGetUserByUserName(userName);

        Optional<Like> like = likeRepository.findLikeByUserAndPost(user, post);

        if (like.isPresent()) {
            likeRepository.delete(like.get());
            return "좋아요를 취소했습니다.";
        }else{
            likeRepository.save(Like.toEntity(post, user));
            Alarm alarm = alarmRepository.findAlarmByFromUserIdAndTargetId(user.getId(), post.getId())
                    .orElse(alarmRepository.save(Alarm.toEntity(user, post, NEW_LIKE_ON_POST)));
            return "좋아요를 눌렀습니다";
        }
    }
}
