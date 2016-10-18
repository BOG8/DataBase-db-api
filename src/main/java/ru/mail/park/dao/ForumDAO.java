package ru.mail.park.dao;

import ru.mail.park.response.Reply;

/**
 * Created by zac on 11.10.16.
 */

public interface ForumDAO extends BaseDAO {
    Reply create(String jsonString);

    Reply details(String shortName, String[] related);

    Reply listPosts(String forum, String since, Long limit, String order, String[] related);

    Reply listThreads(String forum, String since, Long limit, String order, String[] related);

    Reply listUsers(String forum, Long sinceId, Long limit, String order);
}
