package ru.mail.park.dao.impl;

import com.google.gson.JsonParser;
import com.sun.xml.internal.ws.api.ha.StickyFeature;
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
            forum = new Forum(new JsonParser().parse(jsonString).getAsJsonObject());
            String query = new StringBuilder("INSERT INTO ")
                    .append(tableName)
                    .append("(name, short_name, user) VALUES (?, ?, ?)").toString();
            try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, forum.getName());
                ps.setString(2, forum.getShort_name());
                ps.setString(3, forum.getUser());
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

}
