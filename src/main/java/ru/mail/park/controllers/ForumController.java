package ru.mail.park.controllers;

import org.springframework.web.bind.annotation.*;
import ru.mail.park.dao.ForumDAO;
import ru.mail.park.dao.impl.ForumDAOImpl;
import ru.mail.park.response.RestResponse;

import javax.sql.DataSource;

/**
 * Created by zac on 11.10.16.
 */

@RestController
@RequestMapping(value = "/db/api/forum")
public class ForumController extends BaseController {
    private ForumDAO forumDAO;

    public ForumController(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    void init() {
        super.init();
        forumDAO = new ForumDAOImpl(dataSource);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public RestResponse create(@RequestBody String body){
        return new RestResponse(forumDAO.create(body));
    }

    @RequestMapping(value = "/details", method = RequestMethod.GET)
    public RestResponse details(@RequestParam(value = "forum", required = true) String shortName,
                                @RequestParam(value = "related", required = false) String[] related ) {
        return new RestResponse(forumDAO.details(shortName, related));
    }
}
