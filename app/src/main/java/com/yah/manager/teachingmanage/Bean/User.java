package com.yah.manager.teachingmanage.Bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/4/5.
 */

public class User implements Serializable {
    public int id;
    public String userName;
    public String userPwd;
    public int type;//0代表学生 1代表老师

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public int getType() {
        return type;
    }
}
