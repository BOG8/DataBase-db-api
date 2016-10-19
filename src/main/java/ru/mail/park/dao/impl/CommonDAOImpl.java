package ru.mail.park.dao.impl;

import ru.mail.park.dao.*;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zac on 16.10.16.
 */

public class CommonDAOImpl implements CommonDAO {
    private UserDAO userDAO;
    private ForumDAO forumDAO;
    private ThreadDAO threadDAO;
    private PostDAO postDAO;

    public CommonDAOImpl(DataSource dataSource) {
        userDAO = new UserDAOImpl(dataSource);
        forumDAO = new ForumDAOImpl(dataSource);
        threadDAO = new ThreadDAOImpl(dataSource);
        postDAO = new PostDAOImpl(dataSource);
    }

    @Override
    public void truncateAllTables() {
        userDAO.truncateTable();
        forumDAO.truncateTable();
        threadDAO.truncateTable();
        postDAO.truncateTable();
    }

    @Override
    public Map<String, Long> getAmounts() {
        Map<String, Long> response = new HashMap<>();
        response.put("user",userDAO.getAmount());
        response.put("forum", forumDAO.getAmount());
        response.put("thread", threadDAO.getAmount());
        response.put("post", postDAO.getAmount());

        return response;
    }
}
