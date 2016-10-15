package ru.mail.park.model;

import com.google.gson.JsonObject;

/**
 * Created by zac on 09.10.16.
 */

public class Thread {
    public static final String TABLE_NAME = "Thread";
    public static final String DATE_COLUMN = "date";
    public static final String FORUM_COLUMN = "forum";
    public static final String ID_COLUMN = "id";
    public static final String ISCLOSED_COLUMN = "isClosed";
    public static final String ISDELETED_COLUMN = "isDeleted";
    public static final String MESSAGE_COLUMN = "message";
    public static final String SLUG_COLUMN = "slug";
    public static final String TITLE_COLUMN = "title";
    public static final String USER_COLUMN = "user";

    private String date;
    private String forum;
    private long id;
    private boolean isClosed;
    private boolean isDeleted;
    private String message;
    private String slug;
    private String title;
    private String user;

    public Thread(JsonObject object) {
        date = object.get(DATE_COLUMN).getAsString();
        forum = object.get(FORUM_COLUMN).getAsString();
        id = object.has(ID_COLUMN) ? object.get(ID_COLUMN).getAsInt() : 0;
        isClosed = object.get(ISCLOSED_COLUMN).getAsBoolean();
        isDeleted = object.has(ISDELETED_COLUMN) && object.get(ISDELETED_COLUMN).getAsBoolean();
        message = object.get(MESSAGE_COLUMN).getAsString();
        slug = object.get(SLUG_COLUMN).getAsString();
        title = object.get(TITLE_COLUMN).getAsString();
        user = object.get(USER_COLUMN).getAsString();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getForum() {
        return forum;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(boolean isClosed) {
        this.isClosed = isClosed;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
