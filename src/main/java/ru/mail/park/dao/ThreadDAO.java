package ru.mail.park.dao;

import ru.mail.park.response.Reply;

/**
 * Created by zac on 15.10.16.
 */

public interface ThreadDAO extends BaseDAO {
    Reply create(String jsonString);

    Reply details(long threadId, String[] related);

    Reply close(String jsonString);

    Reply open(String jsonString);

    Reply remove(String jsonString);

    Reply restore(String jsonString);

    Reply update(String jsonString);

    Reply vote(String jsonString);

    Reply subscribe(String jsonString);

    Reply unsubscribe(String jsonString);

    Reply listForum(String forum, String since, Long limit, String order);

    Reply listUser(String user, String since, Long limit, String order);

    Reply listPosts(Long threadId, String since, Long limit, String sort, String order);
}
