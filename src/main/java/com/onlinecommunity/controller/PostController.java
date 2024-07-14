package com.onlinecommunity.controller;

import com.onlinecommunity.domain.post.ForDeletePost;
import com.onlinecommunity.domain.post.ForRegisterPost;
import com.onlinecommunity.domain.post.ForUpdatePost;
import com.onlinecommunity.domain.post.complaint.ForDeleteComplaintPost;
import com.onlinecommunity.domain.post.complaint.ForRegisterComplaintPost;
import com.onlinecommunity.service.PostService;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    // 게시글 작성
    @PostMapping("")
    public ResponseEntity<?> registerPost(@Validated @RequestBody ForRegisterPost request) {
        var result = this.postService.registerPost(request);
        return ResponseEntity.ok(result);
    }

    // 고유번호를 기반으로 게시글 조회
    @GetMapping("/information")
    public ResponseEntity<?> getPost(@RequestParam @PositiveOrZero int id) {
        var result = this.postService.getPost(id);
        return ResponseEntity.ok(result);
    }

    // 게시글 목록 조회 (로그인 없이도 가능) , 작성시간을 기준으로 내림차순 출력
    // title로 검색시 title이 들어있는 제목을 가진 게시글들만 조회 가능하다.
    // context로 검색시 context이 들어있는 내용을 가진 게시글들만 조회 가능하다.
    // title, context둘 다 사용할 때는 title이 들어있는 제목을 가진 게시글이랑 context이 들어있는 내용을 가진 게시글들을 모두 조회한다.
    @GetMapping("/list")
    public ResponseEntity<?> getPosts(Pageable pageable, @RequestParam @PositiveOrZero int topicid, @RequestParam(required = false) String title, @RequestParam(required = false) String context) {
        var result = this.postService.getPosts(pageable, topicid, title, context);
        return ResponseEntity.ok(result);
    }

    // 내가 작성한 게시글 목록 조회, 작성시간을 기준으로 내림차순 출력
    @GetMapping("mylist")
    public ResponseEntity<?> getMyPosts(Pageable pageable) {
        var result = this.postService.getMyPosts(pageable);
        return ResponseEntity.ok(result);
    }
    // 게시글 수정
    @PutMapping("")
    public ResponseEntity<?> updatePost(@Validated @RequestBody ForUpdatePost request) {
        var result = this.postService.updatePost(request);
        return ResponseEntity.ok(result);
    }

    // 게시글에 좋아요 (한 계정당 1번만 가능, 좋아요와 싫어요 둘 중 하나만 선택 가능하며 추후에 변경 가능)
    @PutMapping("like")
    public ResponseEntity<?> likePost(@RequestParam @PositiveOrZero int id) {
        var result = this.postService.likePost(id, true);
        return ResponseEntity.ok(result);
    }

    // 특정 게시글에 대해 사용자가 좋아요 또는 싫어요를 눌렀는지 정보 조회
    @GetMapping("like")
    public ResponseEntity<?> getScorePost(@RequestParam @PositiveOrZero int id) {
        var result = this.postService.getScorePost(id);
        return ResponseEntity.ok(result);
    }

    // 게시글에 싫어요 (한 계정당 1번만 가능, 좋아요와 싫어요 둘 중 하나만 선택 가능하며 추후에 변경 가능)
    @PutMapping("dislike")
    public ResponseEntity<?> dislikePost(@RequestParam @PositiveOrZero int id) {
        var result = this.postService.likePost(id, false);
        return ResponseEntity.ok(result);
    }

    // 게시글에 눌렀던 좋아요 or 싫어요 취소
    @PutMapping("cancellike")
    public ResponseEntity<?> cancellikePost(@RequestParam @PositiveOrZero int id) {
        var result = this.postService.cancellikePost(id);
        return ResponseEntity.ok(result);
    }

    // 게시글에 대한 신고글 작성
    @PostMapping("complaint")
    public ResponseEntity<?> complaintPost(@Validated @RequestBody ForRegisterComplaintPost request) {
        var result = this.postService.complaintPost(request);
        return ResponseEntity.ok(result);
    }

    // 게시글에 대한 신고글 조회 (관리자만 가능)
    @GetMapping("complaint/information")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getComplaintPost(@RequestParam @PositiveOrZero int id) {
        var result = this.postService.getComplaintPost(id);
        return ResponseEntity.ok(result);
    }

    // 게시글에 대한 신고글 목록 조회 (관리자만 가능, 등록날짜를 기준으로 내림차순 조회)
    @GetMapping("complaint/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getComplaintPosts(Pageable pageable) {
        var result = this.postService.getComplaintPosts(pageable);
        return ResponseEntity.ok(result);
    }

    // 게시글에 대한 신고글 삭제 (관리자만 가능)
    @DeleteMapping("complaint")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteComplaintPost(@Validated @RequestBody ForDeleteComplaintPost request) {
        var result = this.postService.deleteComplaintPost(request);
        return ResponseEntity.ok(result);

    }

    // 게시글 삭제 (작성자 본인과 관리자만 가능)
    @DeleteMapping("")
    public ResponseEntity<?> deletePost(@Validated @RequestBody ForDeletePost request) {
        var result = this.postService.deletePost(request);
        return ResponseEntity.ok(result);
    }
}
