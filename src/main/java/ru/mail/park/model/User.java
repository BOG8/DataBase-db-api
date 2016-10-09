package ru.mail.park.model;

/**
 * Created by zac on 09.10.16.
 */

public class User {
    public static final String TABLE_NAME = "User";
    public static final String ABOUT_COLUMN = "about";
    public static final String EMAIL_COLUMN = "email";
    public static final String ID_COLUMN = "id";
    public static final String ISANONYMOUS_COLUMN = "isAnonymous";
    public static final String NAME_COLUMN = "name";
    public static final String USERNAME_COLUMN = "username";

    private String about;
    private String email;
    private long id;
    private boolean isAnonymous;
    private String name;
    private String username;

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setIsAnonymous(boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
