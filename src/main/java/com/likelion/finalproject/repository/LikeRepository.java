package com.likelion.finalproject.repository;

import com.likelion.finalproject.domain.entity.Like;
import com.likelion.finalproject.domain.entity.Post;
import com.likelion.finalproject.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Integer> {
    Optional<Like> findLikeByUserAndPost(User user, Post post);
}
