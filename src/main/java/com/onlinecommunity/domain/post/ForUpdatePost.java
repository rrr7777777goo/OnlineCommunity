package com.onlinecommunity.domain.post;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ForUpdatePost {
    @PositiveOrZero
    private int id; // 게시글 아이디
    @Size(min=5, max=90)
    private String title; // 제목
    @Size(min=1, max=1000)
    private String context; // 내용
}
