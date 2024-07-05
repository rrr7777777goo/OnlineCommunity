package com.onlinecommunity.controller;

import com.onlinecommunity.domain.topic.ForDeleteTopic;
import com.onlinecommunity.domain.topic.ForRegisterTopic;
import com.onlinecommunity.domain.topic.ForUpdateTopic;
import com.onlinecommunity.service.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/topic")
@RequiredArgsConstructor
public class TopicController {
    private final TopicService topicService;

    // 주제 추가 (관리자만 가능)
    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerTopic(@Validated @RequestBody ForRegisterTopic request) {
        var result = this.topicService.registerTopic(request);
        return ResponseEntity.ok(result);
    }

    // 주제 수정 (관리자만 가능)
    @PutMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateTopic(@Validated @RequestBody ForUpdateTopic request) {
        var result = this.topicService.updateTopic(request);
        return ResponseEntity.ok(result);
    }

    // 주제 삭제 (관리자만 가능)
    @DeleteMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteTopic(@Validated @RequestBody ForDeleteTopic request) {
        var result = this.topicService.deleteTopic(request);
        return ResponseEntity.ok(result);
    }


    // 주제 조회, 이름순으로 출력, keyword로 검색시 keyword로 시작하는 주제들만 조회 가능
    @GetMapping("/list")
    public ResponseEntity<?> getTopics(Pageable pageable, @RequestParam(required = false) String keyword) {
        var result = this.topicService.getTopics(pageable, keyword);
        return ResponseEntity.ok(result);
    }
}
