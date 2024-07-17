package com.onlinecommunity.domain.reply.score;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ForRegisterScoreReply {
    @PositiveOrZero
    private int id;
}
