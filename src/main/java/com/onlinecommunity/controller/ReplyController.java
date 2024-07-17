package com.onlinecommunity.controller;

import com.onlinecommunity.domain.reply.ForDeleteReply;
import com.onlinecommunity.domain.reply.ForRegisterReply;
import com.onlinecommunity.domain.reply.ForUpdateReply;
import com.onlinecommunity.domain.reply.complaint.ForDeleteComplaintReply;
import com.onlinecommunity.domain.reply.complaint.ForRegisterComplaintReply;
import com.onlinecommunity.domain.reply.score.ForRegisterScoreReply;
import com.onlinecommunity.service.ReplyService;
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
@RequestMapping("/reply")
@RequiredArgsConstructor
public class ReplyController {
    private final ReplyService replyService;

    // 대댓글 작성
    @PostMapping("")
    public ResponseEntity<?> registerReply(@Validated @RequestBody ForRegisterReply request) {
        var result = this.replyService.registerReply(request);
        return ResponseEntity.ok(result);
    }

    // 고유번호를 기반으로 대댓글 조회
    @GetMapping("/information")
    public ResponseEntity<?> getReply(@RequestParam @PositiveOrZero int id) {
        var result = this.replyService.getReply(id);
        return ResponseEntity.ok(result);
    }

    // 대댓글 목록 조회 (로그인 없이도 가능) , 작성시간을 기준으로 내림차순 출력
    @GetMapping("/list")
    public ResponseEntity<?> getReplies(Pageable pageable, @RequestParam @PositiveOrZero int commentid) {
        var result = this.replyService.getReplies(pageable, commentid);
        return ResponseEntity.ok(result);
    }

    // 내가 작성한 대댓글 목록 조회, 작성시간을 기준으로 내림차순 출력
    @GetMapping("/mylist")
    public ResponseEntity<?> getMyReplies(Pageable pageable) {
        var result = this.replyService.getMyReplies(pageable);
        return ResponseEntity.ok(result);
    }

    // 대댓글 수정
    @PutMapping("")
    public ResponseEntity<?> updateReply(@Validated @RequestBody ForUpdateReply request) {
        var result = this.replyService.updateReply(request);
        return ResponseEntity.ok(result);
    }

    // 대댓글에 좋아요 (한 계정당 1번만 가능, 좋아요와 싫어요 둘 중 하나만 선택 가능하며 추후에 변경 가능)
    @PutMapping("/like")
    public ResponseEntity<?> likeReply(@Validated @RequestBody ForRegisterScoreReply request) {
        var result = this.replyService.likeReply(request.getId(), true);
        return ResponseEntity.ok(result);
    }

    // 특정 대댓글에 대해 사용자가 좋아요 또는 싫어요를 눌렀는지 정보 조회
    @GetMapping("/like")
    public ResponseEntity<?> getScoreReply(@RequestParam @PositiveOrZero int id) {
        var result = this.replyService.getScoreReply(id);
        return ResponseEntity.ok(result);
    }

    // 대댓글에 싫어요 (한 계정당 1번만 가능, 좋아요와 싫어요 둘 중 하나만 선택 가능하며 추후에 변경 가능)
    @PutMapping("/dislike")
    public ResponseEntity<?> dislikeReply(@Validated @RequestBody ForRegisterScoreReply request) {
        var result = this.replyService.likeReply(request.getId(), false);
        return ResponseEntity.ok(result);
    }

    // 대댓글에 눌렀던 좋아요 or 싫어요 취소
    @PutMapping("/cancel-like")
    public ResponseEntity<?> cancelLikeReply(@Validated @RequestBody ForRegisterScoreReply request) {
        var result = this.replyService.cancelLikeReply(request.getId());
        return ResponseEntity.ok(result);
    }

    // 대댓글에 대한 신고글 작성
    @PostMapping("/complaint")
    public ResponseEntity<?> complaintReply(@Validated @RequestBody ForRegisterComplaintReply request) {
        var result = this.replyService.complaintReply(request);
        return ResponseEntity.ok(result);
    }

    // 대댓글에 대한 신고글 조회 (관리자만 가능)
    @GetMapping("/complaint/information")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getComplaintReply(@RequestParam @PositiveOrZero int id) {
        var result = this.replyService.getComplaintReply(id);
        return ResponseEntity.ok(result);
    }

    // 대댓글에 대한 신고글 목록 조회 (관리자만 가능, 등록날짜를 기준으로 내림차순 조회)
    @GetMapping("/complaint/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getComplaintReplies(Pageable pageable) {
        var result = this.replyService.getComplaintReplies(pageable);
        return ResponseEntity.ok(result);
    }

    // 대댓글에 대한 신고글 삭제 (관리자만 가능)
    @DeleteMapping("/complaint")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteComplaintReply(@Validated @RequestBody ForDeleteComplaintReply request) {
        var result = this.replyService.deleteComplaintReply(request);
        return ResponseEntity.ok(result);

    }

    // 대댓글 삭제 (작성자 본인과 관리자만 가능)
    @DeleteMapping("")
    public ResponseEntity<?> deleteReply(@Validated @RequestBody ForDeleteReply request) {
        var result = this.replyService.deleteReply(request);
        return ResponseEntity.ok(result);
    }
}
