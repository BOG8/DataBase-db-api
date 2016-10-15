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
}
