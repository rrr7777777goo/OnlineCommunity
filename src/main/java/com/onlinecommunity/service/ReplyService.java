package com.onlinecommunity.service;

import com.onlinecommunity.domain.ScoreStatus;
import com.onlinecommunity.domain.member.Auth;
import com.onlinecommunity.domain.member.MemberRole;
import com.onlinecommunity.domain.reply.*;
import com.onlinecommunity.domain.reply.complaint.ComplaintReply;
import com.onlinecommunity.domain.reply.complaint.ForDeleteComplaintReply;
import com.onlinecommunity.domain.reply.complaint.ForRegisterComplaintReply;
import com.onlinecommunity.domain.reply.score.ScoreReply;
import com.onlinecommunity.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class ReplyService {
    private final CommentRepository commentRepository;

    private final ReplyRepository replyRepository;
    private final ScoreReplyRepository scoreReplyRepository;
    private final ComplaintReplyRepository complaintReplyRepository;

    // 현재 로그인한 사람의 아이디 관련 정보를 가져오는 함수
    private Auth.IdInterface getIdInterface() {
        Auth.IdInterface idInterface = (Auth.IdInterface) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return idInterface;
    }

    // 현재 로그인 한 계정의 권한 정보 가져오기
    private MemberRole getRole() {
        MemberRole memberRole = null;

        int cnt = 0;
        for(GrantedAuthority ga : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
            memberRole = MemberRole.getEnum(ga.getAuthority());
            ++cnt;
            if (cnt > 1) {
                throw new RuntimeException("권한 정보가 1개를 초과하였습니다.");
            }
        }
        if (cnt == 0) {
            throw new RuntimeException("현재 계정에는 어떠한 권한 정보도 존재하지 않습니다.");
        }

        return memberRole;
    }

    // 대댓글 저장
    public Reply registerReply(ForRegisterReply request) {

        boolean exists = this.commentRepository.existsById(request.getCommentId());
        if (!exists) {
            throw new RuntimeException("존재하지 않는 댓글 ID입니다. -> " + request.getCommentId());
        }

        var result = this.replyRepository.save(request.toEntity(getIdInterface().getId()));
        return result;
    }

    // 고유번호를 기반으로 한 대댓글 조회
    public ForResponseReply getReply(int id) {
        var result = this.replyRepository.findAllByIdAndReturnForResponseComment(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 대댓글 ID입니다. -> " + id));
        return result;
    }

    // 대댓글 목록 조회 (로그인 없이도 가능) , 작성시간을 기준으로 내림차순 출력
    public Page<ForResponseReply> getReplies(Pageable pageable, int commentId) {
        boolean exists = this.commentRepository.existsById(commentId);
        if (!exists) {
            throw new RuntimeException("존재하지 않는 댓글 ID입니다. -> " + commentId);
        }

        var result = this.replyRepository.findAllByOrderByInsertDateDescAndReturnForResponseComment(pageable, commentId);
        return result;
    }

    public Page<ForResponseReply> getMyReplies(Pageable pageable) {
        var result = this.replyRepository.findAllByUserIdAndReturnForResponsePost(pageable, getIdInterface().getId());
        return result;
    }

    // 대댓글 수정
    @Transactional
    public ForResponseReply updateReply(ForUpdateReply request) {
        var post = this.replyRepository.findAllByIdAndUserId(request.getId(), getIdInterface().getId())
                .orElseThrow(() -> new RuntimeException("현재 대댓글 정보가 존재하지 않거나 로그인한 계정에서 접근할 수 있는 권한이 없습니다."));
        post.updateComment(request.getContext());

        var result = this.replyRepository.findAllByIdAndReturnForResponseComment(request.getId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 대댓글 ID입니다. -> " + request.getId()));
        return result;
    }

    // 대댓글에 좋아요 또는 싫어요 (한 계정당 1번만 가능, 좋아요와 싫어요 둘 중 하나만 선택 가능하며 추후에 변경 가능)
    @Transactional
    public ForResponseReply likeReply(int id, boolean isLike) {
        var userId = getIdInterface().getId();
        var scoreReply = this.scoreReplyRepository.findAllByReplyIdAndUserId(id, userId);

        if (scoreReply == null) {
            var reply = this.replyRepository.findAllById(id)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 대댓글 ID입니다. -> " + id));

            if (isLike) {
                reply.plusLikeScore();
            } else {
                reply.plusDislikeScore();
            }
            this.scoreReplyRepository.save(ScoreReply.builder()
                    .replyId(id)
                    .userId(userId)
                    .status(isLike ? ScoreStatus.SCORE_LIKE : ScoreStatus.SCORE_DISLIKE)
                    .build());
        } else {
            scoreReply.changeScore(isLike);
            var comment = this.replyRepository.findAllById(id)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 대댓글 ID입니다. -> " + id));
            comment.changeLikeScore(isLike);
        }

        var result = this.replyRepository.findAllByIdAndReturnForResponseComment(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 대댓글 ID입니다. -> " + id));
        return result;
    }

    // 대댓글에 사용자가 좋아요 또는 싫어요를 눌렀는지 조회
    public ScoreReply getScoreReply(int id) {
        var userInterface = getIdInterface();
        var result = this.scoreReplyRepository.findAllByReplyIdAndUserId(id, userInterface.getId());

        if (result == null) {
            var exists = this.replyRepository.existsAllById(id);
            if (!exists) {
                throw new RuntimeException("존재하지 않는 대댓글 ID입니다. -> " + id);
            } else {
                throw new RuntimeException("현재 사용자는 해당 글에 좋아요, 싫어요 둘 중 어느것도 선택하지 않은 상태입니다. -> " + userInterface.getSignupId());
            }
        }

        return result;
    }

    // 대댓글에 눌렀던 좋아요 or 싫어요 취소
    @Transactional
    public ForResponseReply cancelLikeReply(int id) {
        var userInterface = getIdInterface();
        var scoreReply = this.scoreReplyRepository.findAllByReplyIdAndUserId(id, userInterface.getId());

        if (scoreReply == null) {
            var exists = this.replyRepository.existsAllById(id);
            if (!exists) {
                throw new RuntimeException("존재하지 않는 대댓글 ID입니다. -> " + id);
            } else {
                throw new RuntimeException("현재 사용자는 해당 글에 좋아요, 싫어요 둘 중 어느것도 선택하지 않은 상태입니다. -> " + userInterface.getSignupId());
            }
        } else {
            var reply = this.replyRepository.findAllById(id)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 대댓글 ID입니다. -> " + id));
            if (scoreReply.getStatus() == ScoreStatus.SCORE_LIKE) {
                reply.minusLikeScore();
            } else {
                reply.minusDislikeScore();
            }
            this.scoreReplyRepository.delete(scoreReply);
        }

        var result = this.replyRepository.findAllByIdAndReturnForResponseComment(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 대댓글 ID입니다. -> " + id));
        return result;
    }

    // 대댓글에 대한 신고글 작성
    public ComplaintReply complaintReply(ForRegisterComplaintReply request) {
        var exists = this.replyRepository.existsAllById(request.getReplyId());
        if (!exists) {
            throw new RuntimeException("존재하지 않는 대댓글 ID입니다. -> " + request.getReplyId());
        }

        var userInterface = getIdInterface();
        var result = this.complaintReplyRepository.save(request.toEntity(userInterface.getId()));
        return result;
    }

    // 대댓글에 대한 신고글 조회
    public ComplaintReply getComplaintReply(int id) {
        var result = this.complaintReplyRepository.findAllById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 대댓글 신고글 ID입니다. ->" + id));
        return result;
    }

    // 대댓글에 대한 신고글 목록 조회 (등록날짜를 기준으로 내림차순 조회)
    public Page<ComplaintReply> getComplaintReplies(Pageable pageable) {
        var result = this.complaintReplyRepository.findAllByOrderByInsertDateDesc(pageable);
        return result;
    }

    // 대댓글에 대한 신고글 삭제
    public String deleteComplaintReply(ForDeleteComplaintReply request) {
        var complaintPostForAdmin = this.complaintReplyRepository.findAllById(request.getId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 대댓글 신고글 ID입니다. -> " + request.getId()));
        this.complaintReplyRepository.delete(complaintPostForAdmin);

        return "Delete Complete!";
    }


    // 대댓글 삭제
    public String deleteReply(ForDeleteReply request) {
        var role = getRole();
        switch(role) {
            case ROLE_USER :
                var replyForUser = this.replyRepository.findAllByIdAndUserId(request.getId(), getIdInterface().getId())
                        .orElseThrow(() -> new RuntimeException("현재 대댓글 정보가 존재하지 않거나 로그인한 계정에서 접근할 수 있는 권한이 없습니다."));

                this.replyRepository.delete(replyForUser);
                break;
            case ROLE_ADMIN:
                var replyForAdmin = this.replyRepository.findAllById(request.getId())
                        .orElseThrow(() -> new RuntimeException("존재하지 않는 대댓글 ID입니다. -> " + request.getId()));
                this.replyRepository.delete(replyForAdmin);
                break;
        }
        return "Delete Complete!";
    }

}
