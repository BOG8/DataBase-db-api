package ru.mail.park.dao.impl;

import ru.mail.park.dao.BaseDAO;
import ru.mail.park.response.Reply;
import ru.mail.park.response.Status;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Created by zac on 11.10.16.
 */

public class BaseDAOImpl implements BaseDAO {
    public static final int ALREADY_EXIST = 1062;
    protected String tableName = "";
    protected DataSource dataSource;

    protected Reply handeSQLException(SQLException e) {
        if (e.getErrorCode() == ALREADY_EXIST) {
            return new Reply(Status.ALREADY_EXIST);
        } else {
            return new Reply(Status.UNKNOWN_ERROR);
        }
    }
}
