package com.yah.manager.teachingmanage.Utils;

/**
 * Created by Administrator on 2018/4/5.
 */

public class API {
    private static final String IP ="http://192.168.1.45:8080/";
    public static final String IP_LOGIN = IP + "User?method=login";
    public static final String IP_REGISTER = IP + "User?method=register";
    public static final String IP_MSG_LIST = IP +"News?method=getNewsList";
    public static final String IP_COMMENT_LIST = IP +"News?method=getNewsDetail";
    public static final String IP_COMMIT_COMMENT = IP +"";/*提交评论*/
    public static final String IP_GET_WORK_LIST = IP +"Work?method=getWorkList";//获取所有题目列表
    public static final String IP_GET_WORK_DETAIL = IP +"Work?method=getWorkDetail";//获取某个题目的内容
    public static final String IP_COMMIT_WORK = IP +"Work?method=commitWork";//提交作业
    public static final String IP_COMMIT_POSTS = IP+"";//发布帖子

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

    /**
     * 评论列表
     */
    public interface COMMNETS{
        String msgId = "msgId";
    }

    /**
     * 提交评论
     */
    public interface COMMIT_COMMENT{
        String msgId = "msgId";
        String content = "content";
        String userId = "userId";
    }

    /**
     * 获取所有作业列表
     */
    public interface GET_TEST_LIST{
        String userId = "userId";
    }

    public interface GET_WORK_DETAIL{
        String workId = "workId";
        String userId = "userId";
    }

    public interface COMMIT_WORK{
        String workDetail = "workDetail";
        String workId = "workId";
        String userId= "userId";
        String errorCount = "errorCount";
    }

    /**
     * 发表说说
     */
    public interface SEND_CIRCLE{
        String title = "title";
        String content = "content";
        String userId = "userId";
    }
}
