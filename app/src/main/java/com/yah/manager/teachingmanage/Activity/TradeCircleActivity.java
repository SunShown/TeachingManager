package com.yah.manager.teachingmanage.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yah.manager.teachingmanage.Preferences;
import com.yah.manager.teachingmanage.R;
import com.yah.manager.teachingmanage.Utils.API;
import com.yah.manager.teachingmanage.Utils.MyDialogHandler;
import com.yah.manager.teachingmanage.Utils.Utils;
import com.yah.manager.teachingmanage.widgets.TitleBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class TradeCircleActivity extends AppCompatActivity {
    private static final int ASK_SUCCESS = 0;
    private static final int ASK_FAIFURE = 1;
    @BindView(R.id.titlebar)
    TitleBar titlebar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.tb_tv_right)
    TextView commit;
    @BindView(R.id.tb_iv_left)
    ImageView ivBack;
    private MyDialogHandler dialogHandler;
    private MyHander hander;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_circle);
        ButterKnife.bind(this);
        hander = new MyHander(this);
        dialogHandler = new MyDialogHandler(this,"正在提交......");
    }

    @OnClick({R.id.tb_iv_left,R.id.tb_tv_right})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.tb_iv_left:
                finish();
                break;
            case R.id.tb_tv_right:
                commit();
                break;
        }
    }

    /**
     * 提价评论
     */
    public void commit(){
        if (TextUtils.isEmpty(etTitle.getText().toString())){
            Utils.toast(getApplicationContext(),"请输入标题");
            return;
        }
        if (TextUtils.isEmpty(etContent.getText().toString())){
            Utils.toast(getApplicationContext(),"请输入内容");
            return;
        }
        dialogHandler.sendEmptyMessage(MyDialogHandler.SHOW_LOADING_DIALOG);
        OkHttpUtils.get()
                .url(API.IP_COMMIT_POSTS)
                .addParams(API.SEND_CIRCLE.title,etTitle.getText().toString())
                .addParams(API.SEND_CIRCLE.content,etContent.getText().toString())
                .addParams(API.SEND_CIRCLE.userId, Preferences.getInstance(getApplicationContext()).getUserMsg().getId()+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (!isFinishing()){
                            hander.sendEmptyMessage(ASK_FAIFURE);
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (!isFinishing()){
                            if (response !=null && response.endsWith("success")){
                                hander.sendEmptyMessage(ASK_SUCCESS);
                            }else {
                                hander.sendEmptyMessage(ASK_FAIFURE);
                            }
                        }else {
                            hander.sendEmptyMessage(ASK_FAIFURE);
                        }
                    }
                });
    }
    class MyHander extends Handler {
        WeakReference<TradeCircleActivity> msgDetailActivities;

        public MyHander(TradeCircleActivity chatFragments) {
            this.msgDetailActivities = new WeakReference<>(chatFragments);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TradeCircleActivity msgDetailActivity = msgDetailActivities.get();
            switch (msg.what) {
                case ASK_FAIFURE:
                    //请求失败
                    msgDetailActivity.dialogHandler.sendEmptyMessage(MyDialogHandler.DISMISS_LOADING_DIALOG);
                    Utils.toast(getApplicationContext(), "请求失败，请重试");
                    break;
                case ASK_SUCCESS:
                    //请求成功
                    msgDetailActivity.dialogHandler.sendEmptyMessage(MyDialogHandler.DISMISS_LOADING_DIALOG);
                    Utils.toast(getApplicationContext(),"发表成功");
                    finish();
                    break;
            }
        }
    }
}
