package ru.mail.park.controllers;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.mail.park.dao.PostDAO;
import ru.mail.park.dao.impl.PostDAOImpl;
import ru.mail.park.response.RestResponse;

import javax.sql.DataSource;

/**
 * Created by zac on 15.10.16.
 */

@RestController
@RequestMapping(value = "/db/api/post")
public class PostController extends BaseController {
    private PostDAO postDAO;

    public PostController(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    void init() {
        super.init();
        postDAO = new PostDAOImpl(dataSource);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public RestResponse create(@RequestBody String body){
        return new RestResponse(postDAO.create(body));
    }

}
