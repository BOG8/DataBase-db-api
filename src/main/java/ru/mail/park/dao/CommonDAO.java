package ru.mail.park.dao;

import java.util.Map;

/**
 * Created by zac on 16.10.16.
 */

public interface CommonDAO {
    void truncateAllTables();

    Map<String, Long> getAmounts();
}
