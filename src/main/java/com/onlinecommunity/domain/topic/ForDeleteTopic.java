package com.onlinecommunity.domain.topic;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ForDeleteTopic {
    @PositiveOrZero
    private int id;
}
