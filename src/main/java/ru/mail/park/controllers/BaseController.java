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
    @Autowired
    protected DataSource dataSource;

    @PostConstruct
    void init() {

    }
}
