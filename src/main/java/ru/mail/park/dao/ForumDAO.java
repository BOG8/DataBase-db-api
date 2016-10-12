package ru.mail.park.dao;

import ru.mail.park.response.Reply;

/**
 * Created by zac on 11.10.16.
 */

public interface ForumDAO extends BaseDAO {
    Reply create(String jsonString);
}
