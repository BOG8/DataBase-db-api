package ru.mail.park.model;

import com.google.gson.JsonObject;

import java.sql.ResultSet;

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
    public static final String FOLLOWERS_COLUMN = "followers";
    public static final String FOLLOWING_COLUMN = "following";
    public static final String SUBSCRIPTIONS_COLUMN = "subscriptions";
    public static final String COMMA = ",";

    private String about;
    private String email;
    private long id;
    private boolean isAnonymous;
    private String name;
    private String username;
    private String[] followers;
    private String[] following;
    private Long[] subscriptions;

    public User(String about, String email, long id, boolean isAnonymous, String name, String username) {
        this.about = about;
        this.email = email;
        this.id = id;
        this.isAnonymous = isAnonymous;
        this.name = name;
        this.username = username;
        this.followers = new String[]{};
        this.following = new String[]{};
        this.subscriptions = new Long[]{};
    }

    public User(JsonObject object) {
        about = object.get(ABOUT_COLUMN).getAsString();
        email = object.get(EMAIL_COLUMN).getAsString();
        id = object.has(ID_COLUMN) ? object.get(ID_COLUMN).getAsInt() : 0;
        isAnonymous = object.has(ISANONYMOUS_COLUMN) && object.get(ISANONYMOUS_COLUMN).getAsBoolean();
        name = object.get(NAME_COLUMN).getAsString();
        username = object.get(USERNAME_COLUMN).getAsString();
    }

    public User(ResultSet resultSet) throws Exception {
        about = resultSet.getString(ABOUT_COLUMN);
        email = resultSet.getString(EMAIL_COLUMN);
        id = resultSet.getLong(ID_COLUMN);
        isAnonymous = resultSet.getBoolean(ISANONYMOUS_COLUMN);
        name = resultSet.getString(NAME_COLUMN);
        username = resultSet.getString(USERNAME_COLUMN);

        if (followers != null) {
            this.followers = resultSet.getString(FOLLOWERS_COLUMN).split(COMMA);
        } else {
            this.followers = new String[]{};
        }
        if (following != null) {
            this.following = resultSet.getString(FOLLOWING_COLUMN).split(COMMA);
        } else {
            this.following = new String[]{};
        }
        if (subscriptions != null) {
            String[] subs = resultSet.getString(SUBSCRIPTIONS_COLUMN).split(COMMA);
            this.subscriptions = new Long[subs.length];
            for (int i=0; i < subs.length; i++) {
                this.subscriptions[i] = Long.parseLong(subs[i]);
            }
        } else {
            this.subscriptions = new Long[]{};
        }
    }

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

    public boolean getIsAnonymous() {
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

    public String[] getFollowers() {
        return followers;
    }

    public void setFollowers(String[] followers) {
        this.followers = followers;
    }

    public String[] getFollowing() {
        return following;
    }

    public void setFollowing(String[] following) {
        this.following = following;
    }

    public Long[] getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Long[] subscriptions) {
        this.subscriptions = subscriptions;
    }
}
