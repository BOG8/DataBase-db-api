package ru.mail.park.dao;

/**
 * Created by zac on 11.10.16.
 */

public interface BaseDAO {
    void truncateTable();

    long getAmount();
}
