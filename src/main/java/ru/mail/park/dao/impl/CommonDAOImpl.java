package ru.mail.park.dao.impl;

import ru.mail.park.dao.*;

import javax.sql.DataSource;

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
}
