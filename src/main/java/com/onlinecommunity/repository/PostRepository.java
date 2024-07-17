package com.onlinecommunity.repository;

import com.onlinecommunity.domain.post.ForResponsePost;
import com.onlinecommunity.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> {
    boolean existsAllById(int id);

    Optional<Post> findAllById(int id);

    Optional<Post> findAllByIdAndUserId(int id, int userId);

    @Query(value=
            "select a.id as id, a.topicId as topicId, c.name as topicName, a.userId as userId, d.nickname as nickname, d.role as role, a.title as title, a.context as context, a.viewCount as viewCount, a.likeScore as likeScore, a.dislikeScore as dislikeScore, a.insertDate as insertDate, a.updateDate as updateDate \n" +
            "from com.onlinecommunity.domain.post.Post as a, " +
            "com.onlinecommunity.domain.topic.Topic as c," +
            "com.onlinecommunity.domain.member.Member as d\n" +
            "where a.id = ?1 and c.id = a.topicId and a.userId = d.id\n" +
            "group by a.id")
    Optional<ForResponsePost> findAllByIdAndReturnForResponsePost(int id);

    @Query(value=
            "select a.id as id, a.topicId as topicId, c.name as topicName, a.userId as userId, d.nickname as nickname, d.role as role, a.title as title, a.context as context, a.viewCount as viewCount, a.likeScore as likeScore, a.dislikeScore as dislikeScore, a.insertDate as insertDate, a.updateDate as updateDate \n" +
                    "from com.onlinecommunity.domain.post.Post as a , " +
                    "com.onlinecommunity.domain.topic.Topic as c," +
                    "com.onlinecommunity.domain.member.Member as d\n" +
                    "where c.id = a.topicId and a.userId = ?1\n" +
                    "group by a.id\n" +
                    "order by a.insertDate desc")
    Page<ForResponsePost> findAllByUserIdAndReturnForResponsePost(Pageable pageable, int userId);

    @Query(value=
            "select a.id as id, a.topicId as topicId, c.name as topicName, a.userId as userId, d.nickname as nickname, d.role as role, a.title as title, a.context as context, a.viewCount as viewCount, a.likeScore as likeScore, a.dislikeScore as dislikeScore, a.insertDate as insertDate, a.updateDate as updateDate \n" +
                    "from com.onlinecommunity.domain.post.Post as a, " +
                    "com.onlinecommunity.domain.topic.Topic as c," +
                    "com.onlinecommunity.domain.member.Member as d\n" +
                    "where a.topicId = ?1 and upper(a.title) like upper(concat('%', concat(?2, '%'))) and c.id = a.topicId and a.userId = d.id\n" +
                    "group by a.id\n" +
                    "order by a.insertDate desc")
    Page<ForResponsePost> findAllByTitleContainingOrderByInsertDateDescAndReturnForResponsePost(Pageable pageable, int topic, String title);

    @Query(value=
            "select a.id as id, a.topicId as topicId, c.name as topicName, a.userId as userId, d.nickname as nickname, d.role as role, a.title as title, a.context as context, a.viewCount as viewCount, a.likeScore as likeScore, a.dislikeScore as dislikeScore, a.insertDate as insertDate, a.updateDate as updateDate \n" +
                    "from com.onlinecommunity.domain.post.Post as a, " +
                    "com.onlinecommunity.domain.topic.Topic as c," +
                    "com.onlinecommunity.domain.member.Member as d\n" +
                    "where a.topicId = ?1 and upper(a.context) like upper(concat('%', concat(?2, '%'))) and c.id = a.topicId and a.userId = d.id\n" +
                    "group by a.id\n" +
                    "order by a.insertDate desc")
    Page<ForResponsePost> findAllByContextContainingOrderByInsertDateDescAndReturnForResponsePost(Pageable pageable, int topic, String context);

    @Query(value=
            "select a.id as id, a.topicId as topicId, c.name as topicName, a.userId as userId, d.nickname as nickname, d.role as role, a.title as title, a.context as context, a.viewCount as viewCount, a.likeScore as likeScore, a.dislikeScore as dislikeScore, a.insertDate as insertDate, a.updateDate as updateDate \n" +
                    "from com.onlinecommunity.domain.post.Post as a, " +
                    "com.onlinecommunity.domain.topic.Topic as c," +
                    "com.onlinecommunity.domain.member.Member as d\n" +
                    "where a.topicId = ?1 and (upper(a.title) like upper(concat('%', concat(?2, '%'))) or upper(a.context) like upper(concat('%', concat(?3, '%')))) and c.id = a.topicId and a.userId = d.id\n" +
                    "group by a.id\n" +
                    "order by a.insertDate desc")
    Page<ForResponsePost> findAllByTitleContainingOrContextContainingOrderByInsertDateDescAndReturnForResponsePost(Pageable pageable, int topic, String title, String context);
}
