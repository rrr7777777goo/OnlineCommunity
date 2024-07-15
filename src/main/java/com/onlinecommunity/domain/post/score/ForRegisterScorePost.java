package com.onlinecommunity.domain.post.score;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ForRegisterScorePost {
    @PositiveOrZero
    private int id;
}
