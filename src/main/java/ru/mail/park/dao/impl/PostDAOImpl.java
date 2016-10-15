package ru.mail.park.dao.impl;

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
            final StringBuilder query = new StringBuilder("INSERT INTO ");
            query.append(tableName);
            query.append("(date, forum, isApproved, isDeleted, isEdited, isHighlighted, isSpam, message, parent, thread, user)");
            query.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            try (PreparedStatement ps = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS)) {
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
}
