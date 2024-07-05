package com.onlinecommunity.domain.topic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ForUpdateTopic {
    @PositiveOrZero
    private int id;

    @NotBlank
    @Size(min=2, max=20)
    private String name;
}
