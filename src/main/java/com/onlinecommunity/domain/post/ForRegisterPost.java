package com.onlinecommunity.domain.post;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ForRegisterPost {
    @PositiveOrZero
    private int topicId; // 주제
    @Size(min=5, max=90)
    private String title; // 제목
    @Size(min=1, max=1000)
    private String context; // 내용

    public Post toEntity(int userid) {
        LocalDateTime nowTime = LocalDateTime.now();

        return Post.builder()
                .topicId(this.topicId)
                .userId(userid)
                .title(this.title)
                .context(this.context)
                .viewCount(0)
                .insertDate(nowTime)
                .updateDate(nowTime)
                .build();
    }
}
