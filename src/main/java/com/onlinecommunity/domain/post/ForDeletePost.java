package com.onlinecommunity.domain.post;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ForDeletePost {
    @PositiveOrZero
    private int id; // 게시글 아이디
}
