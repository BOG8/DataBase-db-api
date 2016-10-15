package ru.mail.park.model;

import com.google.gson.JsonObject;

/**
 * Created by zac on 09.10.16.
 */

public class Post {
    public static final String TABLE_NAME = "Post";
    public static final String DATE_COLUMN = "date";
    public static final String FORUM_COLUMN = "forum";
    public static final String ID_COLUMN = "id";
    public static final String ISAPPROVED_COLUMN = "isApproved";
    public static final String ISDELETED_COLUMN = "isDeleted";
    public static final String ISEDITED_COLUMN = "isEdited";
    public static final String ISHIGHLIGHTED_COLUMN = "isHighlighted";
    public static final String ISSPAM_COLUMN = "isSpam";
    public static final String MESSAGE_COLUMN = "message";
    public static final String PARENT_COLUMN = "parent";
    public static final String THREAD_COLUMN = "thread";
    public static final String USER_COLUMN = "user";

    private String date;
    private String forum;
    private long id;
    private boolean isApproved;
    private boolean isDeleted;
    private boolean isEdited;
    private boolean isHighlighted;
    private boolean isSpam;
    private String message;
    private Long parent;
    private long thread;
    private String user;

    public Post(JsonObject object) {
        date = object.get(DATE_COLUMN).getAsString();
        forum = object.get(FORUM_COLUMN).getAsString();
        id = object.has(ID_COLUMN) ? object.get(ID_COLUMN).getAsInt() : 0;
        isApproved = object.has(ISAPPROVED_COLUMN) && object.get(ISAPPROVED_COLUMN).getAsBoolean();
        isDeleted = object.has(ISDELETED_COLUMN) && object.get(ISDELETED_COLUMN).getAsBoolean();
        isEdited = object.has(ISEDITED_COLUMN) && object.get(ISEDITED_COLUMN).getAsBoolean();
        isHighlighted = object.has(ISHIGHLIGHTED_COLUMN) && object.get(ISHIGHLIGHTED_COLUMN).getAsBoolean();
        isSpam = object.has(ISSPAM_COLUMN) && object.get(ISSPAM_COLUMN).getAsBoolean();
        message = object.get(MESSAGE_COLUMN).getAsString();
        parent = object.has(PARENT_COLUMN) ? object.get(PARENT_COLUMN).getAsLong() : null;
        thread = object.get(THREAD_COLUMN).getAsLong();
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

    public boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(boolean isApproved) {
        this.isApproved = isApproved;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public boolean getIsEdited() {
        return isEdited;
    }

    public void setIsEdited(boolean isEdited) {
        this.isEdited = isEdited;
    }

    public boolean getIsHighlighted() {
        return isHighlighted;
    }

    public void setIsHighlighted(boolean isHighlighted) {
        this.isHighlighted = isHighlighted;
    }

    public boolean getIsSpam() {
        return isSpam;
    }

    public void setIsSpam(boolean isSpam) {
        this.isSpam = isSpam;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public long getThread() {
        return thread;
    }

    public void setThread(long thread) {
        this.thread = thread;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
