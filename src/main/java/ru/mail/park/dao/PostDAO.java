package ru.mail.park.dao;

import ru.mail.park.response.Reply;

/**
 * Created by zac on 15.10.16.
 */

public interface PostDAO extends BaseDAO {
    Reply create(String jsonString);

    Reply details(long postId, String[] related);

    Reply remove(String jsonString);

    Reply restore(String jsonString);

    Reply update(String jsonString);

    Reply vote(String jsonString);

    Reply listForum(String forum, String since, Long limit, String order);

    Reply listThread(Long threadId, String since, Long limit, String order);
}
