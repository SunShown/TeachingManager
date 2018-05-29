package com.yah.manager.teachingmanage.Bean;

import java.io.Serializable;

// CourseInfo用于记录课程信息
public class CourseInfo extends BaseInfo implements Serializable {

    private static final long serialVersionUID = 2074656067805712769L;
	
	private int courseId;		// 课程标记id
	private String coursename; //课程名
	private String teacherName;		// 教师
	
	public int getCourseId() {
		return courseId;
	}
	public void setCourseId(int cid) {
		this.courseId = cid;
	}
	public String getCoursename() {
		return coursename;
	}
	public void setCoursename(String coursename) {
		this.coursename = coursename;
	}

	public String getTeacherName() {
		return teacherName;
	}

	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}
}