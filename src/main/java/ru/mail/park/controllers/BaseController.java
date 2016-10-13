package ru.mail.park.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * Created by zac on 11.10.16.
 */

@RestController
public abstract class BaseController {
    protected final DataSource dataSource;

    @Autowired
    public BaseController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    void init() {

    }
}
