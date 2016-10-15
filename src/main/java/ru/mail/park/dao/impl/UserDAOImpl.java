package ru.mail.park.dao.impl;

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
            final StringBuilder query = new StringBuilder("INSERT INTO ");
            query.append(tableName);
            query.append("(about, email, isAnonymous, name, username) VALUES (?, ?, ?, ?, ?)");
            try (PreparedStatement ps = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS)) {
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
}
