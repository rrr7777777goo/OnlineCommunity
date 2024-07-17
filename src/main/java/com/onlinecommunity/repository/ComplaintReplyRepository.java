package com.onlinecommunity.repository;

import com.onlinecommunity.domain.reply.complaint.ComplaintReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ComplaintReplyRepository extends JpaRepository<ComplaintReply, Integer> {
    Optional<ComplaintReply> findAllById(int id);

    Page<ComplaintReply> findAllByOrderByInsertDateDesc(Pageable pageable);
}
