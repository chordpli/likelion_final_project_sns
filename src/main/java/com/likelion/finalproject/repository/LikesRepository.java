package com.likelion.finalproject.repository;

import com.likelion.finalproject.domain.entity.Likes;
import com.likelion.finalproject.domain.entity.Post;
import com.likelion.finalproject.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Integer> {
    Optional<Likes> findLikeByUserAndPost(User user, Post post);
}
