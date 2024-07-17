package com.onlinecommunity.repository;

import com.onlinecommunity.domain.comment.complaint.ComplaintComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ComplaintCommentRepository extends JpaRepository<ComplaintComment, Integer> {
    Optional<ComplaintComment> findAllById(int id);

    Page<ComplaintComment> findAllByOrderByInsertDateDesc(Pageable pageable);
}
