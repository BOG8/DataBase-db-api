package ru.mail.park.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.mail.park.dao.CommonDAO;
import ru.mail.park.dao.impl.CommonDAOImpl;
import ru.mail.park.response.RestResponse;

import javax.sql.DataSource;

/**
 * Created by zac on 16.10.16.
 */

@RestController
@RequestMapping(value = "/db/api")
public class CommonController extends BaseController {
    private CommonDAO commonDAO;

    public CommonController(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    void init() {
        super.init();
        commonDAO = new CommonDAOImpl(dataSource);
    }

    @RequestMapping(value = "/clear", method = RequestMethod.POST)
    public RestResponse clear() {
        commonDAO.truncateAllTables();
        return new RestResponse("OK");
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public RestResponse status() {
        return new RestResponse(commonDAO.getAmounts());
    }
}
