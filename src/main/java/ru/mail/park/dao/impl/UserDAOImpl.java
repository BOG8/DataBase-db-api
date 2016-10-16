package ru.mail.park.dao.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ru.mail.park.dao.UserDAO;
import ru.mail.park.model.User;
import ru.mail.park.response.Reply;
import ru.mail.park.response.Status;

import javax.sql.DataSource;
import java.sql.*;

/**
 * Created by zac on 13.10.16.
 */


public class UserDAOImpl extends BaseDAOImpl implements UserDAO {

    public UserDAOImpl(DataSource dataSource) {
        this.tableName = User.TABLE_NAME;
        this.dataSource = dataSource;
    }

    @Override
    public Reply create(String jsonString) {
        final User user;
        try (Connection connection = dataSource.getConnection()){
            user = new User(new JsonParser().parse(jsonString).getAsJsonObject());
            String query = new StringBuilder("INSERT INTO ")
                    .append(tableName)
                    .append("(about, email, isAnonymous, name, username) VALUES (?, ?, ?, ?, ?)").toString();
            try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, user.getAbout());
                ps.setString(2, user.getEmail());
                ps.setBoolean(3, user.getIsAnonymous());
                ps.setString(4, user.getName());
                ps.setString(5, user.getUsername());
                ps.executeUpdate();
                try (ResultSet resultSet = ps.getGeneratedKeys()) {
                    resultSet.next();
                    user.setId(resultSet.getLong(1));
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (Exception e) {
            return new Reply(Status.INVALID_REQUEST);
        }

        return new Reply(Status.OK, user);
    }

    @Override
    public Reply details(String email) {
        User user;
        try (Connection connection = dataSource.getConnection()) {
            String query = new StringBuilder("SELECT U.*, ")
                    .append("group_concat(distinct JUF1.follower) as followers, ")
                    .append("group_concat(distinct JUF2.user) as following, ")
                    .append("group_concat(distinct JUS.thread) as subscriptions\n")
                    .append("FROM User U \n")
                    .append("LEFT JOIN Followers JUF1 ON U.email = JUF1.user\n")
                    .append("LEFT JOIN Followers JUF2 ON U.email = JUF2.follower\n")
                    .append("LEFT JOIN Subscriptions JUS ON U.email= JUS.user\n")
                    .append("WHERE U.email = ?").toString();
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, email);
                try (ResultSet resultSet = ps.executeQuery()) {
                    resultSet.next();
                    user = new User(resultSet);
                } catch (Exception e) {
                    return new Reply(Status.NOT_FOUND);
                }
            }
        } catch (SQLException e) {
            return new Reply(Status.INCORRECT_REQUEST);
        }

        return new Reply(Status.OK, user);
    }

    @Override
    public Reply follow(String jsonString) {
        final String follower;
        try (Connection connection = dataSource.getConnection())  {
            final JsonObject object = new JsonParser().parse(jsonString).getAsJsonObject();
            follower = object.get("follower").getAsString();
            String followee = object.get("followee").getAsString();
            try {
                String query = "INSERT INTO Followers (user, follower) VALUES (?,?)";
                try (PreparedStatement ps = connection.prepareStatement(query)) {
                    ps.setString(1, followee);
                    ps.setString(2, follower);
                    ps.execute();
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (Exception e) {
            return new Reply(Status.INVALID_REQUEST);
        }

        return details(follower);
    }

    @Override
    public Reply unfollow(String data) {
        final String follower;
        try (Connection connection = dataSource.getConnection())  {
            final JsonObject object = new JsonParser().parse(data).getAsJsonObject();
            follower = object.get("follower").getAsString();
            String followee = object.get("followee").getAsString();
            try {
                String query = "DELETE FROM Followers WHERE user=? AND follower=?";
                try (PreparedStatement ps = connection.prepareStatement(query)) {
                    ps.setString(1, followee);
                    ps.setString(2, follower);
                    ps.execute();
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (Exception e) {
            return new Reply(Status.INVALID_REQUEST);
        }

        return details(follower);
    }

    @Override
    public Reply updateProfile(String jsonString) {
        String email;
        try (Connection connection = dataSource.getConnection())  {
            JsonObject object = new JsonParser().parse(jsonString).getAsJsonObject();
            String about = object.get("about").getAsString();
            email = object.get("user").getAsString();
            String name = object.get("name").getAsString();
            String query = "UPDATE " + tableName + " SET about=?, name=? WHERE email=?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, about);
                ps.setString(2, name);
                ps.setString(3, email);
                ps.executeUpdate();
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (Exception e ) {
            return new Reply(Status.INVALID_REQUEST);
        }

        return details(email);
    }
}
