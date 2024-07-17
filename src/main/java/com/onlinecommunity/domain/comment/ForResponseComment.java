package com.onlinecommunity.domain.comment;

import com.onlinecommunity.domain.member.MemberRole;

import java.time.LocalDateTime;

public interface ForResponseComment {
    Integer getId();

    Integer getPostId();

    Integer getUserId();

    String getNickname();

    MemberRole getRole();


    String getContext();

    LocalDateTime getInsertDate();

    LocalDateTime getUpdateDate();

    Integer getLikeScore();

    Integer getDislikeScore();
}
