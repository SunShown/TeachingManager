package com.yah.manager.teachingmanage.Activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yah.manager.teachingmanage.Adapter.CommentAdapter;
import com.yah.manager.teachingmanage.Bean.Comment;
import com.yah.manager.teachingmanage.Preferences;
import com.yah.manager.teachingmanage.R;
import com.yah.manager.teachingmanage.Utils.API;
import com.yah.manager.teachingmanage.Utils.MyDialogHandler;
import com.yah.manager.teachingmanage.Utils.Utils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class MsgDetailActivity extends AppCompatActivity {
    public static final int ASK_SUCCESS = 0;
    public static final int ASK_FAIFURE = 1;
    public static final int COMMENT_FAIFURE = 2;//评论失败
    public static final int COMMENT_SUCCESS = 3;//评论成功
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.srf_refresh)
    SwipeRefreshLayout srfRefresh;
    @BindView(R.id.tv_no_comment)
    TextView tvNoComment;
    @BindView(R.id.et_comment_content)
    EditText etCommentContent;
    @BindView(R.id.btn_commit)
    Button btnCommit;

    private String msgId;
    private String title;
    private String content;
    private MyHander hander;
    private CommentAdapter commentAdapter;
    private ArrayList<Comment> comments = new ArrayList<>();
    private MyDialogHandler dialogHandler;
    InputMethodManager imm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_detail);
        ButterKnife.bind(this);
        if (getIntent() == null || TextUtils.isEmpty(getIntent().getStringExtra("msgId"))) {
            Utils.toast(this, "消息不存在");
            finish();
        }
        msgId = getIntent().getStringExtra("msgId");
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        tvTitle.setText(title);
        tvContent.setText(content);
        hander = new MyHander(this);
        commentAdapter = new CommentAdapter(this, comments);
        listView.setAdapter(commentAdapter);
        dialogHandler = new MyDialogHandler(MsgDetailActivity.this,"正在评论....");
        srfRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    /**
     * 刷新数据
     */
    public void refresh() {
        srfRefresh.setRefreshing(true);
        OkHttpUtils.post()
                .url(API.IP_COMMENT_LIST)
                .addParams(API.COMMNETS.msgId, msgId)
                .id(1)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (srfRefresh != null) {
                            //取消刷新
                            hander.sendEmptyMessage(ASK_FAIFURE);
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            if (srfRefresh != null) {
                                Gson gson = new Gson();
                                ArrayList<Comment> msgLists = gson.fromJson(response, new TypeToken<ArrayList<Comment>>() {
                                }.getType());
                                Message message = new Message();
                                message.what = ASK_SUCCESS;
                                message.obj = msgLists;
                                hander.sendMessage(message);
                            }
                        }catch (Exception e){
                            hander.sendEmptyMessage(ASK_FAIFURE);
                        }
                    }
                });
    }

    /**
     * 添加评论
     */
    public void commitComment() {
        if (TextUtils.isEmpty(etCommentContent.getText().toString())){
            Utils.toast(getApplicationContext(),"请输入评论内容");
            return;
        }
        hander.sendEmptyMessage(MyDialogHandler.SHOW_LOADING_DIALOG);
        OkHttpUtils.post()
                .url(API.IP_COMMIT_COMMENT)
                .addParams(API.COMMIT_COMMENT.msgId, msgId)
                .addParams(API.COMMIT_COMMENT.userId, Preferences.getInstance(getApplicationContext()).getUserMsg().getId() + "")
                .addParams(API.COMMIT_COMMENT.content, etCommentContent.getText().toString())
                .id(1)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        hander.sendEmptyMessage(COMMENT_FAIFURE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (!isFinishing()) {
                            if(!TextUtils.isEmpty(response) && response.equals("success")){
                                hander.sendEmptyMessage(COMMENT_SUCCESS);
                            }else {
                                hander.sendEmptyMessage(COMMENT_FAIFURE);
                            }
                        }
                    }
                });
    }

    private void refreshSuccess(ArrayList<Comment> commentServe) {
        if (commentServe == null || commentServe.size() == 0) {
            if (comments.size() == 0) {

                listView.setVisibility(View.GONE);
                tvNoComment.setVisibility(View.VISIBLE);
                Utils.toast(getApplicationContext(), "暂无评论");
            } else {
                Utils.toast(getApplicationContext(), "暂无最新评论");
            }
            return;
        }
        listView.setVisibility(View.VISIBLE);
        tvNoComment.setVisibility(View.GONE);
        comments.clear();
        comments.addAll(commentServe);
        commentAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.btn_commit,R.id.tb_iv_left})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.btn_commit:
                commitComment();
                break;
            case R.id.tb_iv_left:
                finish();
                break;
        }
    }

    class MyHander extends Handler {
        WeakReference<MsgDetailActivity> msgDetailActivities;

        public MyHander(MsgDetailActivity chatFragments) {
            this.msgDetailActivities = new WeakReference<>(chatFragments);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MsgDetailActivity msgDetailActivity = msgDetailActivities.get();
            switch (msg.what) {
                case ASK_FAIFURE:
                    //请求失败
                    msgDetailActivity.srfRefresh.setRefreshing(false);
                    Utils.toast(getApplicationContext(), "请求失败，请重试");
                    break;
                case ASK_SUCCESS:
                    //请求成功
                    msgDetailActivity.srfRefresh.setRefreshing(false);
                    ArrayList<Comment> msgLists = (ArrayList<Comment>) msg.obj;
                    msgDetailActivity.refreshSuccess(msgLists);
                    break;
                case COMMENT_FAIFURE://评论失败
                    hander.sendEmptyMessage(MyDialogHandler.DISMISS_LOADING_DIALOG);
                    Utils.toast(getApplicationContext(), "评论失败，请稍后重试");
                    break;
                case COMMENT_SUCCESS://评论成功
                    hander.sendEmptyMessage(MyDialogHandler.DISMISS_LOADING_DIALOG);
                    Utils.toast(getApplicationContext(),"评论成功");
                    etCommentContent.setText("");
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    msgDetailActivity.refresh();
                    break;
            }
        }
    }
}
