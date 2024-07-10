package com.onlinecommunity.repository;

import com.onlinecommunity.domain.post.complaint.ComplaintPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ComplaintPostRepository extends JpaRepository<ComplaintPost, Integer> {
    Optional<ComplaintPost> findAllById(int id);

    Page<ComplaintPost> findAllByOrderByInsertDateDesc(Pageable pageable);
}
