package ru.mail.park.response;

import java.util.Map;

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

    public RestResponse(String response) {
        this.response = response;
    }

    public int getCode() {
        return code;
    }

    public Object getResponse() {
        return response;
    }
}
