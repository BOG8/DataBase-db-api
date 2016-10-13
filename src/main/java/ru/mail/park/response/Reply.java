package ru.mail.park.response;

import org.jetbrains.annotations.Nullable;

/**
 * Created by zac on 11.10.16.
 */

public class Reply {
    private Integer code;
    private Object object;

    public Integer getCode() {
        return code;
    }

    public Object getObject() {
        return object;
    }

    public Reply(Integer code, @Nullable Object object) {
        this.code = code;
        this.object = object;
    }

    public Reply(Integer code) {
        this.code = code;
        this.object = "";
    }
}
