package ru.mail.park.dao.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ru.mail.park.dao.ThreadDAO;
import ru.mail.park.model.Post;
import ru.mail.park.model.Thread;
import ru.mail.park.response.Reply;
import ru.mail.park.response.Status;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

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
                if (Arrays.asList(related).contains("thread")) {
                    return new Reply(Status.INCORRECT_REQUEST);
                }
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
    public Reply open(String jsonString) {
        try (Connection connection = dataSource.getConnection())  {
            Integer thread = new JsonParser().parse(jsonString).getAsJsonObject().get("thread").getAsInt();
            try {
                String query = new StringBuilder("UPDATE ")
                        .append(tableName)
                        .append(" SET isClosed = 0 WHERE id = ?").toString();
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
    public Reply remove(String jsonString) {
        try (Connection connection = dataSource.getConnection())  {
            Integer thread = new JsonParser().parse(jsonString).getAsJsonObject().get("thread").getAsInt();
            try {
                String query = new StringBuilder("UPDATE ")
                        .append(tableName)
                        .append(" SET isDeleted = 1, posts = 0 WHERE id = ?").toString();
                try (PreparedStatement ps = connection.prepareStatement(query)) {
                    ps.setInt(1, thread);
                    ps.execute();
                }
                query = "UPDATE Post SET isDeleted = 1 WHERE thread = ?";
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
    public Reply restore(String jsonString) {
        try (Connection connection = dataSource.getConnection())  {
            Integer thread = new JsonParser().parse(jsonString).getAsJsonObject().get("thread").getAsInt();
            try {
                String query = "UPDATE Post SET isDeleted = 0 WHERE thread = ?";
                try (PreparedStatement ps = connection.prepareStatement(query)) {
                    ps.setInt(1, thread);
                    ps.executeUpdate();
                }

                Long countPosts = null;
                query = "SELECT COUNT(*) AS countPosts FROM Post WHERE thread = ?";
                try(PreparedStatement ps = connection.prepareStatement(query)) {
                    ps.setLong(1, thread);
                    try (ResultSet resultSet = ps.executeQuery()) {
                        resultSet.next();
                        countPosts = resultSet.getLong("countPosts");
                    }
                }

                query = new StringBuilder("UPDATE ")
                        .append(tableName)
                        .append(" SET isDeleted = 0, posts = ? WHERE id = ?").toString();
                try (PreparedStatement ps = connection.prepareStatement(query)) {
                    ps.setLong(1, countPosts);
                    ps.setInt(2, thread);
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
    public Reply update(String jsonString) {
        long threadId;
        try (Connection connection = dataSource.getConnection())  {
            JsonObject object = new JsonParser().parse(jsonString).getAsJsonObject();
            threadId = object.get("thread").getAsInt();
            String message = object.get("message").getAsString();
            String slug = object.get("slug").getAsString();
            String query = new StringBuilder("UPDATE ")
                    .append(tableName)
                    .append(" SET message = ?, slug = ? WHERE id = ?").toString();
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, message);
                ps.setString(2, slug);
                ps.setLong(3, threadId);
                ps.executeUpdate();
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (Exception e) {
            return new Reply(Status.INVALID_REQUEST);
        }

        return details(threadId, null);
    }

    @Override
    public Reply vote(String jsonString) {
        long threadId;
        try (Connection connection = dataSource.getConnection())  {
            JsonObject object = new JsonParser().parse(jsonString).getAsJsonObject();
            Integer vote = object.get("vote").getAsInt();
            threadId = object.get("thread").getAsInt();
            String column = vote == -1 ? "dislikes" : "likes";
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
                ps.setLong(1, threadId);
                ps.executeUpdate();
            } catch (SQLException e) {
                return new Reply(Status.NOT_FOUND);
            }
        } catch (Exception e) {
            return new Reply(Status.INVALID_REQUEST);
        }

        return details(threadId, null);
    }

    @Override
    public Reply subscribe(String jsonString) {
        try (Connection connection = dataSource.getConnection())  {
            JsonObject object = new JsonParser().parse(jsonString).getAsJsonObject();
            String user = object.get("user").getAsString();
            Integer thread = object.get("thread").getAsInt();
            try {
                String query = "INSERT INTO Subscriptions (user, thread) VALUES (?,?)";
                try (PreparedStatement ps = connection.prepareStatement(query)) {
                    ps.setString(1, user);
                    ps.setInt(2, thread);
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
    public Reply unsubscribe(String jsonString) {
        try (Connection connection = dataSource.getConnection())  {
            JsonObject object = new JsonParser().parse(jsonString).getAsJsonObject();

            String user = object.get("user").getAsString();
            Integer thread = object.get("thread").getAsInt();

            try {
                String query = "DELETE FROM Subscriptions WHERE user = ? AND thread = ?";
                try (PreparedStatement ps = connection.prepareStatement(query)) {
                    ps.setString(1, user);
                    ps.setInt(2, thread);
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
    public Reply listForum(String forum, String since, Long limit, String order) {
        return new ForumDAOImpl(dataSource).listThreads(forum, since, limit, order, null);
    }

    @Override
    public Reply listUser(String user, String since, Long limit, String order) {
        ArrayList<Thread> threads = new ArrayList<>();
        try (Connection connection = dataSource.getConnection())  {
            StringBuilder query = new StringBuilder("SELECT * FROM Thread WHERE user = ?");

            if (since != null) {
                query.append("AND date >= '");
                query.append(since);
                query.append("' ");
            }

            query.append("ORDER BY date ");
            if (order != null) {
                if (order.equals("asc")) {
                    query.append("ASC ");
                } else {
                    query.append("DESC ");
                }
            } else {
                query.append("DESC ");
            }

            if (limit != null) {
                query.append("LIMIT ");
                query.append(limit);
            }

            try(PreparedStatement ps = connection.prepareStatement(query.toString())) {
                ps.setString(1, user);
                try (ResultSet resultSet = ps.executeQuery()) {
                    while (resultSet.next()) {
                        Thread thread = new Thread(resultSet);
                        threads.add(thread);
                    }
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (Exception e) {
            return new Reply(Status.INVALID_REQUEST);
        }

        return new Reply(Status.OK, threads);
    }

    @Override
    public Reply listPosts(Long threadId, String since, Long limit, String sort, String order) {
        ArrayList<Post> posts = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            if (sort == null || sort.equals("flat")) {
                StringBuilder query = new StringBuilder("SELECT * FROM Post WHERE thread = ? ");

                if (since != null) {
                    query.append("AND date >= '");
                    query.append(since);
                    query.append("' ");
                }

                query.append("ORDER BY date ");
                if (order != null) {
                    if (order.equals("asc")) {
                        query.append("ASC ");
                    } else {
                        query.append("DESC ");
                    }
                } else {
                    query.append("DESC ");
                }

                if (limit != null) {
                    query.append("LIMIT ");
                    query.append(limit);
                }

                try (PreparedStatement ps = connection.prepareStatement(query.toString())) {
                    ps.setLong(1, threadId);
                    try (ResultSet resultSet = ps.executeQuery()) {
                        while (resultSet.next()) {
                            posts.add(new Post(resultSet));
                        }
                        return new Reply(Status.OK, posts);
                    }
                } catch (SQLException e) {
                    return handeSQLException(e);
                }
            }

            if (sort.equals("tree")) {
                StringBuilder query = new StringBuilder();
                if (order == null || order.equals("desc")) {
                    query.append("SELECT patch FROM Post WHERE thread = ? ");

                    if (since != null) {
                        query.append("AND date >= '");
                        query.append(since);
                        query.append("' ");
                    }

                    query.append("AND patch LIKE '____' ");
                    query.append("ORDER BY patch DESC ");

                    if (limit != null) {
                        query.append("LIMIT ");
                        query.append(limit);
                    }

                    ArrayList<String> patches = new ArrayList<>();
                    try (PreparedStatement ps = connection.prepareStatement(query.toString())) {
                        ps.setLong(1, threadId);
                        try (ResultSet resultSet = ps.executeQuery()) {
                            while (resultSet.next()) {
                                patches.add(resultSet.getString("patch"));
                            }
                        }
                    } catch (SQLException e) {
                        return handeSQLException(e);
                    }

                    if (limit != null) {
                        for (int i = 0; i < patches.size() && posts.size() < limit; i++) {
                            long currentLimit = limit - posts.size();
                            StringBuilder unionQuery = new StringBuilder();
                            unionQuery.append("SELECT * FROM Post WHERE thread = ? AND patch LIKE '");
                            unionQuery.append(patches.get(i));
                            unionQuery.append("%' ORDER BY patch ASC LIMIT ");
                            unionQuery.append(currentLimit);

                            try (PreparedStatement ps = connection.prepareStatement(unionQuery.toString())) {
                                ps.setLong(1, threadId);
                                try (ResultSet resultSet = ps.executeQuery()) {
                                    while (resultSet.next()) {
                                        posts.add(new Post(resultSet));
                                    }
                                }
                            } catch (SQLException e) {
                                return handeSQLException(e);
                            }
                        }
                    } else {
                        // При limit == null будет перевод на parent_sort DESC
                    }

                } else {
                    query.append("SELECT * FROM Post WHERE thread = ? ");

                    if (since != null) {
                        query.append("AND date >= '");
                        query.append(since);
                        query.append("' ");
                    }

                    query.append("ORDER BY patch ");
                    query.append("ASC ");

                    if (limit != null) {
                        query.append("LIMIT ");
                        query.append(limit);
                    }

                    try (PreparedStatement ps = connection.prepareStatement(query.toString())) {
                        ps.setLong(1, threadId);
                        try (ResultSet resultSet = ps.executeQuery()) {
                            while (resultSet.next()) {
                                posts.add(new Post(resultSet));
                            }
                        }
                    } catch (SQLException e) {
                        return handeSQLException(e);
                    }
                }

            }

            if (sort.equals("parent_tree")) {
                StringBuilder query = new StringBuilder("SELECT patch FROM Post WHERE thread = ? ");

                if (since != null) {
                    query.append("AND date >= '");
                    query.append(since);
                    query.append("' ");
                }

                query.append("AND patch LIKE '____' ");

                query.append("ORDER BY patch ");
                if (order != null) {
                    if (order.equals("asc")) {
                        query.append("ASC ");
                    } else {
                        query.append("DESC ");
                    }
                } else {
                    query.append("DESC ");
                }

                if (limit != null) {
                    query.append("LIMIT ");
                    query.append(limit);
                }

                ArrayList<String> patches = new ArrayList<>();
                try (PreparedStatement ps = connection.prepareStatement(query.toString())) {
                    ps.setLong(1, threadId);
                    try (ResultSet resultSet = ps.executeQuery()) {
                        while (resultSet.next()) {
                            patches.add(resultSet.getString("patch"));
                        }
                    }
                } catch (SQLException e) {
                    return handeSQLException(e);
                }

                for (int i = 0; i < patches.size(); i++) {
                    StringBuilder unionQuery = new StringBuilder();
                    unionQuery.append("SELECT * FROM Post WHERE thread = ? AND patch LIKE '");
                    unionQuery.append(patches.get(i));
                    unionQuery.append("%' ORDER BY patch ASC");

                    try (PreparedStatement ps = connection.prepareStatement(unionQuery.toString())) {
                        ps.setLong(1, threadId);
                        try (ResultSet resultSet = ps.executeQuery()) {
                            while (resultSet.next()) {
                                posts.add(new Post(resultSet));
                            }
                        }
                    } catch (SQLException e) {
                        return handeSQLException(e);
                    }
                }
            }
        } catch (Exception e) {
            return new Reply(Status.INVALID_REQUEST);
        }

        return new Reply(Status.OK, posts);
    }

}

