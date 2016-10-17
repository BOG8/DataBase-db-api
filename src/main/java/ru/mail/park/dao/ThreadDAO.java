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
}
