package com.yah.manager.teachingmanage.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
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

import org.w3c.dom.Text;

import okhttp3.Call;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    EditText etName,etPwd;
    Button btnLogin,btnRegister;
    private int type ;//判断当前是学生还是老师
    private MyDialogHandler uiFlusHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initListener();
    }
    private void initView(){
        etName = findViewById(R.id.et_username);
        etPwd = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        uiFlusHandler = new MyDialogHandler(this,"登录中...");
    }
    private void initListener(){
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login:
                if (TextUtils.isEmpty(etName.getText().toString()) ||
                        TextUtils.isEmpty(etPwd.getText().toString())){
                    Utils.toast(getApplicationContext(),"用户名或者密码不能为空");
                    return;
                }
                String userName = etName.getText().toString();
                String userPwd = etPwd.getText().toString();
                uiFlusHandler.sendEmptyMessage(MyDialogHandler.SHOW_LOADING_DIALOG);
                OkHttpUtils.post()
                        .url(API.IP_LOGIN)
                        .id(1)
                        .addParams(API.LOGIN.username,userName)
                        .addParams(API.LOGIN.password,userPwd)
                        .build()
                        .execute(new MyStringCallback());

                break;
            case R.id.btn_register:
                Intent  intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }
    public class MyStringCallback extends StringCallback{

        @Override
        public void onError(Call call, Exception e, int id) {
            if (uiFlusHandler != null){
                uiFlusHandler.sendEmptyMessage(MyDialogHandler.DISMISS_LOADING_DIALOG);
                Utils.toast(getApplicationContext(),getResources().getString(R.string.request_error));
            }
        }

        @Override
        public void onResponse(String response, int id) {
            if (uiFlusHandler == null){
                return;
            }
            uiFlusHandler.sendEmptyMessage(MyDialogHandler.DISMISS_LOADING_DIALOG);
            if (TextUtils.isEmpty(response) || response.equals("{'info':'error'}")){
                Utils.toast(getApplicationContext(),"请查看用户名和密码是否正确");
                return;
            }
            if (id == 1){
                try {
                    Gson gson = new Gson();
                    User user =  gson.fromJson(response,User.class);

                    if (user.type == 0){
                        //学生
                        Preferences.getInstance(getApplicationContext()).setTeacher(false);
                    }else {
                        Preferences.getInstance(getApplicationContext()).setTeacher(true);
                    }
                    //登录成功
                    Preferences.getInstance(getApplicationContext()).saveUserMsg(response);//存储用户数据
                    Utils.toast(getApplicationContext(),getResources().getString(R.string.login_success));
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }catch (Exception e){
                    Utils.toast(getApplicationContext(),getResources().getString(R.string.request_error));
                }
            }else {
                Utils.toast(getApplicationContext(),getResources().getString(R.string.request_error));
            }
        }
    }
}
