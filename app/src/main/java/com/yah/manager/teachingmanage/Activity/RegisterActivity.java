package com.yah.manager.teachingmanage.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.yah.manager.teachingmanage.Bean.User;
import com.yah.manager.teachingmanage.Preferences;
import com.yah.manager.teachingmanage.R;
import com.yah.manager.teachingmanage.Utils.API;
import com.yah.manager.teachingmanage.Utils.MyDialogHandler;
import com.yah.manager.teachingmanage.Utils.Utils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.reg_et_username)
    EditText regEtUsername;
    @BindView(R.id.reg_et_password)
    EditText regEtPassword;
    @BindView(R.id.reg_et_repassword)
    EditText regEtRepassword;
    @BindView(R.id.reg_rd_student)
    RadioButton regRdStudent;
    @BindView(R.id.reg_rd_teacher)
    RadioButton regRdTeacher;
    @BindView(R.id.radio_status)
    RadioGroup radioStatus;
    @BindView(R.id.reg_btn_register)
    Button regBtnRegister;
    MyDialogHandler dialogHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        dialogHandler = new MyDialogHandler(RegisterActivity.this,"正在注册...");
    }

    /**
     * 注册逻辑
     */
    public void register() {
        String userName = regEtUsername.getText().toString();//得到输入的用户名
        String userPwd = regEtPassword.getText().toString();//得到输入的密码
        String reUserPwd = regEtRepassword.getText().toString();//得到重新输入的密码
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(userPwd) || TextUtils.isEmpty(reUserPwd)) {
            //三者均不能为空置
            Utils.toast(RegisterActivity.this, "请输入完整信息");
            return;
        }
        if (!userPwd.equals(reUserPwd)) {
            //如果两次密码输入的不相等
            Utils.toast(RegisterActivity.this, "两次密码输入不一致");
            return;
        }
        int type = 0;//0为学生，1为老师
        if (radioStatus.getCheckedRadioButtonId() == R.id.reg_rd_teacher) {
            type = 1;//如果为老师，则状态改为1；
        }
        Log.e("register", "register:"+API.IP_REGISTER);
        dialogHandler.sendEmptyMessage(MyDialogHandler.SHOW_LOADING_DIALOG);
        OkHttpUtils.post().url(API.IP_REGISTER)
                .id(1)
                .addParams(API.REGISTER.username, userName.trim())
                .addParams(API.REGISTER.password, userPwd.trim())
                .addParams(API.REGISTER.type, type + "")
                .build()
                .execute(new MyStringCallback());
    }

    @OnClick({R.id.reg_btn_register,R.id.tb_iv_left})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.reg_btn_register:
                register();
                break;
            case R.id.tb_iv_left:
                finish();
                break;
        }

    }

    public class MyStringCallback extends StringCallback {

        @Override
        public void onError(Call call, Exception e, int id) {
            dialogHandler.sendEmptyMessage(MyDialogHandler.DISMISS_LOADING_DIALOG);
            Utils.toast(RegisterActivity.this, "请求失败");
        }

        @Override
        public void onResponse(String response, int id) {
            dialogHandler.sendEmptyMessage(MyDialogHandler.DISMISS_LOADING_DIALOG);
            if (id == 1) {
                Gson gson = new Gson();
                try {
                    User user = gson.fromJson(response, User.class);
                    if (user.type == 0){
                        //学生
                        Preferences.getInstance(getApplicationContext()).setTeacher(false);
                    }else {
                        Preferences.getInstance(getApplicationContext()).setTeacher(true);
                    }
                    Utils.toast(getApplicationContext(),"注册成功");
                    Preferences.getInstance(getApplicationContext()).saveUserMsg(response);
                    Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }catch (Exception e){
                    Utils.toast(getApplicationContext(),response);
                }


            } else {
                Utils.toast(RegisterActivity.this, "请求失败");
            }
        }
    }
}
