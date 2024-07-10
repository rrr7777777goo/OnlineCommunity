package com.onlinecommunity.domain.topic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ForRegisterTopic {
    @NotBlank
    @Size(min=2, max=20)
    private String name;

    public Topic toEntity(){
        return Topic.builder().name(this.name)
                .insertDate(LocalDateTime.now())
                .build();
    }
}
