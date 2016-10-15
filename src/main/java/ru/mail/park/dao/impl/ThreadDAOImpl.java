package ru.mail.park.dao.impl;

import com.google.gson.JsonParser;
import ru.mail.park.dao.ThreadDAO;
import ru.mail.park.model.Thread;
import ru.mail.park.response.Reply;
import ru.mail.park.response.Status;

import javax.sql.DataSource;
import java.sql.*;

/**
 * Created by zac on 15.10.16.
 */

public class ThreadDAOImpl extends BaseDAOImpl implements ThreadDAO {

    public ThreadDAOImpl(DataSource dataSource) {
        this.tableName = Thread.TABLE_NAME;
        this.dataSource = dataSource;
    }

    @Override
    public Reply create(String jsonString) {
        final Thread thread;
        try (Connection connection = dataSource.getConnection()){
            thread = new Thread(new JsonParser().parse(jsonString).getAsJsonObject());
            String query = new StringBuilder("INSERT INTO ")
                    .append(tableName)
                    .append("(date, forum, isClosed, isDeleted, message, slug, title, user) ")
                    .append("VALUES (?, ?, ?, ?, ?, ?, ?, ?)").toString();
            try (PreparedStatement ps = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, thread.getDate());
                ps.setString(2, thread.getForum());
                ps.setBoolean(3, thread.getIsClosed());
                ps.setBoolean(4, thread.getIsDeleted());
                ps.setString(5, thread.getMessage());
                ps.setString(6, thread.getSlug());
                ps.setString(7, thread.getTitle());
                ps.setString(8, thread.getUser());
                ps.executeUpdate();
                try (ResultSet resultSet = ps.getGeneratedKeys()) {
                    resultSet.next();
                    thread.setId(resultSet.getLong(1));
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (Exception e) {
            return new Reply(Status.INVALID_REQUEST);
        }

        return new Reply(Status.OK, thread);
    }
}
