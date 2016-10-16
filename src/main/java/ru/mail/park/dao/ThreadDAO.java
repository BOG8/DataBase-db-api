package ru.mail.park.dao;

import ru.mail.park.response.Reply;

/**
 * Created by zac on 15.10.16.
 */

public interface ThreadDAO extends BaseDAO {
    Reply create(String jsonString);

    Reply details(long threadId, String[] related);
}
