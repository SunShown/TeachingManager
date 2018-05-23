package com.yah.manager.teachingmanage.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.yah.manager.teachingmanage.R;
import com.yah.manager.teachingmanage.Utils.API;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
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
        OkHttpUtils.post().url(API.IP_REGISTER)
                .id(1)
                .addParams(API.REGISTER.username, userName.trim())
                .addParams(API.REGISTER.password, userPwd.trim())
                .addParams(API.REGISTER.type, type + "")
                .build()
                .execute(new MyStringCallback());
    }

    @OnClick(R.id.reg_btn_register)
    public void onViewClicked() {
        register();
    }

    public class MyStringCallback extends StringCallback {

        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(String response, int id) {
            if (id == 1) {
                Utils.toast(RegisterActivity.this, response);
            } else {
                Utils.toast(RegisterActivity.this, "请求失败");
            }
        }
    }
}
