package ru.mail.park.controllers;

import ru.mail.park.dao.Reply;
import ru.mail.park.dao.Status;

/**
 * Created by zac on 11.10.16.
 */

public class RestResponse {
    private int code = Status.OK;
    private Object response;

    public RestResponse(Reply reply) {
        this.code = reply.getCode();
        this.response = reply.getObject();
    }

    public int getCode() {
        return code;
    }

    public Object getResponse() {
        return response;
    }
}
