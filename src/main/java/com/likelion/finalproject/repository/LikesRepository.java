package com.likelion.finalproject.repository;

import com.likelion.finalproject.domain.entity.Likes;
import com.likelion.finalproject.domain.entity.Post;
import com.likelion.finalproject.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Integer> {
    Optional<Likes> findLikeByUserAndPost(User user, Post post);

    @Query("select count(l) from Likes l where l.deletedAt is null and l.post = :post")
    int countLikesByPost(@Param("post") Post post);

    void deleteAllByPost(Post post);

    List<Likes> findAllByPost(Post post);

    Optional<Likes> findByPostId(Integer postId);

    Optional<Likes> findByPostIdAndUserId(Integer postId, Integer userId);
}
