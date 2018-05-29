package com.yah.manager.teachingmanage.Bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/4/5.
 */

public class User implements Serializable {
    public int userId;
    public String username;
    public String userPwd;
    public int type;//0代表学生 1代表老师

    public int getId() {
        return userId;
    }

    public String getUserName() {
        return username;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public int getType() {
        return type;
    }
}
