package com.likelion.finalproject.service;

import com.likelion.finalproject.domain.dto.post.PostModifyRequest;
import com.likelion.finalproject.domain.dto.post.PostReadResponse;
import com.likelion.finalproject.domain.dto.post.PostRequest;
import com.likelion.finalproject.domain.dto.post.PostResponse;
import com.likelion.finalproject.domain.entity.Post;
import com.likelion.finalproject.domain.entity.User;
import com.likelion.finalproject.exception.SNSAppException;
import com.likelion.finalproject.repository.CommentRepository;
import com.likelion.finalproject.repository.LikesRepository;
import com.likelion.finalproject.repository.PostRepository;
import com.likelion.finalproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikesRepository likesRepository;
    private final CommentRepository commentRepository;
    private final ValidateService service;

    /**
     * dto의 내용으로 userName 사용자의 게시글을 작성합니다.
     * @param dto
     * @param userName
     * @return
     */
    @Transactional
    public PostResponse post(PostRequest dto, String userName) {
        // user가 찾아지지 않는다면 등록할 수 없다.
        User user = service.validateGetUserByUserName(userName);

        Post post = Post.builder()
                .user(user)
                .title(dto.getTitle())
                .body(dto.getBody())
                .build();

        postRepository.save(post);
        return new PostResponse("포스트 등록 완료", post.getId());
    }

    /**
     * 작성된 게시글을 상세 조회 합니다.
     * @param postId
     * @return
     */
    @Transactional
    public PostReadResponse getPost(Integer postId) {
        Post readPost = service.validateGetPostById(postId);
        return PostReadResponse.of(readPost);
    }

    /**
     * 작성된 모든 게시글을 불러옵니다.
     * @param pageRequest
     * @return
     */
    @Transactional
    public List<PostReadResponse> getAllPost(PageRequest pageRequest) {
        Page<Post> posts = postRepository.findAll(pageRequest);
        System.out.println(posts.getPageable());
        return posts.stream()
                .map(Post::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 해당 postId를 갖고 있는 Post를 dto의 내용대로 수정합니다.
     *
     * @param postId
     * @param dto
     * @param userName
     * @throws SNSAppException
     */
    @Transactional
    public void modifyPost(Integer postId, PostModifyRequest dto, String userName) throws SNSAppException {
        User user = service.validateGetUserByUserName(userName);
        Post post = service.validateGetPostById(postId);
        service.validateCheckAdminAndEqualWriter(user, post);
        postRepository.save(dto.toEntity(postId, post.getUser()));
    }

    /**
     * 해당 postId를 갖고 있는 Post를 삭제합니다.
     *
     * @param postId
     * @param userName
     */
    @Transactional
    public void deletePost(Integer postId, String userName) {
        User user = service.validateGetUserByUserName(userName);
        Post post = service.validateGetPostById(postId);
        service.validateCheckAdminAndEqualWriter(user, post);
        commentRepository.deleteAllByPost(post);
        likesRepository.deleteAllByPost(post);
        postRepository.delete(post);
    }

    @Transactional
    public List<PostReadResponse> getMyAllPost(String userName, PageRequest pageable) {
        User user = service.validateGetUserByUserName(userName);
        Page<Post> myFeeds = postRepository.findPostsByUser(user, pageable);
        return myFeeds.stream()
                .map(Post::toResponse)
                .collect(Collectors.toList());
    }
}