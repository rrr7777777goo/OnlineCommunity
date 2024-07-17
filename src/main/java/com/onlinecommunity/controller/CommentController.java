package com.onlinecommunity.controller;

import com.onlinecommunity.domain.comment.ForDeleteComment;
import com.onlinecommunity.domain.comment.ForRegisterComment;
import com.onlinecommunity.domain.comment.ForUpdateComment;
import com.onlinecommunity.domain.comment.complaint.ForDeleteComplaintComment;
import com.onlinecommunity.domain.comment.complaint.ForRegisterComplaintComment;
import com.onlinecommunity.domain.comment.score.ForRegisterScoreComment;
import com.onlinecommunity.service.CommentService;
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
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("")
    public ResponseEntity<?> registerComment(@Validated @RequestBody ForRegisterComment request) {
        var result = this.commentService.registerComment(request);
        return ResponseEntity.ok(result);
    }

    // 고유번호를 기반으로 댓글 조회
    @GetMapping("/information")
    public ResponseEntity<?> getComment(@RequestParam @PositiveOrZero int id) {
        var result = this.commentService.getComment(id);
        return ResponseEntity.ok(result);
    }

    // 댓글 목록 조회 (로그인 없이도 가능) , 작성시간을 기준으로 내림차순 출력
    @GetMapping("/list")
    public ResponseEntity<?> getComments(Pageable pageable, @RequestParam @PositiveOrZero int postid) {
        var result = this.commentService.getComments(pageable, postid);
        return ResponseEntity.ok(result);
    }

    // 내가 작성한 댓글 목록 조회, 작성시간을 기준으로 내림차순 출력
    @GetMapping("/mylist")
    public ResponseEntity<?> getMyComments(Pageable pageable) {
        var result = this.commentService.getMyComments(pageable);
        return ResponseEntity.ok(result);
    }

    // 댓글 수정
    @PutMapping("")
    public ResponseEntity<?> updateComment(@Validated @RequestBody ForUpdateComment request) {
        var result = this.commentService.updateComment(request);
        return ResponseEntity.ok(result);
    }

    // 댓글에 좋아요 (한 계정당 1번만 가능, 좋아요와 싫어요 둘 중 하나만 선택 가능하며 추후에 변경 가능)
    @PutMapping("/like")
    public ResponseEntity<?> likeComment(@Validated @RequestBody ForRegisterScoreComment request) {
        var result = this.commentService.likeComment(request.getId(), true);
        return ResponseEntity.ok(result);
    }

    // 특정 댓글에 대해 사용자가 좋아요 또는 싫어요를 눌렀는지 정보 조회
    @GetMapping("/like")
    public ResponseEntity<?> getScoreComment(@RequestParam @PositiveOrZero int id) {
        var result = this.commentService.getScoreComment(id);
        return ResponseEntity.ok(result);
    }

    // 댓글에 싫어요 (한 계정당 1번만 가능, 좋아요와 싫어요 둘 중 하나만 선택 가능하며 추후에 변경 가능)
    @PutMapping("/dislike")
    public ResponseEntity<?> dislikeComment(@Validated @RequestBody ForRegisterScoreComment request) {
        var result = this.commentService.likeComment(request.getId(), false);
        return ResponseEntity.ok(result);
    }

    // 댓글에 눌렀던 좋아요 or 싫어요 취소
    @PutMapping("/cancel-like")
    public ResponseEntity<?> cancelLikeComment(@Validated @RequestBody ForRegisterScoreComment request) {
        var result = this.commentService.cancelLikeComment(request.getId());
        return ResponseEntity.ok(result);
    }

    // 댓글에 대한 신고글 작성
    @PostMapping("/complaint")
    public ResponseEntity<?> complaintComment(@Validated @RequestBody ForRegisterComplaintComment request) {
        var result = this.commentService.complaintComment(request);
        return ResponseEntity.ok(result);
    }

    // 댓글에 대한 신고글 조회 (관리자만 가능)
    @GetMapping("/complaint/information")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getComplaintComment(@RequestParam @PositiveOrZero int id) {
        var result = this.commentService.getComplaintComment(id);
        return ResponseEntity.ok(result);
    }

    // 댓글에 대한 신고글 목록 조회 (관리자만 가능, 등록날짜를 기준으로 내림차순 조회)
    @GetMapping("/complaint/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getComplaintComments(Pageable pageable) {
        var result = this.commentService.getComplaintComments(pageable);
        return ResponseEntity.ok(result);
    }

    // 댓글에 대한 신고글 삭제 (관리자만 가능)
    @DeleteMapping("/complaint")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteComplaintComment(@Validated @RequestBody ForDeleteComplaintComment request) {
        var result = this.commentService.deleteComplaintComment(request);
        return ResponseEntity.ok(result);

    }

    // 댓글 삭제 (작성자 본인과 관리자만 가능)
    @DeleteMapping("")
    public ResponseEntity<?> deleteComment(@Validated @RequestBody ForDeleteComment request) {
        var result = this.commentService.deleteComment(request);
        return ResponseEntity.ok(result);
    }
}
