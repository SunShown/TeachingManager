package com.yah.manager.teachingmanage.Bean;

import java.util.List;

public class WorkItem {
    public int position;//当前第几道
    public String workTitle;//题目内容
    public List<String> options;//选项
    public int rightPosition ;//正确选项
    public boolean isRight;//用户选的是否正确
    public boolean isChoice;//用户是否已选
    public int choicePostion;//用户选择选择的
}
