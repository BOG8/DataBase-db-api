package ru.mail.park.controllers;

import org.springframework.web.bind.annotation.*;
import ru.mail.park.dao.ThreadDAO;
import ru.mail.park.dao.impl.ThreadDAOImpl;
import ru.mail.park.response.RestResponse;

import javax.sql.DataSource;

/**
 * Created by zac on 15.10.16.
 */

@RestController
@RequestMapping(value = "/db/api/thread")
public class ThreadController extends BaseController {
    private ThreadDAO threadDAO;

    public ThreadController(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    void init() {
        super.init();
        threadDAO = new ThreadDAOImpl(dataSource);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public RestResponse create(@RequestBody String body){
        return new RestResponse(threadDAO.create(body));
    }

    @RequestMapping(value = "/details", method = RequestMethod.GET)
    public RestResponse details(@RequestParam(value = "thread", required = true) int threadId,
                                @RequestParam(value = "related", required = false) String[] related) {
        return new RestResponse(threadDAO.details(threadId, related));
    }

    @RequestMapping(value = "/close", method = RequestMethod.POST)
    public RestResponse close(@RequestBody String body) {
        return new RestResponse(threadDAO.close(body));
    }

    @RequestMapping(value = "/open", method = RequestMethod.POST)
    public RestResponse open(@RequestBody String body) {
        return new RestResponse(threadDAO.open(body));
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public RestResponse remove(@RequestBody String body) {
        return new RestResponse(threadDAO.remove(body));
    }

    @RequestMapping(value = "/restore", method = RequestMethod.POST)
    public RestResponse restore(@RequestBody String body) {
        return new RestResponse(threadDAO.restore(body));
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public RestResponse update(@RequestBody String body) {
        return new RestResponse(threadDAO.update(body));
    }

    @RequestMapping(value = "/vote", method = RequestMethod.POST)
    public RestResponse vote(@RequestBody String body) {
        return new RestResponse(threadDAO.vote(body));
    }

    @RequestMapping(value = "/subscribe", method = RequestMethod.POST)
    public RestResponse subscribe(@RequestBody String body) {
        return new RestResponse(threadDAO.subscribe(body));
    }

    @RequestMapping(value = "/unsubscribe", method = RequestMethod.POST)
    public RestResponse unsubscribe(@RequestBody String body) {
        return new RestResponse(threadDAO.unsubscribe(body));
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, params = {"forum"})
    public RestResponse listForum(@RequestParam(value = "forum", required = true) String forum,
                                  @RequestParam(value = "since", required = false) String since,
                                  @RequestParam(value = "limit", required = false) Long limit,
                                  @RequestParam(value = "order", required = false) String order) {
        return new RestResponse(threadDAO.listForum(forum, since, limit, order));
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, params = {"user"})
    public RestResponse listUser(@RequestParam(value = "user", required = true) String user,
                                        @RequestParam(value = "since", required = false) String since,
                                        @RequestParam(value = "limit", required = false) Long limit,
                                        @RequestParam(value = "order", required = false) String order) {
        return new RestResponse(threadDAO.listUser(user, since, limit, order));
    }


    @RequestMapping(value = "/listPosts", method = RequestMethod.GET)
    public RestResponse listPosts(@RequestParam(value = "thread", required = true) Long threadId,
                                  @RequestParam(value = "since", required = false) String since,
                                  @RequestParam(value = "limit", required = false) Long limit,
                                  @RequestParam(value = "sort", required = false) String sort,
                                  @RequestParam(value = "order", required = false) String order) {
        return new RestResponse(threadDAO.listPosts(threadId, since, limit, sort, order));
    }
}