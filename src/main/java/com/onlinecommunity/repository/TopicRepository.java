package com.onlinecommunity.repository;

import com.onlinecommunity.domain.topic.Topic;
import org.hibernate.query.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Integer> {

    boolean existsByName(String name);

    Optional<Topic> findAllById(int id);

    List<Topic> findAllByNameStartingWithOrderByNameAsc(Pageable pageable, String name);
}
