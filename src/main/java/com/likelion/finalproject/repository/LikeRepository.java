package com.likelion.finalproject.repository;

import com.likelion.finalproject.domain.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Integer> {
}
