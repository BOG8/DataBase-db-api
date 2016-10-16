package ru.mail.park.dao.impl;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import ru.mail.park.dao.ThreadDAO;
import ru.mail.park.model.Thread;
import ru.mail.park.response.Reply;
import ru.mail.park.response.Status;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Arrays;

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
                ps.setString(2, thread.getForum().toString());
                ps.setBoolean(3, thread.getIsClosed());
                ps.setBoolean(4, thread.getIsDeleted());
                ps.setString(5, thread.getMessage());
                ps.setString(6, thread.getSlug());
                ps.setString(7, thread.getTitle());
                ps.setString(8, thread.getUser().toString());
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

    @Override
    public Reply details(long threadId, String[] related) {
        Thread thread;
        try (Connection connection = dataSource.getConnection()) {
            String query = new StringBuffer("SELECT * FROM ")
                    .append(tableName)
                    .append(" WHERE id = ?").toString();
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setLong(1, threadId);
                try (ResultSet resultSet = ps.executeQuery()) {
                    resultSet.next();
                    thread = new Thread(resultSet);
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
            if (related != null) {
                if (Arrays.asList(related).contains("forum")) {
                    thread.setForum(new ForumDAOImpl(dataSource).details(thread.getForum().toString(), null).getObject());
                }
                if (Arrays.asList(related).contains("user")) {
                    thread.setUser(new UserDAOImpl(dataSource).details(thread.getUser().toString()).getObject());
                }
            }
        } catch (Exception e) {
            return new Reply(Status.INVALID_REQUEST);
        }

        return new Reply(Status.OK, thread);
    }

    @Override
    public Reply close(String jsonString) {
        try (Connection connection = dataSource.getConnection())  {
            Integer thread = new JsonParser().parse(jsonString).getAsJsonObject().get("thread").getAsInt();
            try {
                String query = new StringBuilder("UPDATE ")
                .append(tableName)
                .append(" SET isClosed = 1 WHERE id = ?").toString();
                try (PreparedStatement ps = connection.prepareStatement(query)) {
                    ps.setInt(1, thread);
                    ps.execute();
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (Exception e) {
            return new Reply(Status.INVALID_REQUEST);
        }

        return new Reply(Status.OK, new Gson().fromJson(jsonString, Object.class));
    }

    @Override
    public Reply open(String data) {
        try (Connection connection = dataSource.getConnection())  {
            Integer thread = new JsonParser().parse(data).getAsJsonObject().get("thread").getAsInt();
            try {
                String query = new StringBuilder("UPDATE ")
                        .append(tableName)
                        .append("  SET isClosed = 0 WHERE id = ?").toString();
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setInt(1, thread);
                    stmt.execute();
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (Exception e) {
            return new Reply(Status.INVALID_REQUEST);
        }
        return new Reply(Status.OK, new Gson().fromJson(data, Object.class));
    }
}
