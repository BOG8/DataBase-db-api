package ru.mail.park.dao.impl;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import ru.mail.park.dao.ForumDAO;
import ru.mail.park.response.Reply;
import ru.mail.park.response.Status;
import ru.mail.park.model.Forum;

import javax.sql.DataSource;
import java.sql.*;

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
            final JsonParser parser = new JsonParser();
            forum = new Forum(parser.parse(jsonString).getAsJsonObject());
            final String query = "INSERT INTO " + tableName +
                    "(name, short_name, user) VALUES (?, ?, ?)";
            try (PreparedStatement prepStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                prepStatement.setString(1, forum.getName());
                prepStatement.setString(2, forum.getShortName());
                prepStatement.setString(3, forum.getUser());
                prepStatement.executeUpdate();
                try (ResultSet resultSet = prepStatement.getGeneratedKeys()) {
                    resultSet.next();
                    forum.setId(resultSet.getLong(1));
                }
            } catch (SQLException e) {
                return handeSQLException(e);
            }
        } catch (SQLException | JsonSyntaxException e) {
            return new Reply(Status.INVALID_REQUEST);
        }

        return new Reply(Status.OK, forum);
    }

}
