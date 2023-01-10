package com.likelion.finalproject.service;

import com.likelion.finalproject.domain.entity.Alarm;
import com.likelion.finalproject.domain.entity.Likes;
import com.likelion.finalproject.domain.entity.Post;
import com.likelion.finalproject.domain.entity.User;
import com.likelion.finalproject.repository.AlarmRepository;
import com.likelion.finalproject.repository.LikesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

import static com.likelion.finalproject.domain.enums.AlarmType.NEW_COMMENT_ON_POST;
import static com.likelion.finalproject.domain.enums.AlarmType.NEW_LIKE_ON_POST;

@Service
@RequiredArgsConstructor
public class LikesService {

    private final LikesRepository likeRepository;
    private final AlarmRepository alarmRepository;
    private final ValidateService service;

    /**
     * 좋아요 상태를 변경하는 메서드
     * 좋아요를 누른 상태가 아니라면 좋아요를 증가시킵니다.
     * 좋아요를 누른 상태라면 좋아요를 취소합니다.(감소합니다)
     *
     * @param postId 좋아요를 누른 게시물 id
     * @param userName 좋아요를 누른 user
     * @return
     */
    @Transactional
    public String increaseLike(Integer postId, String userName) {
        Post post = service.validateGetPostById(postId);
        User user = service.validateGetUserByUserName(userName);

        Optional<Likes> optionalLike = likeRepository.findLikeByUserAndPost(user, post);
        Likes like;

        // 좋아요 기록이 있는지 확인합니다.
        if (optionalLike.isPresent()) {
            like = optionalLike.get();
        } else {
            // 좋아요 기록이 없다면 처음 좋아요를 누른 것이므로 좋아요 기록을 저장합니다.
            like = likeRepository.save(Likes.toEntity(post, user));
            Optional<Likes> checkLike = likeRepository.findById(like.getId());

            // 좋아요 기록이 DB에 잘 저장되었다면, Alarm을 보냅니다.
            if (checkLike.isPresent()) {
                if (!Objects.equals(like.getUser().getUserName(), user.getUserName())) {
                    Alarm alarm = alarmRepository.findAlarmByFromUserIdAndTargetIdAndAlarmType(user.getId(), post.getId(), NEW_LIKE_ON_POST)
                            .orElse(alarmRepository.save(Alarm.toEntity(user, post, NEW_LIKE_ON_POST)));
                }
            }
            return "좋아요를 눌렀습니다";
        }

        // 받아온 like 기록중 getDeletedAt의 정보를 확인합니다.
        if (like.getDeletedAt() == null) {
            // 이미 like를 한 적이 있는데 getDeletedAt이 NULL이라면 다시 한 번 버튼을 누른 것이므로 좋아요를 취소합니다.
            likeRepository.delete(like);
            // like를 soft delete 처리한 후 알람 기록도 삭제합니다.
            if (!Objects.equals(like.getUser().getUserName(), user.getUserName())) {
                Optional<Alarm> alarm = alarmRepository.findAlarmByFromUserIdAndTargetIdAndAlarmType(user.getId(), post.getId(), NEW_LIKE_ON_POST);
                alarm.ifPresent(alarmRepository::delete);
            }
            return "좋아요를 취소했습니다.";
        } else {
            // 이미 like를 한 기록이 있는데 getDeletedAt이 있다면, 좋아요를 취소한 상태에서 다시 좋아요 버튼을 누른 상황입니다.
            // deletedAt 기록을 삭제합니다.
            like.cancelDeletion();

            // 다시 알람을 보냅니다.
            if (!Objects.equals(like.getUser().getUserName(), user.getUserName())) {
                Alarm alarm = alarmRepository.findAlarmByFromUserIdAndTargetIdAndAlarmType(user.getId(), post.getId(), NEW_LIKE_ON_POST)
                        .orElse(alarmRepository.save(Alarm.toEntity(user, post, NEW_LIKE_ON_POST)));
            }

            return "좋아요를 눌렀습니다";
        }
    }

    /**
     * 좋아요 수를 세는 메서드
     *
     * @param postId 좋아요 수를 확인할 게시물의 id
     * @return 좋아요 수를 반환합니다.
     */
    public int getLikeCount(Integer postId) {
        Post post = service.validateGetPostById(postId);
        return likeRepository.countLikesByPost(post);
    }
}
