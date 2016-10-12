package ru.mail.park.controllers;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.mail.park.dao.ForumDAO;
import ru.mail.park.dao.impl.ForumDAOImpl;

/**
 * Created by zac on 11.10.16.
 */

@RestController
@RequestMapping(value = "/db/api/forum")
public class ForumController extends BaseController {
    private ForumDAO forumDAO;

    @Override
    void init() {
        super.init();
        forumDAO = new ForumDAOImpl(dataSource);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public RestResponse create(@RequestBody String body){
        return new RestResponse(forumDAO.create(body));
    }
}
