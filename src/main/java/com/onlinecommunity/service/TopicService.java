package com.onlinecommunity.service;

import com.onlinecommunity.domain.topic.ForDeleteTopic;
import com.onlinecommunity.domain.topic.ForRegisterTopic;
import com.onlinecommunity.domain.topic.ForUpdateTopic;
import com.onlinecommunity.domain.topic.Topic;
import com.onlinecommunity.repository.TopicRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;

    // 이름을 검색하지 않았을 때 오류가 발생하지 않도록 null을 ""로 바꿔주는 함수
    private String getKeyword(String keyword) {
        return keyword == null ? "" : keyword;
    }

    public Topic registerTopic(ForRegisterTopic request) {
        boolean exists = this.topicRepository.existsByName(request.getName());
        if (exists) {
            throw new RuntimeException("이미 존재하는 이름입니다. -> " + request.getName());
        }

        var result =topicRepository.save(request.toEntity());
        return result;
    }

    public List<Topic> getTopics(Pageable pageable, String keyword) {
        var result = topicRepository.findAllByNameStartingWithOrderByNameAsc(pageable, getKeyword(keyword));
        return result;
    }

    @Transactional
    public Topic updateTopic(ForUpdateTopic request) {
        Topic topic = this.topicRepository.findAllById(request.getId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 주제 ID 입니다. -> " + request.getId()));

        boolean exists = this.topicRepository.existsByName(request.getName());
        if (exists) {
            throw new RuntimeException("이미 존재하는 이름입니다. -> " + request.getName());
        }

        topic.changeName(request.getName());
        return topic;
    }

    public String deleteTopic(ForDeleteTopic request) {
        Topic topic = this.topicRepository.findAllById(request.getId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 주제 ID 입니다. -> " + request.getId()));

        this.topicRepository.delete(topic);
        return "Delete Complete!";
    }
}
