package ru.mail.park.dao;

import ru.mail.park.response.Reply;

/**
 * Created by zac on 13.10.16.
 */

public interface UserDAO extends BaseDAO {
    Reply create(String jsonString);
}
