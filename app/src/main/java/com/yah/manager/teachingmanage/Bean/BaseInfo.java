package com.yah.manager.teachingmanage.Bean;

import java.io.Serializable;

// BaseInfo用于记录整体信息
public class BaseInfo implements Serializable {

    private static final long serialVersionUID = 2074656067805712769L;

    public int weekfrom;	// 起始周
    public int weekto;		// 结束周
    public int weektype;	// 周类型：1普通；2单周；3双周
    public int day;			// 星期几上课
    public int lessonfrom;	// 开始节次
    public int lessonto;	// 结束节次
    public String place;	//地点
	public int userId;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getWeekfrom() {
		return weekfrom;
	}
	public void setWeekfrom(int weekfrom) {
		this.weekfrom = weekfrom;
	}
	public int getWeekto() {
		return weekto;
	}
	public void setWeekto(int weekto) {
		this.weekto = weekto;
	}
	public int getWeektype() {
		return weektype;
	}
	public void setWeektype(int weektype) {
		this.weektype = weektype;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getLessonfrom() {
		return lessonfrom;
	}
	public void setLessonfrom(int lessonfrom) {
		this.lessonfrom = lessonfrom;
	}
	public int getLessonto() {
		return lessonto;
	}
	public void setLessonto(int lessonto) {
		this.lessonto = lessonto;
	}
    public String getPlace() {
        return place;
    }
    public void setPlace(String place) {
        this.place = place;
    }


    //仅测试使用
    public  BaseInfo() {
    }

    public BaseInfo (int weekfrom,int weekto, int weektype,int day,int lessonfrom,int lessonto,
                       String place){
        this.weekfrom = weekfrom;
        this.weekto = weekto;
        this.weektype = weektype;
        this.day = day;
        this.lessonfrom = lessonfrom;
        this.lessonto = lessonto;
        this.place=place;
    }

}