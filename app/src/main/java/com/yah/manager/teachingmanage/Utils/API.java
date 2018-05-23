package com.yah.manager.teachingmanage.Utils;

/**
 * Created by Administrator on 2018/4/5.
 */

public class API {
    private static final String IP ="http://192.168.13.1:8080/";
    public static final String IP_LOGIN = IP + "";
    public static final String IP_REGISTER = IP + "User?method=register";
    public static final String IP_MSG_LIST = IP +"News?method=getNewsList";
    //登录接口
    public interface LOGIN{
        String username ="username";
        String password = "password";
    }
    //注册接口
    public interface REGISTER{
        String username= "username";
        String password = "password";
        String  type = "type";
    }

    /**
     * 获取消息列表
     */
    public interface MSG_LIST{
//        String
    }
}