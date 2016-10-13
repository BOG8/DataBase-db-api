package ru.mail.park.dao.impl;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
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
            System.out.println("Start");
            final JsonParser parser = new JsonParser();
            user = new User(parser.parse(jsonString).getAsJsonObject());
            String query = "INSERT INTO " + tableName +
                    "(about, email, isAnonymous, name, username) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement prepStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                prepStatement.setString(1, user.getAbout());
                prepStatement.setString(2, user.getEmail());
                prepStatement.setBoolean(3, user.isAnonymous());
                prepStatement.setString(4, user.getName());
                prepStatement.setString(5, user.getUsername());
                prepStatement.executeUpdate();
                try (ResultSet resultSet = prepStatement.getGeneratedKeys()) {
                    resultSet.next();
                    user.setId(resultSet.getLong(1));
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (JsonSyntaxException | SQLException e) {
            return new Reply(Status.INVALID_REQUEST);
        }

        return new Reply(Status.OK, user);
    }
}
