package com.yah.manager.teachingmanage.Bean;

public class Comment {
    private int comentId;
    private String userName;
    private String time;

    private int commentId;

    private int userId;

    private int newsId;

    private String username;

    private String replyUser;

    private String comment;

    private String commentTime;
    public int getComentId() {
        return comentId;
    }

    public void setComentId(int comentId) {
        this.comentId = comentId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return comment;
    }


    public String getTime() {
        return commentTime;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
