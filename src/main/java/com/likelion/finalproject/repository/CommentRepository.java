package com.likelion.finalproject.repository;

import com.likelion.finalproject.domain.entity.Comment;
import com.likelion.finalproject.domain.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Page<Comment> findCommentsByPost(Post post, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("update Comment c set c.comment = :comment, c.lastModifiedAt = current_timestamp where c.id = :commentId ")
    void update(@Param("comment") String comment, @Param("commentId") Integer id) ;

    void deleteAllByPost(Post post);

    List<Comment> findAllByPost(Post post);
}
