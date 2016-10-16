package ru.mail.park.dao;

import ru.mail.park.response.Reply;

/**
 * Created by zac on 13.10.16.
 */

public interface UserDAO extends BaseDAO {
    Reply create(String jsonString);

    Reply details(String email);

    Reply follow(String jsonString);

    Reply unfollow(String jsonString);

    Reply updateProfile(String jsonString);
}
