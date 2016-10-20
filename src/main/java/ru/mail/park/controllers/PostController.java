package ru.mail.park.controllers;

import org.springframework.web.bind.annotation.*;
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
    public RestResponse create(@RequestBody String body) {
        return new RestResponse(postDAO.create(body));
    }

    @RequestMapping(value = "/details", method = RequestMethod.GET)
    public RestResponse details(@RequestParam(value = "post", required = true) long postId,
                                @RequestParam(value = "related", required = false) String[] related){
        return new RestResponse(postDAO.details(postId, related));
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public RestResponse remove(@RequestBody String body) {
        return new RestResponse(postDAO.remove(body));
    }

//    @RequestMapping(value = "/restore", method = RequestMethod.POST)
//    public RestResponse restore(@RequestBody String body){
//        return new RestResponse(postDAO.restore(body));
//    }
//
//    @RequestMapping(value = "/update", method = RequestMethod.POST)
//    public RestResponse update(@RequestBody String body){
//        return new RestResponse(postDAO.update(body));
//    }
//
//    @RequestMapping(value = "/vote", method = RequestMethod.POST)
//    public RestResponse vote(@RequestBody String body){
//        return new RestResponse(postDAO.vote(body));
//    }
//
    @RequestMapping(value = "/list", method = RequestMethod.GET, params = {"forum"})
    public RestResponse listForum(@RequestParam(value = "forum", required = true) String forum,
                                  @RequestParam(value = "since", required = false) String since,
                                  @RequestParam(value = "limit", required = false) Long limit,
                                  @RequestParam(value = "order", required = false) String order) {
        return new RestResponse(postDAO.listForum(forum, since, limit, order));
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, params = {"thread"})
    public RestResponse listUser(@RequestParam(value = "thread", required = true) Long threadId,
                                 @RequestParam(value = "since", required = false) String since,
                                 @RequestParam(value = "limit", required = false) Long limit,
                                 @RequestParam(value = "order", required = false) String order) {
        return new RestResponse(postDAO.listThread(threadId, since, limit, order));
    }
}
