package com.yah.manager.teachingmanage.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.yah.manager.teachingmanage.Bean.CourseInfo;
import com.yah.manager.teachingmanage.Preferences;
import com.yah.manager.teachingmanage.R;
import com.yah.manager.teachingmanage.Utils.API;
import com.yah.manager.teachingmanage.Utils.MyDialogHandler;
import com.yah.manager.teachingmanage.Utils.Utils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class EditCourseActivity extends AppCompatActivity {

    @BindView(R.id.et_course_name)
    EditText etCourseName;
    @BindView(R.id.et_week)
    EditText etWeek;
    @BindView(R.id.et_first_less)
    EditText etFirstLess;
    @BindView(R.id.et_second_less)
    EditText etSecondLess;
    @BindView(R.id.et_first_week)
    EditText etFirstWeek;
    @BindView(R.id.et_second_week)
    EditText etSecondWeek;
    @BindView(R.id.et_place)
    EditText etPlace;
    @BindView(R.id.btn_commit)
    Button btnCommit;
    MyDialogHandler dialogHandler ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);
        ButterKnife.bind(this);
        dialogHandler = new MyDialogHandler(EditCourseActivity.this,"正在添加...");
    }

    @OnClick({R.id.btn_commit,R.id.tb_iv_left})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.btn_commit:
                commit();
                break;
            case R.id.tb_iv_left:
                finish();
                break;
        }
    }

    public void commit(){
        String courseName = etCourseName.getText().toString();//课程名
        String weekday = etWeek.getText().toString();//星期几
        String firstLess = etFirstLess.getText().toString();//form 第几节
        String secondLess = etSecondWeek.getText().toString();//to 第几节
        String firstWeek = etFirstWeek.getText().toString();//form周几
        String secondWeek = etSecondWeek.getText().toString();//to 周几
        String place = etPlace.getText().toString();//地点
        if (TextUtils.isEmpty(courseName) || TextUtils.isEmpty(weekday) || TextUtils.isEmpty(firstLess)
                || TextUtils.isEmpty(secondLess) || TextUtils.isEmpty(firstWeek) || TextUtils.isEmpty(secondWeek)
                || TextUtils.isEmpty(place)){
            Utils.toast(getApplicationContext(),"请将信息填写完整");
            return;
        }
        dialogHandler.sendEmptyMessage(MyDialogHandler.SHOW_LOADING_DIALOG);
        CourseInfo courseInfo = new CourseInfo();
        courseInfo.setCoursename(courseName);
        courseInfo.setDay(Integer.parseInt(weekday));
        courseInfo.setWeekfrom(Integer.parseInt(firstWeek));
        courseInfo.setWeekto(Integer.parseInt(secondWeek));
        courseInfo.setLessonfrom(Integer.parseInt(firstLess));
        courseInfo.setLessonto(Integer.parseInt(secondLess));
        courseInfo.setPlace(place);
        courseInfo.setUserId(Preferences.getInstance(getApplicationContext()).getUserMsg().userId);
        courseInfo.setTeacherName(Preferences.getInstance(getApplicationContext()).getUserMsg().username);
        Gson gson = new Gson();
        String content = gson.toJson(courseInfo);
        OkHttpUtils.post()
                .url(API.IP_COMMIT_COURSE)
                .addParams(API.RELEASE_WORK.content,content)
                .id(1)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (!isFinishing()){
                            dialogHandler.sendEmptyMessage(MyDialogHandler.DISMISS_LOADING_DIALOG);
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (!isFinishing()) {
                            dialogHandler.sendEmptyMessage(MyDialogHandler.DISMISS_LOADING_DIALOG);
                            if(!TextUtils.isEmpty(response) && response.equals("success")){
                                Utils.toast(getApplicationContext(),"添加成功");
                                finish();
                            }else {
                                Utils.toast(getApplicationContext(),"添加失败");
                            }
                        }
                    }
                });
    }
}
