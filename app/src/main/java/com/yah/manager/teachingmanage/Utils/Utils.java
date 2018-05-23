package com.yah.manager.teachingmanage.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.yah.manager.teachingmanage.Bean.BaseInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Administrator on 2018/4/5.
 */

public class Utils {
    public static void toast(Context context,String content){
        Toast.makeText(context,content,Toast.LENGTH_SHORT).show();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int px = (int) (dpValue * (metrics.densityDpi / 160f));
        return px;
    }

    /**
     * 计算当前教学周
     * @param termBegin 开学的日期
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    static public int getWeeks(String termBegin) {
        try {
            Date currentTime = new Date();

            SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = dFormat.parse(termBegin);

            Calendar calendar = new GregorianCalendar();
            calendar.setFirstDayOfWeek(Calendar.SUNDAY);  		//将星期天作为一个星期的开始

            calendar.setTime(date);
            int weeks2 = calendar.get(Calendar.WEEK_OF_YEAR);	// 开学星期数

            calendar.setTime(currentTime);
            int weeks1 = calendar.get(Calendar.WEEK_OF_YEAR);	// 当前星期数

            if (date.after(currentTime)) {
                return 0;
            }
            else {
                int n = (weeks1-weeks2>0)?(weeks1-weeks2+1):(weeks1-weeks2+53);
                return n;
            }
        } catch (Exception e) {
            return 0;
        }
    }
    /**
     * 计算当前时间是周几
     * @param
     * @return
     */
    static public int getWeek(){
        Calendar cal = Calendar.getInstance();
        int i = cal.get(Calendar.DAY_OF_WEEK);
        if(i==1){
            return 7;
        }
        else{
            return i-1;
        }

    }

    /**
     * 计算cInfoTmp是否在当前周上课
     * @param
     * @return
     */
    static public boolean isCurrWeek(BaseInfo cInfoTmp, int currentWeek) {
        // 全周
        if (cInfoTmp.getWeektype() == 1) {
            if ((cInfoTmp.getWeekfrom() <= currentWeek) && (currentWeek <= cInfoTmp.getWeekto())) {
                return true;
            }
            else {
                return false;
            }
        }
        // 单周
        else if (cInfoTmp.getWeektype() == 2) {
            if (currentWeek%2 == 1) {
                return true;
            }
            else {
                return false;
            }
        }
        // 双周
        else {
            if (currentWeek%2 == 0) {
                return true;
            }
            else {
                return false;
            }
        }
    }


    static public String getDayStr(int day) {//由数字星期得出文字星期
        String dayStr;
        switch (day){
            case 1:
                dayStr = "一";
                break;
            case 2:
                dayStr = "二";
                break;
            case 3:
                dayStr = "三";
                break;
            case 4:
                dayStr = "四";
                break;
            case 5:
                dayStr = "五";
                break;
            case 6:
                dayStr = "六";
                break;
            case 7:
                dayStr = "日";
                break;
            default:
                dayStr = "";
                break;
        }
        return dayStr;
    }


}
