package com.yah.manager.teachingmanage.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.math.MathUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yah.manager.teachingmanage.Bean.QWork;
import com.yah.manager.teachingmanage.Bean.WorkDetail;
import com.yah.manager.teachingmanage.Bean.WorkItem;
import com.yah.manager.teachingmanage.Preferences;
import com.yah.manager.teachingmanage.R;
import com.yah.manager.teachingmanage.Utils.API;
import com.yah.manager.teachingmanage.Utils.MyDialogHandler;
import com.yah.manager.teachingmanage.Utils.Utils;
import com.yah.manager.teachingmanage.widgets.MyListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class EditWorkActivity extends AppCompatActivity {
    public static final int ASK_SUCCESS = 0;
    public static final int ASK_FAIFURE = 1;
    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.ll_content)
    LinearLayout listView;
    @BindView(R.id.tv_add_item)
    TextView tvAddItem;
    int position =0;
    private MyDialogHandler dialogHandler;
    private MyHander handler;
    WorkDetail workDetail = new WorkDetail();
    List<WorkItem> workItems = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_work);
        ButterKnife.bind(this);
        dialogHandler = new MyDialogHandler(this,"正在发布...");
        handler = new MyHander(this);
    }

    @OnClick({R.id.tv_add_item,R.id.tb_tv_right,R.id.tb_iv_left})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.tv_add_item:
                addView();
                break;
            case R.id.tb_tv_right:
                //提交作业
                commit();
                break;
            case R.id.tb_iv_left:
                finish();
                break;

        }
    }

    /**
     * 提交作业
     */
    public void commit(){
        if (listView.getChildCount() == 0){
            Utils.toast(getApplicationContext(),"请添加题目");
            return;
        }
        for (int i = 0; i < listView.getChildCount(); i++) {
            LinearLayout linearLayout = (LinearLayout) listView.getChildAt(i);
            EditText etTitle = (EditText) linearLayout.getChildAt(1);
            EditText etQestion1 = (EditText) linearLayout.getChildAt(3);
            EditText etQestion2 = (EditText) linearLayout.getChildAt(4);
            EditText etQestion3 = (EditText) linearLayout.getChildAt(5);
            EditText etQestion4 = (EditText) linearLayout.getChildAt(6);
            EditText rightPosition = (EditText) linearLayout.getChildAt(8);
            WorkItem workItem = new WorkItem();
            workItem.title = etTitle.getText().toString();
            workItem.qOne = etQestion1.getText().toString();
            workItem.qTwo = etQestion2.getText().toString();
            workItem.qThree = etQestion3.getText().toString();
            workItem.qFour = etQestion4.getText().toString();
            workItem.answer = Integer.parseInt(rightPosition.getText().toString());
            workItems.add(workItem);
        }
        workDetail.workList = workItems;
        workDetail.workTitle = etTitle.getText().toString();
        workDetail.userId = Preferences.getInstance(getApplicationContext()).getUserMsg().getId();
        Gson gson = new Gson();
        String content = gson.toJson(workDetail);
        OkHttpUtils.post()
                .url(API.IP_RELEASE_WORK)
                .addParams(API.RELEASE_WORK.content,content)
                .id(1)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        handler.sendEmptyMessage(ASK_FAIFURE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (!isFinishing()) {
                            if(!TextUtils.isEmpty(response) && response.equals("success")){
                                handler.sendEmptyMessage(ASK_SUCCESS);
                            }else {
                                handler.sendEmptyMessage(ASK_FAIFURE);
                            }
                        }
                    }
                });
    }
    public void addView(){
        final LinearLayout view = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_edit_work,null);
        EditText etTitle = (EditText) view.getChildAt(1);
        EditText etQestion1 = (EditText) view.getChildAt(3);
        EditText etQestion2 = (EditText) view.getChildAt(4);
        EditText etQestion3 = (EditText) view.getChildAt(5);
        EditText etQestion4 = (EditText) view.getChildAt(6);
        EditText radioGroup = (EditText) view.getChildAt(8);
        TextView delete = (TextView) view.getChildAt(9);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.removeView(view);
            }
        });
        listView.addView(view);
    }

    class MyHander extends Handler {
        WeakReference<EditWorkActivity> msgDetailActivities;

        public MyHander(EditWorkActivity chatFragments) {
            this.msgDetailActivities = new WeakReference<>(chatFragments);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            EditWorkActivity msgDetailActivity = msgDetailActivities.get();
            switch (msg.what) {
                case ASK_FAIFURE:
                    //请求失败
                    workItems.clear();
                    msgDetailActivity.dialogHandler.sendEmptyMessage(MyDialogHandler.DISMISS_LOADING_DIALOG);
                    Utils.toast(getApplicationContext(), "请求失败，请重试");
                    break;
                case ASK_SUCCESS:
                    //请求成功
                    Utils.toast(getApplicationContext(),"发布成功");
                    msgDetailActivity.dialogHandler.sendEmptyMessage(MyDialogHandler.DISMISS_LOADING_DIALOG);
                    finish();
                    break;
            }
        }
    }
}
