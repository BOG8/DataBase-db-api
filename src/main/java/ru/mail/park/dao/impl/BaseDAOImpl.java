package ru.mail.park.dao.impl;

import ru.mail.park.Truncator;
import ru.mail.park.dao.BaseDAO;
import ru.mail.park.response.Reply;
import ru.mail.park.response.Status;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by zac on 11.10.16.
 */

public class BaseDAOImpl implements BaseDAO {
    public static final int ALREADY_EXIST = 1062;
    protected String tableName = "";
    protected DataSource dataSource;

    @Override
    public void truncateTable() {
        try (Connection connection = dataSource.getConnection()) {
            Truncator.truncByQuery(connection, "SET FOREIGN_KEY_CHECKS = 0;");
            Truncator.truncByQuery(connection, "TRUNCATE TABLE " + tableName);
            if (tableName == "User") {
                Truncator.truncByQuery(connection, "TRUNCATE TABLE Followers");
            }
            if (tableName == "Thread") {
                Truncator.truncByQuery(connection, "TRUNCATE TABLE Subscriptions");
            }
            Truncator.truncByQuery(connection, "SET FOREIGN_KEY_CHECKS = 1;");
        } catch (Exception e) {
            new Reply(Status.UNKNOWN_ERROR);
        }
    }

    protected Reply handeSQLException(SQLException e) {
        if (e.getErrorCode() == ALREADY_EXIST) {
            return new Reply(Status.ALREADY_EXIST);
        } else {
            return new Reply(Status.UNKNOWN_ERROR);
        }
    }
}
