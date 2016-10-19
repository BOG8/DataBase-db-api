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

//    @RequestMapping(value = "/follow", method = RequestMethod.POST)
//    public RestResponse follow(@RequestBody String body) {
//        return new RestResponse(userDAO.follow(body));
//    }
//
//    @RequestMapping(value = "/unfollow", method = RequestMethod.POST)
//    public RestResponse unfollow(@RequestBody String body) {
//        return new RestResponse(userDAO.unfollow(body));
//    }
//
//    @RequestMapping(value = "/updateProfile", method = RequestMethod.POST)
//    public RestResponse updateProfile(@RequestBody String body) {
//        return new RestResponse(userDAO.updateProfile(body));
//    }
//
//    @RequestMapping(value = "/listFollowers", method = RequestMethod.GET)
//    public RestResponse listFollowers(@RequestParam(value = "user", required = true) String email,
//                                      @RequestParam(value = "limit", required = false) Long limit,
//                                      @RequestParam(value = "order", required = false) String order,
//                                      @RequestParam(value = "since_id", required = false) Long sinceId) {
//        return new RestResponse(userDAO.listFollowers(email, limit, order, sinceId));
//    }
//
//
//    @RequestMapping(value = "/listFollowing", method = RequestMethod.GET)
//    public RestResponse listFollowing(@RequestParam(value = "user", required = true) String email,
//                                      @RequestParam(value = "limit", required = false) Long limit,
//                                      @RequestParam(value = "order", required = false) String order,
//                                      @RequestParam(value = "since_id", required = false) Long sinceId) {
//        return new RestResponse(userDAO.listFollowing(email, limit, order, sinceId));
//    }
//
//    @RequestMapping(value = "/listPosts", method = RequestMethod.GET)
//    public RestResponse listPosts(@RequestParam(value = "user", required = true) String email,
//                                  @RequestParam(value = "limit", required = false) Long limit,
//                                  @RequestParam(value = "order", required = false) String order,
//                                  @RequestParam(value = "since", required = false) String since){
//        return new RestResponse(userDAO.listPosts(email, limit, order, since));
//    }
}
