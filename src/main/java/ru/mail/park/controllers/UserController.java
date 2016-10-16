package ru.mail.park.controllers;

import org.springframework.web.bind.annotation.*;
import ru.mail.park.dao.UserDAO;
import ru.mail.park.dao.impl.UserDAOImpl;
import ru.mail.park.response.RestResponse;

import javax.sql.DataSource;

/**
 * Created by zac on 13.10.16.
 */

@RestController
@RequestMapping(value = "/db/api/user")
public class UserController extends BaseController {
    private UserDAO userDAO;

    public UserController(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    void init() {
        super.init();
        userDAO = new UserDAOImpl(dataSource);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public RestResponse create(@RequestBody String body){
        return new RestResponse(userDAO.create(body));
    }

    @RequestMapping(value = "/details", method = RequestMethod.GET)
    public RestResponse details(@RequestParam(value = "user") String email) {
        return new RestResponse(userDAO.details(email));
    }

    @RequestMapping(value = "/follow", method = RequestMethod.POST)
    public RestResponse follow(@RequestBody String data) {
        return new RestResponse(userDAO.follow(data));
    }

    @RequestMapping(value = "/unfollow", method = RequestMethod.POST)
    public RestResponse unfollow(@RequestBody String data) {
        return new RestResponse(userDAO.unfollow(data));
    }
}
