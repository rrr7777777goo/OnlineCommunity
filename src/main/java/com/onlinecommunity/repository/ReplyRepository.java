package com.onlinecommunity.repository;

import com.onlinecommunity.domain.reply.ForResponseReply;
import com.onlinecommunity.domain.reply.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Integer> {
    boolean existsAllById(int id);

    Optional<Reply> findAllById(int id);

    Optional<Reply> findAllByIdAndUserId(int id, int userId);

    @Query(value=
            "select a.id as id, a.commentId as commentId, a.userId as userId, d.nickname as nickname, d.role as role, a.context as context, a.likeScore as likeScore, a.dislikeScore as dislikeScore, a.insertDate as insertDate, a.updateDate as updateDate \n" +
                    "from com.onlinecommunity.domain.reply.Reply as a, " +
                    "com.onlinecommunity.domain.comment.Comment as c," +
                    "com.onlinecommunity.domain.member.Member as d\n" +
                    "where a.id = ?1 and c.id = a.commentId and a.userId = d.id\n" +
                    "group by a.id")
    Optional<ForResponseReply> findAllByIdAndReturnForResponseComment(int id);

    @Query(value=
            "select a.id as id, a.commentId as commentId, a.userId as userId, d.nickname as nickname, d.role as role, a.context as context, a.likeScore as likeScore, a.dislikeScore as dislikeScore, a.insertDate as insertDate, a.updateDate as updateDate \n" +
                    "from com.onlinecommunity.domain.reply.Reply as a, " +
                    "com.onlinecommunity.domain.comment.Comment as c," +
                    "com.onlinecommunity.domain.member.Member as d\n" +
                    "where c.id = a.commentId and a.userId = ?1\n" +
                    "group by a.id\n" +
                    "order by a.insertDate desc")
    Page<ForResponseReply> findAllByUserIdAndReturnForResponsePost(Pageable pageable, int userId);

    @Query(value=
            "select a.id as id, a.commentId as commentId, a.userId as userId, d.nickname as nickname, d.role as role, a.context as context, a.likeScore as likeScore, a.dislikeScore as dislikeScore, a.insertDate as insertDate, a.updateDate as updateDate \n" +
                    "from com.onlinecommunity.domain.reply.Reply as a, " +
                    "com.onlinecommunity.domain.comment.Comment as c," +
                    "com.onlinecommunity.domain.member.Member as d\n" +
                    "where a.commentId = ?1 and c.id = a.commentId and a.userId = d.id\n" +
                    "group by a.id\n" +
                    "order by a.insertDate desc")
    Page<ForResponseReply> findAllByOrderByInsertDateDescAndReturnForResponseComment(Pageable pageable, int commentId);
}
