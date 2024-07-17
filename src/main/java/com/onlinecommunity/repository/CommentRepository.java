package com.onlinecommunity.repository;

import com.onlinecommunity.domain.comment.Comment;
import com.onlinecommunity.domain.comment.ForResponseComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    boolean existsAllById(int id);

    Optional<Comment> findAllById(int id);

    Optional<Comment> findAllByIdAndUserId(int id, int userId);

    @Query(value=
            "select a.id as id, a.postId as postId, a.userId as userId, d.nickname as nickname, d.role as role, a.context as context, a.likeScore as likeScore, a.dislikeScore as dislikeScore, a.insertDate as insertDate, a.updateDate as updateDate \n" +
                    "from com.onlinecommunity.domain.comment.Comment as a, " +
                    "com.onlinecommunity.domain.post.Post as c," +
                    "com.onlinecommunity.domain.member.Member as d\n" +
                    "where a.id = ?1 and c.id = a.postId and a.userId = d.id\n" +
                    "group by a.id")
    Optional<ForResponseComment> findAllByIdAndReturnForResponseComment(int id);

    @Query(value=
            "select a.id as id, a.postId as postId, a.userId as userId, d.nickname as nickname, d.role as role, a.context as context, a.likeScore as likeScore, a.dislikeScore as dislikeScore, a.insertDate as insertDate, a.updateDate as updateDate \n" +
                    "from com.onlinecommunity.domain.comment.Comment as a, " +
                    "com.onlinecommunity.domain.post.Post as c," +
                    "com.onlinecommunity.domain.member.Member as d\n" +
                    "where c.id = a.postId and a.userId = ?1\n" +
                    "group by a.id\n" +
                    "order by a.insertDate desc")
    Page<ForResponseComment> findAllByUserIdAndReturnForResponsePost(Pageable pageable, int userId);

    @Query(value=
            "select a.id as id, a.postId as postId, a.userId as userId, d.nickname as nickname, d.role as role, a.context as context, a.likeScore as likeScore, a.dislikeScore as dislikeScore, a.insertDate as insertDate, a.updateDate as updateDate \n" +
                    "from com.onlinecommunity.domain.comment.Comment as a, " +
                    "com.onlinecommunity.domain.post.Post as c," +
                    "com.onlinecommunity.domain.member.Member as d\n" +
                    "where a.postId = ?1 and c.id = a.postId and a.userId = d.id\n" +
                    "group by a.id\n" +
                    "order by a.insertDate desc")
    Page<ForResponseComment> findAllByOrderByInsertDateDescAndReturnForResponseComment(Pageable pageable, int postId);
}
