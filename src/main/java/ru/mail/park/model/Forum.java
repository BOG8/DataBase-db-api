package ru.mail.park.model;

import com.google.gson.JsonObject;

/**
 * Created by zac on 09.10.16.
 */

public class Forum {
    public static final String TABLE_NAME = "Forum";
    public static final String ID_COLUMN = "id";
    public static final String NAME_COLUMN = "name";
    public static final String SHORTNAME_COLUMN = "short_name";
    public static final String USER_COLUMN = "user";

    private long id;
    private String name;
    private String shortName;
    private String user;

    public Forum(long id, String name, String shortName, String user) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.user = user;
    }

    public Forum(JsonObject object) {
        id = object.has(ID_COLUMN) ? object.get(ID_COLUMN).getAsInt() : 0;
        name = object.get(NAME_COLUMN).getAsString();
        shortName = object.get(SHORTNAME_COLUMN).getAsString();
        user = object.get(USER_COLUMN).getAsString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShort_name() {
        return shortName;
    }

    public void setShort_name(String shortName) {
        this.shortName = shortName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
