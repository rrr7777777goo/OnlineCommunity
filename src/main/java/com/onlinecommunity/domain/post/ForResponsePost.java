package com.onlinecommunity.domain.post;

import com.onlinecommunity.domain.member.MemberRole;

import java.time.LocalDateTime;

public interface ForResponsePost {
    Integer getId();

    Integer getTopicId();

    String getTopicName();

    Integer getuserId();

    String getNickname();

    MemberRole getRole();

    String getTitle();

    String getContext();

    Integer getViewCount();

    LocalDateTime getInsertDate();

    LocalDateTime getUpdateDate();

    Integer getLikeScore();

    Integer getDislikeScore();
}
