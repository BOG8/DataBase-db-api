package ru.mail.park.model;

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
    private String short_name;
    private String user;

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
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
