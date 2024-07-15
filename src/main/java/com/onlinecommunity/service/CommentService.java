package com.onlinecommunity.service;

import com.onlinecommunity.domain.ScoreStatus;
import com.onlinecommunity.domain.comment.*;
import com.onlinecommunity.domain.comment.complaint.ComplaintComment;
import com.onlinecommunity.domain.comment.complaint.ForDeleteComplaintComment;
import com.onlinecommunity.domain.comment.complaint.ForRegisterComplaintComment;
import com.onlinecommunity.domain.comment.score.ScoreComment;
import com.onlinecommunity.domain.member.Auth;
import com.onlinecommunity.domain.member.MemberRole;
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
public class CommentService {
    private final PostRepository postRepository;

    private final CommentRepository commentRepository;
    private final ScoreCommentRepository scoreCommentRepository;
    private final ComplaintCommentRepository complaintCommentRepository;

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

    // 댓글 저장
    public Comment registerComment(ForRegisterComment request) {

        boolean exists = this.postRepository.existsById(request.getPostId());
        if (!exists) {
            throw new RuntimeException("존재하지 않는 게시글 ID입니다. -> " + request.getPostId());
        }

        var result = this.commentRepository.save(request.toEntity(getIdInterface().getId()));
        return result;
    }

    // 고유번호를 기반으로 한 댓글 조회
    public ForResponseComment getComment(int id) {
        var result = this.commentRepository.findAllByIdAndReturnForResponseComment(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 댓글 ID입니다. -> " + id));
        return result;
    }

    // 댓글 목록 조회 (로그인 없이도 가능) , 작성시간을 기준으로 내림차순 출력
    public Page<ForResponseComment> getComments(Pageable pageable, int postId) {
        boolean exists = this.postRepository.existsById(postId);
        if (!exists) {
            throw new RuntimeException("존재하지 않는 게시글 ID입니다. -> " + postId);
        }

        var result = this.commentRepository.findAllByOrderByInsertDateDescAndReturnForResponseComment(pageable, postId);
        return result;
    }

    public Page<ForResponseComment> getMyComments(Pageable pageable) {
        var result = this.commentRepository.findAllByUserIdAndReturnForResponsePost(pageable, getIdInterface().getId());
        return result;
    }

    // 댓글 수정
    @Transactional
    public ForResponseComment updateComment(ForUpdateComment request) {
        var post = this.commentRepository.findAllByIdAndUserId(request.getId(), getIdInterface().getId())
                .orElseThrow(() -> new RuntimeException("현재 댓글 정보가 존재하지 않거나 로그인한 계정에서 접근할 수 있는 권한이 없습니다."));
        post.updateComment(request.getContext());

        var result = this.commentRepository.findAllByIdAndReturnForResponseComment(request.getId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 댓글 ID입니다. -> " + request.getId()));
        return result;
    }

    // 댓글에 좋아요 또는 싫어요 (한 계정당 1번만 가능, 좋아요와 싫어요 둘 중 하나만 선택 가능하며 추후에 변경 가능)
    @Transactional
    public ForResponseComment likeComment(int id, boolean isLike) {
        var userId = getIdInterface().getId();
        var scoreComment = this.scoreCommentRepository.findAllByCommentIdAndUserId(id, userId);

        if (scoreComment == null) {
            var comment = this.commentRepository.findAllById(id)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 댓글 ID입니다. -> " + id));

            if (isLike) {
                comment.plusLikeScore();
            } else {
                comment.plusDislikeScore();
            }
            this.scoreCommentRepository.save(ScoreComment.builder()
                    .commentId(id)
                    .userId(userId)
                    .status(isLike ? ScoreStatus.SCORE_LIKE : ScoreStatus.SCORE_DISLIKE)
                    .build());
        } else {
            scoreComment.changeScore(isLike);
            var comment = this.commentRepository.findAllById(id)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 댓글 ID입니다. -> " + id));
            comment.changeLikeScore(isLike);
        }

        var result = this.commentRepository.findAllByIdAndReturnForResponseComment(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 댓글 ID입니다. -> " + id));
        return result;
    }

    // 댓글에 사용자가 좋아요 또는 싫어요를 눌렀는지 조회
    public ScoreComment getScoreComment(int id) {
        var userInterface = getIdInterface();
        var result = this.scoreCommentRepository.findAllByCommentIdAndUserId(id, userInterface.getId());

        if (result == null) {
            var exists = this.commentRepository.existsAllById(id);
            if (!exists) {
                throw new RuntimeException("존재하지 않는 댓글 ID입니다. -> " + id);
            } else {
                throw new RuntimeException("현재 사용자는 해당 글에 좋아요, 싫어요 둘 중 어느것도 선택하지 않은 상태입니다. -> " + userInterface.getSignupId());
            }
        }

        return result;
    }

    // 댓글에 눌렀던 좋아요 or 싫어요 취소
    @Transactional
    public ForResponseComment cancelLikeComment(int id) {
        var userInterface = getIdInterface();
        var scoreComment = this.scoreCommentRepository.findAllByCommentIdAndUserId(id, userInterface.getId());

        if (scoreComment == null) {
            var exists = this.commentRepository.existsAllById(id);
            if (!exists) {
                throw new RuntimeException("존재하지 않는 댓글 ID입니다. -> " + id);
            } else {
                throw new RuntimeException("현재 사용자는 해당 글에 좋아요, 싫어요 둘 중 어느것도 선택하지 않은 상태입니다. -> " + userInterface.getSignupId());
            }
        } else {
            var comment = this.commentRepository.findAllById(id)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 댓글 ID입니다. -> " + id));
            if (scoreComment.getStatus() == ScoreStatus.SCORE_LIKE) {
                comment.minusLikeScore();
            } else {
                comment.minusDislikeScore();
            }
            this.scoreCommentRepository.delete(scoreComment);
        }

        var result = this.commentRepository.findAllByIdAndReturnForResponseComment(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 댓글 ID입니다. -> " + id));
        return result;
    }

    // 댓글에 대한 신고글 작성
    public ComplaintComment complaintComment(ForRegisterComplaintComment request) {
        var exists = this.commentRepository.existsAllById(request.getCommentId());
        if (!exists) {
            throw new RuntimeException("존재하지 않는 댓글 ID입니다. -> " + request.getCommentId());
        }

        var userInterface = getIdInterface();
        var result = this.complaintCommentRepository.save(request.toEntity(userInterface.getId()));
        return result;
    }

    // 댓글에 대한 신고글 조회
    public ComplaintComment getComplaintComment(int id) {
        var result = this.complaintCommentRepository.findAllById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 댓글 신고글 ID입니다. ->" + id));
        return result;
    }

    // 댓글에 대한 신고글 목록 조회 (등록날짜를 기준으로 내림차순 조회)
    public Page<ComplaintComment> getComplaintComments(Pageable pageable) {
        var result = this.complaintCommentRepository.findAllByOrderByInsertDateDesc(pageable);
        return result;
    }

    // 댓글에 대한 신고글 삭제
    public String deleteComplaintComment(ForDeleteComplaintComment request) {
        var complaintPostForAdmin = this.complaintCommentRepository.findAllById(request.getId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 댓글 신고글 ID입니다. -> " + request.getId()));
        this.complaintCommentRepository.delete(complaintPostForAdmin);

        return "Delete Complete!";
    }


    // 댓글 삭제
    public String deleteComment(ForDeleteComment request) {
        var role = getRole();
        switch(role) {
            case ROLE_USER :
                var commentForUser = this.commentRepository.findAllByIdAndUserId(request.getId(), getIdInterface().getId())
                        .orElseThrow(() -> new RuntimeException("현재 댓글 정보가 존재하지 않거나 로그인한 계정에서 접근할 수 있는 권한이 없습니다."));

                this.commentRepository.delete(commentForUser);
            break;
            case ROLE_ADMIN:
                var commentForAdmin = this.commentRepository.findAllById(request.getId())
                        .orElseThrow(() -> new RuntimeException("존재하지 않는 댓글 ID입니다. -> " + request.getId()));
                this.commentRepository.delete(commentForAdmin);
            break;
        }
        return "Delete Complete!";
    }

}
