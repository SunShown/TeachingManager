package com.yah.manager.teachingmanage.Bean;

public class Comment {
    private int comentId;
    private String userName;
    private String content;
    private String time;

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
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
