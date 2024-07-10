package com.onlinecommunity.service;

import com.onlinecommunity.domain.member.Auth;
import com.onlinecommunity.domain.member.MemberRole;
import com.onlinecommunity.domain.post.*;
import com.onlinecommunity.domain.post.complaint.ComplaintPost;
import com.onlinecommunity.domain.post.complaint.ForDeleteComplaintPost;
import com.onlinecommunity.domain.post.complaint.ForRegisterComplaintPost;
import com.onlinecommunity.domain.post.score.ScorePost;
import com.onlinecommunity.domain.post.score.ScorePostStatus;
import com.onlinecommunity.repository.ComplaintPostRepository;
import com.onlinecommunity.repository.PostRepository;
import com.onlinecommunity.repository.ScorePostRepository;
import com.onlinecommunity.repository.TopicRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class PostService {

    private final TopicRepository topicRepository;

    private final PostRepository postRepository;
    private final ScorePostRepository scorePostRepository;
    private final ComplaintPostRepository complaintPostRepository;

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

    // 이름을 검색하지 않았을 때 오류가 발생하지 않도록 null을 ""로 바꿔주는 함수
    private String getKeyword(String keyword) {
        return keyword == null ? "" : keyword;
    }

    // 게시글 저장
    public Post registerPost(ForRegisterPost request) {

        boolean exists = topicRepository.existsById(request.getTopicId());
        if(!exists) {
            throw new RuntimeException("존재하지 않는 주제 ID입니다. -> " + request.getTopicId());
        }

        var result = this.postRepository.save(request.toEntity(getIdInterface().getId()));
        return result;
    }

    // 고유번호를 기반으로 게시글 조회
    @Transactional
    public ForResponsePost getPost(int id) {
        var post = this.postRepository.findAllById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시글 ID입니다. -> " + id));
        post.plusViewCount();

        var result = this.postRepository.findAllByIdAndReturnForResponsePost(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시글 ID입니다. -> " + id));
        return result;
    }

    // 게시글 목록 조회 (로그인 없이도 가능) , 작성시간을 기준으로 내림차순 출력, keyword로 검색시 keyword로 시작하는 제목들만 조회 가능
    public Page<ForResponsePost> getPosts(Pageable pageable, int topicid, String title, String context) {
        boolean exists = topicRepository.existsById(topicid);
        if(!exists) {
            throw new RuntimeException("존재하지 않는 주제 ID입니다. -> " + topicid);
        }

        var keywordTitle = getKeyword(title);
        var keywordContext = getKeyword(context);

        if(keywordTitle.compareTo("") != 0 && keywordContext.compareTo("") != 0) {
            var resultForTitleAndContext = this.postRepository.findAllByTitleContainingOrContextContainingOrderByInsertdateDescAndReturnForResponsePost(pageable, topicid, keywordTitle, keywordContext);
            return resultForTitleAndContext;
        } else if (keywordContext.compareTo("") != 0) {
            var resultForContext = this.postRepository.findAllByContextContainingOrderByInsertdateDescAndReturnForResponsePost(pageable, topicid, keywordContext);
            return resultForContext;
        } else {
            var resultForTitle = this.postRepository.findAllByTitleContainingOrderByInsertdateDescAndReturnForResponsePost(pageable, topicid, keywordTitle);
            return resultForTitle;
        }
    }

    public Page<ForResponsePost> getMyPosts(Pageable pageable) {
        var result = this.postRepository.findAllByUseridAndReturnForResponsePost(pageable, getIdInterface().getId());
        return result;
    }

    // 게시글 수정
    @Transactional
    public ForResponsePost updatePost(ForUpdatePost request) {
        var post = this.postRepository.findAllByIdAndUserId(request.getId(), getIdInterface().getId())
                .orElseThrow(() -> new RuntimeException("현재 게시글 정보가 존재하지 않거나 로그인한 계정에서 접근할 수 있는 권한이 없습니다."));
        post.updatePost(request.getTitle(), request.getContext());

        var result = this.postRepository.findAllByIdAndReturnForResponsePost(request.getId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시글 ID입니다. -> " + request.getId()));
        return result;
    }


    // 게시글에 좋아요 또는 싫어요 (한 계정당 1번만 가능, 좋아요와 싫어요 둘 중 하나만 선택 가능하며 추후에 변경 가능)
    @Transactional
    public ForResponsePost likePost(int id, boolean isLike) {
        var userid = getIdInterface().getId();
        var scorepost = this.scorePostRepository.findAllByPostIdAndUserId(id, userid);

        if(scorepost == null) {
            var post = this.postRepository.findAllById(id)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 게시글 ID입니다. -> " + id));

            if(isLike) {
                post.plusLikeScore();
            } else {
                post.plusDislikeScore();
            }
            this.scorePostRepository.save(ScorePost.builder()
                    .postId(id)
                    .userId(userid)
                    .status(isLike ? ScorePostStatus.SCORE_LIKE : ScorePostStatus.SCORE_DISLIKE)
                    .build());
        } else {
            scorepost.changeScore(isLike);
            var post = this.postRepository.findAllById(id)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 게시글 ID입니다. -> " + id));
            post.changeLikeScore(isLike);
        }

        var result = this.postRepository.findAllByIdAndReturnForResponsePost(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시글 ID입니다. -> " + id));
        return result;
    }

    // 게시글에 사용자가 좋아요 또는 싫어요를 눌렀는지 조회
    public Object getScorePost(int id) {
        var userInterface = getIdInterface();
        var result = this.scorePostRepository.findAllByPostIdAndUserId(id, userInterface.getId());

        if(result == null) {
            var exists = this.postRepository.existsAllById(id);
            if(!exists) {
                throw new RuntimeException("존재하지 않는 게시글 ID입니다. -> " + id);
            } else {
                throw new RuntimeException("현재 사용자는 해당 글에 좋아요, 싫어요 둘 중 어느것도 선택하지 않은 상태입니다. -> " + userInterface.getSignupId());
            }
        }

        return result;
    }

    // 게시글에 눌렀던 좋아요 or 싫어요 취소
    @Transactional
    public ForResponsePost cancellikePost(int id) {
        var userInterface = getIdInterface();
        var scorepost = this.scorePostRepository.findAllByPostIdAndUserId(id, userInterface.getId());

        if(scorepost == null) {
            var exists = this.postRepository.existsAllById(id);
            if(!exists) {
                throw new RuntimeException("존재하지 않는 게시글 ID입니다. -> " + id);
            } else {
                throw new RuntimeException("현재 사용자는 해당 글에 좋아요, 싫어요 둘 중 어느것도 선택하지 않은 상태입니다. -> " + userInterface.getSignupId());
            }
        } else {
            var post = this.postRepository.findAllById(id)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 게시글 ID입니다. -> " + id));
            if(scorepost.getStatus() == ScorePostStatus.SCORE_LIKE) {
                post.minusLikeScore();
            } else {
                post.minusDislikeScore();
            }
            this.scorePostRepository.delete(scorepost);
        }

        var result = this.postRepository.findAllByIdAndReturnForResponsePost(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시글 ID입니다. -> " + id));
        return result;
    }

    // 게시글에 대한 신고글 작성
    public ComplaintPost complaintPost(ForRegisterComplaintPost request) {
        var exists = this.postRepository.existsAllById(request.getPostId());
        if(!exists) {
            throw new RuntimeException("존재하지 않는 게시글 ID입니다. -> " + request.getPostId());
        }

        var userInterface = getIdInterface();
        var result = this.complaintPostRepository.save(request.toEntity(userInterface.getId()));
        return result;
    }

    // 게시글에 대한 신고글 조회
    public ComplaintPost getComplaintPost(int id) {
        var result = this.complaintPostRepository.findAllById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시글 신고글 ID입니다. ->" + id));
        return result;
    }

    // 게시글에 대한 신고글 목록 조회 (등록날짜를 기준으로 내림차순 조회)
    public Page<ComplaintPost> getComplaintPosts(Pageable pageable) {
        var result = this.complaintPostRepository.findAllByOrderByInsertDateDesc(pageable);
        return result;
    }

    // 게시글에 대한 신고글 삭제
    public String deleteComplaintPost(ForDeleteComplaintPost request) {
        var complaintPostForAdmin = this.complaintPostRepository.findAllById(request.getId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시글 신고글 ID입니다. -> " + request.getId()));
        this.complaintPostRepository.delete(complaintPostForAdmin);

        return "Delete Complete!";
    }

    // 게시글 삭제
    public String deletePost(ForDeletePost request) {
        var role = getRole();
        switch(role) {
            case ROLE_USER :
                var postForUser = this.postRepository.findAllByIdAndUserId(request.getId(), getIdInterface().getId())
                        .orElseThrow(() -> new RuntimeException("현재 게시글 정보가 존재하지 않거나 로그인한 계정에서 접근할 수 있는 권한이 없습니다."));

                this.postRepository.delete(postForUser);
            break;
            case ROLE_ADMIN:
                var postForAdmin = this.postRepository.findAllById(request.getId())
                        .orElseThrow(() -> new RuntimeException("존재하지 않는 게시글 ID입니다. -> " + request.getId()));
                this.postRepository.delete(postForAdmin);
            break;
        }
        return "Delete Complete!";
    }

}
