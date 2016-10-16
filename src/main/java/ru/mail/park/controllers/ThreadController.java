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
}