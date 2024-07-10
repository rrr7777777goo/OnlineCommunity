package com.onlinecommunity.domain.post.complaint;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ForRegisterComplaintPost {
    @PositiveOrZero
    private int postId; // 게시글 아이디
    @Size(min=10, max=1000)
    private String context; // 내용

    public ComplaintPost toEntity(int userId) {
        return ComplaintPost.builder()
                .postId(this.postId)
                .userId(userId)
                .context(this.context)
                .insertDate(LocalDateTime.now())
                .build();
    }
}
