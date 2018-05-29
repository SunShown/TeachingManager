package com.yah.manager.teachingmanage.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yah.manager.teachingmanage.Bean.CourseInfo;
import com.yah.manager.teachingmanage.Bean.GlobalInfo;
import com.yah.manager.teachingmanage.Preferences;
import com.yah.manager.teachingmanage.R;
import com.yah.manager.teachingmanage.Utils.API;
import com.yah.manager.teachingmanage.Utils.MyDialogHandler;
import com.yah.manager.teachingmanage.Utils.Utils;
import com.yah.manager.teachingmanage.db.dao.GlobalInfoDao;
import com.yah.manager.teachingmanage.db.dao.UserCourseDao;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;


/**
 * Created by miao on 2017/3/21.
 */

public class CourseDetailInfoActivity extends FragmentActivity {

    /**
     *  静态成员变量
     */
    private static Context context;

    /**
     * UI相关成员变量
     */
    private ProgressDialog progressDialog;

    /**
     * View相关成员变量
     */
    protected View backView;
    protected TextView titleView;

    protected TextView courseNameView;//课程名
    protected TextView courseTeacherView;//任课老师
    protected TextView courseTimeView;//上课时间
    protected TextView courseWeekView;//上课周数
    protected TextView courseSpaceView;//上课地点

    protected Button deleteButton;//删除课程

    //Dao成员变量
    GlobalInfoDao gInfoDao;
    UserCourseDao uCourseDao;

    //数据模型变量
    GlobalInfo gInfo;//需要isFirstUse和activeUserUid

    //数据存储变量
    CourseInfo cInfo;

    //临时变量
    private int uid,cid;

    private MyDialogHandler handleDeleteCourse;
    /**
     * Activity回调函数
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // 继承父类方法，绑定View
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        handleDeleteCourse = new MyDialogHandler(this,"正在请求...");
        // 初始化context
        context = getApplicationContext();

        // 初始化Dao成员变量
        gInfoDao = new GlobalInfoDao(context);
        uCourseDao = new UserCourseDao(context);

        // 初始化数据模型变量
        gInfo = gInfoDao.query();
        if (gInfo == null){
            gInfo = new GlobalInfo();
        }
        uid = gInfo.getActiveUserUid();
        cInfo = new CourseInfo();
        cInfo = (CourseInfo) getIntent().getSerializableExtra("courseInfo");//从Intent中取回Serializable的course_info内容
        cid = cInfo.getCourseId();

        // 初始化临时变量

        // 自定义函数
        initView();//初始化CourseActivity界面
        initListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void initView() {
        backView = findViewById(R.id.Btn_Course_Back);
        //设置标题栏
        titleView = (TextView) findViewById(R.id.Text_Course_Title);
        titleView.setTextSize(20);
        titleView.setPadding(15, 2, 15, 2);

        courseNameView = (TextView) findViewById(R.id.Detail_Course_Name);
        courseTeacherView = (TextView) findViewById(R.id.Detail_Course_Teacher);
        courseTimeView = (TextView) findViewById(R.id.Detail_Course_Time);
        courseWeekView = (TextView) findViewById(R.id.Detail_Course_Week);
        courseSpaceView = (TextView) findViewById(R.id.Detail_Course_Space);

        courseNameView.setText("课程名：" + cInfo.getCoursename());
        courseTeacherView.setText("任课教师：" + cInfo.getTeacherName());
        courseTimeView.setText("时间：星期" + Utils.getDayStr(cInfo.getDay()) + " 第" + cInfo.getLessonfrom() + "节 - 第" + cInfo.getLessonto() + "节");
        courseWeekView.setText("周数：第" +  cInfo.getWeekfrom() + "周 - 第" + cInfo.getWeekto() + "周");
        courseSpaceView.setText("地点：" + cInfo.getPlace());

        deleteButton = (Button) findViewById(R.id.Btn_Delete);
        if (!Preferences.getInstance(getApplicationContext()).isTeacher()){
            deleteButton.setVisibility(View.GONE);
        }else {
            deleteButton.setVisibility(View.VISIBLE);
        }
    }

    private void initListener() {
        backView.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                finish();
            }
        });

        deleteButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                delete();
            }
        });

    }


    private void deleteCourse(){
//        // 显示状态对话框
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage(getResources().getString(R.string.loading_tip));
//        progressDialog.setCancelable(true);
//        progressDialog.show();
//
//        handleDeleteCourse(uid, cid);
    }

    public void delete(){
        handleDeleteCourse.sendEmptyMessage(MyDialogHandler.SHOW_LOADING_DIALOG);
        OkHttpUtils.post().url(API.IP_DELETE_COURSE)
                .addParams(API.DELETE_COURSE.courseId,cid+"")
                .id(1)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        handleDeleteCourse.sendEmptyMessage(MyDialogHandler.DISMISS_LOADING_DIALOG);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        handleDeleteCourse.sendEmptyMessage(MyDialogHandler.DISMISS_LOADING_DIALOG);
                        if ("success".equals(response)){
                            Utils.toast(getApplicationContext(),"删除成功，请刷新");
                            finish();
                        }else {
                            Utils.toast(getApplicationContext(),"删除失败");
                        }
                    }
                });
    }





}
