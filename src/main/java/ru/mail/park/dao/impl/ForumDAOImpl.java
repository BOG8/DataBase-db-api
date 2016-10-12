package ru.mail.park.dao.impl;

import com.google.gson.JsonParser;
import ru.mail.park.dao.ForumDAO;
import ru.mail.park.dao.Reply;
import ru.mail.park.dao.Status;
import ru.mail.park.model.Forum;

import javax.sql.DataSource;
import java.sql.*;

/**
 * Created by zac on 11.10.16.
 */

public class ForumDAOImpl extends BaseDAOImpl implements ForumDAO {

    public ForumDAOImpl(DataSource dataSource) {
        this.tableName = "Forum";
        this.dataSource = dataSource;
    }

    @Override
    public Reply create(String jsonString) {
        Forum forum;
        try (Connection connection = dataSource.getConnection()){
            JsonParser parser = new JsonParser();
            forum = new Forum(parser.parse(jsonString).getAsJsonObject());
            String query = "INSERT INTO "+ tableName +" (name, short_name, user) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, forum.getName());
                stmt.setString(2, forum.getShortName());
                stmt.setString(3, forum.getUser());
                stmt.executeUpdate();
                try (ResultSet resultSet = stmt.getGeneratedKeys()) {
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

}
