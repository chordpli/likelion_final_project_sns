package com.likelion.finalproject.repository;

import com.likelion.finalproject.domain.entity.Post;
import com.likelion.finalproject.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer> {
    Page<Post> findPostsByUser(User user, Pageable pageable);
}