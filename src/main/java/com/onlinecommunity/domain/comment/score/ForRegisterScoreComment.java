package com.onlinecommunity.domain.comment.score;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ForRegisterScoreComment {
    @PositiveOrZero
    private int id;
}
