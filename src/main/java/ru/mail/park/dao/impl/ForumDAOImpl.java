package ru.mail.park.dao.impl;

import com.google.gson.JsonParser;
import com.sun.xml.internal.ws.api.ha.StickyFeature;
import ru.mail.park.dao.ForumDAO;
import ru.mail.park.model.Post;
import ru.mail.park.model.Thread;
import ru.mail.park.model.User;
import ru.mail.park.response.Reply;
import ru.mail.park.response.Status;
import ru.mail.park.model.Forum;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by zac on 11.10.16.
 */

public class ForumDAOImpl extends BaseDAOImpl implements ForumDAO {

    public ForumDAOImpl(DataSource dataSource) {
        this.tableName = Forum.TABLE_NAME;
        this.dataSource = dataSource;
    }

    @Override
    public Reply create(String jsonString) {
        final Forum forum;
        try (Connection connection = dataSource.getConnection()){
            forum = new Forum(new JsonParser().parse(jsonString).getAsJsonObject());
            String query = new StringBuilder("INSERT INTO ")
                    .append(tableName)
                    .append("(name, short_name, user) VALUES (?, ?, ?)").toString();
            try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, forum.getName());
                ps.setString(2, forum.getShort_name());
                ps.setString(3, forum.getUser().toString());
                ps.executeUpdate();
                try (ResultSet resultSet = ps.getGeneratedKeys()) {
                    resultSet.next();
                    forum.setId(resultSet.getLong(1));
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (Exception e) {
            return new Reply(Status.INVALID_REQUEST);
        }

        return new Reply(Status.OK, forum);
    }

    @Override
    public Reply details(String shortName, String[] related) {
        Forum forum;
        try (Connection connection = dataSource.getConnection()) {
            String query = new StringBuilder("SELECT * FROM ")
                    .append(tableName)
                    .append(" WHERE short_name = ?").toString();
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, shortName);

                try (ResultSet resultSet = ps.executeQuery()) {
                    resultSet.next();

                    forum = new Forum(resultSet);
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
            if (related != null) {
                if (Arrays.asList(related).contains("user")) {
                    String email = forum.getUser().toString();
                    forum.setUser(new UserDAOImpl(dataSource).details(email).getObject());
                }
            }
        } catch (Exception e) {
            return new Reply(Status.INVALID_REQUEST);
        }

        return new Reply(Status.OK, forum);
    }

    @Override
    public Reply listPosts(String forum, String since, Long limit, String order, String[] related) {
        ArrayList<Post> posts = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            StringBuilder query = new StringBuilder("SELECT * FROM Post WHERE forum = ? ");

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
                ps.setString(1, forum);
                try (ResultSet resultSet = ps.executeQuery()) {
                    while (resultSet.next()) {
                        Post post = new Post(resultSet);
                        if (related != null) {
                            if (Arrays.asList(related).contains("forum")) {
                                String shortName = post.getForum().toString();
                                post.setForum(new ForumDAOImpl(dataSource).details(shortName, null).getObject());
                            }
                            if (Arrays.asList(related).contains("thread")) {
                                long threadId = Long.parseLong(post.getThread().toString());
                                post.setThread(new ThreadDAOImpl(dataSource).details(threadId, null).getObject());
                            }
                            if (Arrays.asList(related).contains("user")) {
                                String email = post.getUser().toString();
                                post.setUser(new UserDAOImpl(dataSource).details(email).getObject());
                            }
                        }
                        posts.add(post);
                    }
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (Exception e) {
            return new Reply(Status.INVALID_REQUEST);
        }

        return new Reply(Status.OK, posts);
    }

    @Override
    public Reply listThreads(String forum, String since, Long limit, String order, String[] related) {
        ArrayList<Thread> threads = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            StringBuilder query = new StringBuilder("SELECT * FROM Thread WHERE forum = ? ");

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
                ps.setString(1, forum);
                try (ResultSet resultSet = ps.executeQuery()) {
                    while (resultSet.next()) {
                        Thread thread = new Thread(resultSet);
                        if (related != null) {
                            if (Arrays.asList(related).contains("forum")) {
                                String shortName = thread.getForum().toString();
                                thread.setForum(new ForumDAOImpl(dataSource).details(shortName, null).getObject());
                            }
                            if (Arrays.asList(related).contains("user")) {
                                String email = thread.getUser().toString();
                                thread.setUser(new UserDAOImpl(dataSource).details(email).getObject());
                            }
                        }
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
    public Reply listUsers(String forum, Long sinceId, Long limit, String order) {
        ArrayList<User> users = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            StringBuilder query = new StringBuilder("SELECT U.*, ")
                    .append("group_concat(distinct JUF1.follower) AS followers, ")
                    .append("group_concat(distinct JUF2.user) AS following, ")
                    .append("group_concat(distinct JUS.thread) AS subscriptions\n")
                    .append("FROM Thread UT\n")
                    .append("JOIN User U ON U.email = UT.user\n")
                    .append("LEFT JOIN Followers JUF1 ON U.email = JUF1.user\n")
                    .append("LEFT JOIN Followers JUF2 ON U.email = JUF2.follower\n")
                    .append("LEFT JOIN Subscriptions JUS ON U.email= JUS.user\n")
                    .append("WHERE UT.forum = ? ");

            if (sinceId != null) {
                query.append("AND U.id >= ");
                query.append(sinceId);
                query.append(" ");
            }
            query.append("GROUP BY U.email ");
            query.append("ORDER BY U.name ");
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
                ps.setString(1, forum);
                try (ResultSet resultSet = ps.executeQuery()) {
                    while (resultSet.next()) {
                        User temp = new User(resultSet);
                        users.add(temp);
                    }
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (Exception e) {
            return new Reply(Status.INVALID_REQUEST);
        }

        return new Reply(Status.OK, users);
    }
}
