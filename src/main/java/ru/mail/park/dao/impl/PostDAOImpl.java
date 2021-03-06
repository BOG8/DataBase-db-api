package ru.mail.park.dao.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ru.mail.park.dao.PostDAO;
import ru.mail.park.model.Post;
import ru.mail.park.model.Thread;
import ru.mail.park.response.Reply;
import ru.mail.park.response.Status;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

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
        try (Connection connection = dataSource.getConnection()) {
            post = new Post(new JsonParser().parse(jsonString).getAsJsonObject());

            String parentPatch = "";
            if (post.getParent() != null) {
                String query = new StringBuilder("SELECT patch FROM ")
                        .append(tableName)
                        .append(" WHERE id = ?").toString();
                try (PreparedStatement ps = connection.prepareStatement(query)) {
                    ps.setLong(1, post.getParent());
                    try (ResultSet resultSet = ps.executeQuery()) {
                        resultSet.next();
                        parentPatch = resultSet.getString("patch");
                    }
                }
            }

            StringBuilder buildedQuery = new StringBuilder("SELECT MAX(patch) AS Max_patch FROM ")
                    .append(tableName)
                    .append(" WHERE thread = ? AND patch LIKE '");
            if (!parentPatch.equals("")) {
                buildedQuery.append(parentPatch);
            }
            buildedQuery.append("____'");

            String maxPatch = null;
            try (PreparedStatement ps = connection.prepareStatement(buildedQuery.toString())) {
                ps.setLong(1, Long.parseLong(post.getThread().toString()));
                try (ResultSet resultSet = ps.executeQuery()) {
                    resultSet.next();
                    maxPatch = resultSet.getString("Max_patch");
                }
            }

            String resultPatch;
            if (maxPatch != null) {
                resultPatch = incPatch(maxPatch);
            } else {
                resultPatch = parentPatch + "0001";
            }

            String query = new StringBuilder("INSERT INTO ")
                    .append(tableName)
                    .append("(date, forum, isApproved, isDeleted, isEdited, isHighlighted, isSpam, message, parent, thread, user, patch)")
                    .append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)").toString();
            try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, post.getDate());
                ps.setString(2, post.getForum().toString());
                ps.setBoolean(3, post.getIsApproved());
                ps.setBoolean(4, post.getIsDeleted());
                ps.setBoolean(5, post.getIsEdited());
                ps.setBoolean(6, post.getIsHighlighted());
                ps.setBoolean(7, post.getIsSpam());
                ps.setString(8, post.getMessage());
                ps.setObject(9, post.getParent());
                ps.setLong(10, Long.parseLong(post.getThread().toString()));
                ps.setString(11, post.getUser().toString());
                ps.setString(12, resultPatch);
                ps.executeUpdate();
                try (ResultSet resultSet = ps.getGeneratedKeys()) {
                    resultSet.next();
                    post.setId(resultSet.getLong(1));
                    post.setPatch(resultPatch);
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }

            query = "UPDATE Thread SET posts = posts + 1 WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setLong(1, Long.parseLong(post.getThread().toString()));
                ps.executeUpdate();
            } catch (SQLException e) {
                return new Reply(Status.NOT_FOUND);
            }

            query = "INSERT INTO UsersOfForum(forum, email) VALUES (?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, post.getForum().toString());
                ps.setString(2, post.getUser().toString());
                ps.executeUpdate();
            } catch (SQLException e) {

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
            if (related != null) {
                if (Arrays.asList(related).contains("forum")) {
                    post.setForum(new ForumDAOImpl(dataSource).details(post.getForum().toString(), null).getObject());
                }
                if (Arrays.asList(related).contains("thread")) {
                    long threadId = Long.parseLong(post.getThread().toString());
                    post.setThread(new ThreadDAOImpl(dataSource).details(threadId, null).getObject());
                }
                if (Arrays.asList(related).contains("user")) {
                    post.setUser(new UserDAOImpl(dataSource).details(post.getUser().toString()).getObject());
                }
            }
        } catch (Exception e) {
            return new Reply(Status.INVALID_REQUEST);
        }

        return new Reply(Status.OK, post);
    }

    @Override
    public Reply remove(String jsonString) {
        try (Connection connection = dataSource.getConnection()) {
            Long postId = new JsonParser().parse(jsonString).getAsJsonObject().get("post").getAsLong();
            String query = new StringBuilder("UPDATE ")
                    .append(tableName)
                    .append(" SET isDeleted = 1 WHERE id = ?").toString();
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setLong(1, postId);
                ps.executeUpdate();
            } catch (SQLException e) {
                new Reply(Status.NOT_FOUND);
            }

            Post post;
            query = new StringBuilder("SELECT * FROM ")
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

            query = "UPDATE Thread SET posts = posts - 1 WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setLong(1, Long.parseLong(post.getThread().toString()));
                ps.executeUpdate();
            } catch (SQLException e) {
                return new Reply(Status.NOT_FOUND);
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

            Post post;
            query = new StringBuilder("SELECT * FROM ")
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

            query = "UPDATE Thread SET posts = posts + 1 WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setLong(1, Long.parseLong(post.getThread().toString()));
                ps.executeUpdate();
            } catch (SQLException e) {
                return new Reply(Status.NOT_FOUND);
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

        return details(postId, null);
    }

    @Override
    public Reply listForum(String forum, String since, Long limit, String order) {
        return new ForumDAOImpl(dataSource).listPosts(forum, since, limit, order, null);
    }

    @Override
    public Reply listThread(Long threadId, String since, Long limit, String order) {
        return new ThreadDAOImpl(dataSource).listPosts(threadId, since, limit, null, order);
    }

    public static String incPatch(String patch) {

        if (patch == null) {
            return "0001";
        }

        int patchLength = patch.length();

        if (!patch.endsWith("9")) {
            String lastStringNumber = patch.substring(patchLength - 1, patchLength);
            int lastNumber = Integer.parseInt(lastStringNumber);
            lastNumber++;
            patch = patch.substring(0, patchLength - 1) + lastNumber;
            return patch;
        }

        if (!patch.endsWith("99")) {
            String lastStringNumber = patch.substring(patchLength - 2, patchLength - 1);
            int lastNumber = Integer.parseInt(lastStringNumber);
            lastNumber++;
            patch = patch.substring(0, patchLength - 2) + lastNumber + "0";
            return patch;
        }

        if (!patch.endsWith("999")) {
            String lastStringNumber = patch.substring(patchLength - 3, patchLength - 2);
            int lastNumber = Integer.parseInt(lastStringNumber);
            lastNumber++;
            patch = patch.substring(0, patchLength - 3) + lastNumber + "00";
            return patch;
        }

        String lastStringNumber = patch.substring(patchLength - 4, patchLength - 3);
        int lastNumber = Integer.parseInt(lastStringNumber);
        lastNumber++;
        patch = patch.substring(0, patchLength - 4) + lastNumber + "000";

        return patch;
    }
    
}
