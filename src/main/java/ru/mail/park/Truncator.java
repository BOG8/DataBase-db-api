package ru.mail.park;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by zac on 16.10.16.
 */

public class Truncator {
    public static void truncByQuery(Connection connection, String query) throws SQLException {
        try (Statement ps = connection.createStatement()) {
            ps.execute(query);
        }
    }
}
