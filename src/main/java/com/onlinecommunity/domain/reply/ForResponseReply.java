package com.onlinecommunity.domain.reply;

import com.onlinecommunity.domain.member.MemberRole;

import java.time.LocalDateTime;

public interface ForResponseReply {
    Integer getId();

    Integer getCommentId();

    Integer getUserId();

    String getNickname();

    MemberRole getRole();


    String getContext();

    LocalDateTime getInsertDate();

    LocalDateTime getUpdateDate();

    Integer getLikeScore();

    Integer getDislikeScore();
}
