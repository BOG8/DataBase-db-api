package ru.mail.park.dao.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ru.mail.park.dao.PostDAO;
import ru.mail.park.model.Post;
import ru.mail.park.response.Reply;
import ru.mail.park.response.Status;

import javax.sql.DataSource;
import java.sql.*;

/**
 * Created by zac on 15.10.16.
 */

public class PostDAOImpl extends BaseDAOImpl implements PostDAO {

    public PostDAOImpl(DataSource dataSource) {
        this.tableName = Post.TABLE_NAME;
        this.dataSource = dataSource;
    }

    @Override
    public Reply create(String jsonString) {
        final Post post;
        try (Connection connection = dataSource.getConnection()){
            post = new Post(new JsonParser().parse(jsonString).getAsJsonObject());
            String query = new StringBuilder("INSERT INTO ")
                    .append(tableName)
                    .append("(date, forum, isApproved, isDeleted, isEdited, isHighlighted, isSpam, message, parent, thread, user)")
                    .append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)").toString();
            try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, post.getDate());
                ps.setString(2, post.getForum());
                ps.setBoolean(3, post.getIsApproved());
                ps.setBoolean(4, post.getIsDeleted());
                ps.setBoolean(5, post.getIsEdited());
                ps.setBoolean(6, post.getIsHighlighted());
                ps.setBoolean(7, post.getIsSpam());
                ps.setString(8, post.getMessage());
                ps.setObject(9, post.getParent());
                ps.setLong(10, post.getThread());
                ps.setString(11, post.getUser());
                ps.executeUpdate();
                try (ResultSet resultSet = ps.getGeneratedKeys()) {
                    resultSet.next();
                    post.setId(resultSet.getLong(1));
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (Exception e) {
            return new Reply(Status.INVALID_REQUEST);
        }

        return new Reply(Status.OK, post);
    }

    @Override
    public Reply details(long postId, String[] related) {
        Post post;
        try (Connection connection = dataSource.getConnection()) {
            String query = new StringBuilder("SELECT * FROM ")
                    .append(tableName)
                    .append(" WHERE id = ?").toString();
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setLong(1, postId);
                try (ResultSet resultSet = ps.executeQuery()) {
                    resultSet.next();
                    post = new Post(resultSet);
                }
            } catch (SQLException e) {
                return new Reply(Status.NOT_FOUND);
            }
        } catch (Exception e) {
            return new Reply(Status.INVALID_REQUEST);
        }

        return new Reply(Status.OK, post);
    }

    @Override
    public Reply remove(String jsonString) {
        try (Connection connection = dataSource.getConnection()) {
            Long post = new JsonParser().parse(jsonString).getAsJsonObject().get("post").getAsLong();
            String query = new StringBuilder("UPDATE ")
                    .append(tableName)
                    .append(" SET isDeleted = 1 WHERE id = ?").toString();
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setLong(1, post);
                ps.executeUpdate();
            } catch (SQLException e) {
                new Reply(Status.NOT_FOUND);
            }
        } catch (Exception e) {
            return new Reply(Status.INVALID_REQUEST);
        }

        return new Reply(Status.OK, new Gson().fromJson(jsonString, Object.class));
    }

    @Override
    public Reply restore(String jsonString) {
        try (Connection connection = dataSource.getConnection()) {
            Long postId = new JsonParser().parse(jsonString).getAsJsonObject().get("post").getAsLong();
            String query = new StringBuilder("UPDATE ")
                    .append(tableName)
                    .append(" SET isDeleted = 0 WHERE id = ?").toString();
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setLong(1, postId);
                ps.executeUpdate();
            } catch (SQLException e) {
                new Reply(Status.NOT_FOUND);
            }
        } catch (Exception e) {
            return new Reply(Status.INVALID_REQUEST);
        }

        return new Reply(Status.OK, new Gson().fromJson(jsonString, Object.class));
    }

    @Override
    public Reply update(String jsonString) {
        long postId;
        try (Connection connection = dataSource.getConnection()) {
            JsonObject object = new JsonParser().parse(jsonString).getAsJsonObject();
            postId = object.get("post").getAsLong();
            String message = object.get("message").getAsString();
            String query = new StringBuilder("UPDATE ")
                    .append(tableName)
                    .append(" SET message = ? WHERE id = ?").toString();
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, message);
                ps.setLong(2, postId);
                ps.executeUpdate();
            } catch (SQLException e) {
                return new Reply(Status.NOT_FOUND);
            }
        } catch (Exception e) {
            return new Reply(Status.INVALID_REQUEST);
        }

        return new Reply(Status.OK, details(postId, null).getObject());
    }

    @Override
    public Reply vote(String jsonString) {
        long postId;
        try (Connection connection = dataSource.getConnection()) {
            JsonObject object = new JsonParser().parse(jsonString).getAsJsonObject();
            int vote = object.get("vote").getAsInt();
            postId = object.get("post").getAsLong();
            final String column = vote == -1 ? "dislikes" : "likes";
            String query = new StringBuilder("UPDATE ")
                    .append(tableName)
                    .append(" SET ")
                    .append(column)
                    .append(" = ")
                    .append(column)
                    .append(" + 1, points = points + ")
                    .append(Integer.toString(vote))
                    .append(" WHERE id = ?").toString();
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setLong(1, postId);
                ps.executeUpdate();
            } catch (SQLException e) {
                return new Reply(Status.NOT_FOUND);
            }
        } catch (Exception e) {
            return new Reply(Status.INVALID_REQUEST);
        }

        return new Reply(Status.OK, details(postId, null).getObject());
    }
}
