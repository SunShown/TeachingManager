package com.yah.manager.teachingmanage.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterViewFlipper;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yah.manager.teachingmanage.Bean.QWork;
import com.yah.manager.teachingmanage.Bean.User;
import com.yah.manager.teachingmanage.Bean.WorkDetail;
import com.yah.manager.teachingmanage.Bean.WorkItem;
import com.yah.manager.teachingmanage.Preferences;
import com.yah.manager.teachingmanage.R;
import com.yah.manager.teachingmanage.Utils.API;
import com.yah.manager.teachingmanage.Utils.MyDialogHandler;
import com.yah.manager.teachingmanage.Utils.Utils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class WorkDetailActivity extends AppCompatActivity {
    public static final int ASK_SUCCESS = 0;
    public static final int ASK_FAIFURE = 1;
    public static final int COMMIT_WORK_SUCCESS = 2;
    public static final int COMMIT_WORK_FAIFURE = 3;
    @BindView(R.id.img_pre)
    ImageView imgPre;
    @BindView(R.id.tv_counts)
    TextView tvCounts;
    @BindView(R.id.img_next)
    ImageView imgNext;
    @BindView(R.id.vf)
    AdapterViewFlipper vf;
    private int workId;
    private MyDialogHandler dialogHandler;
    private MyDialogHandler commitDialog;
    private MyHander hander;
    private List<WorkItem> workItems = new ArrayList<>();
    private MyAdapter adapter;
    private int curPostion ;//当前达到第几题了
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_detail);
        ButterKnife.bind(this);
        if (getIntent() == null || getIntent().getIntExtra("workId",0) == 0){
            Utils.toast(getApplicationContext(),"未查询正确的习题");
            finish();
        }
        workId = getIntent().getIntExtra("workId",0);
        dialogHandler = new MyDialogHandler(this,"加载中......");
        commitDialog = new MyDialogHandler(this,"正在提交......");

        hander = new MyHander(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        dialogHandler.sendEmptyMessage(MyDialogHandler.SHOW_LOADING_DIALOG);
        refresh();
    }

    public void refreshSuccess(final WorkDetail workDetails){
        if (workDetails == null || workDetails.workList == null || workDetails.workList.size() == 0){
            Utils.toast(getApplicationContext(),"暂无数据，请选择其他题库");
        }
        workItems = workDetails.workList;
        tvCounts.setText("1/"+workItems.size());
        adapter = new MyAdapter();
        vf.setAdapter(adapter);
        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!workItems.get(curPostion-1).isChoice){
                    Utils.toast(getApplicationContext(),"选项不能为空");
                }else {
                    vf.showNext();
                    tvCounts.setText(curPostion+"/"+workItems.size());
                }
            }
        });
        imgPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vf.showPrevious();
                tvCounts.setText(curPostion+"/"+workItems.size());
            }
        });
    }

    public void refresh(){
        OkHttpUtils.post()
                .url(API.IP_GET_WORK_DETAIL)
                .addParams(API.GET_WORK_DETAIL.workId, workId+"")
                .addParams(API.GET_WORK_DETAIL.userId, Preferences.getInstance(getApplicationContext()).getUserMsg().id+"")
                .id(1)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (!isFinishing()) {
                            //取消刷新
                            hander.sendEmptyMessage(ASK_FAIFURE);
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try{
                            if (!isFinishing()) {
                                Gson gson = new Gson();
                                List<WorkItem> workItems = gson.fromJson(response, new TypeToken<ArrayList<WorkItem>>() {
                                }.getType());
                                WorkDetail workDetail = new WorkDetail();
                                workDetail.workList = workItems;
                                Message message = new Message();
                                message.what = ASK_SUCCESS;
                                message.obj = workDetail;
                                hander.sendMessage(message);
                            }
                        }catch (Exception e){
                            hander.sendEmptyMessage(ASK_FAIFURE);
                        }
                    }
                });
    }

    /**
     * 提交作业
     */
    public void commit(){
        commitDialog.sendEmptyMessage(MyDialogHandler.SHOW_LOADING_DIALOG);
        int errorCunt = 0;
        List<QWork> qWorks = new ArrayList<>();
        int userId = Preferences.getInstance(getApplicationContext()).getUserMsg().id;
        for (WorkItem workItem : workItems) {
            QWork qwork = new QWork();
            qwork.isRight = workItem.isRight?1:0;
            qwork.questionId = workItem.questionId;
            qwork.select = workItem.answer;
            qwork.userId = userId;
            qWorks.add(qwork);
            if (!workItem.isRight){
                errorCunt ++;
            }
        }
        Gson gson = new Gson();
        String workDetail = gson.toJson(qWorks);
        OkHttpUtils.post()
                .url(API.IP_COMMIT_WORK)
                .addParams(API.COMMIT_WORK.workDetail, workDetail)
                .id(1)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (!isFinishing()) {
                            //取消刷新
                            hander.sendEmptyMessage(COMMIT_WORK_FAIFURE);
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (!isFinishing()) {
                            if (response != "0"){
                                try {
                                    hander.sendEmptyMessage( COMMIT_WORK_SUCCESS);
                                }catch (Exception e){
                                    hander.sendEmptyMessage(COMMIT_WORK_FAIFURE);
                                }
                            }else {
                                hander.sendEmptyMessage(COMMIT_WORK_FAIFURE);
                            }
                        }
                    }
                });
    }
    class MyHander extends Handler {
        WeakReference<WorkDetailActivity> msgDetailActivities;

        public MyHander(WorkDetailActivity chatFragments) {
            this.msgDetailActivities = new WeakReference<>(chatFragments);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            WorkDetailActivity msgDetailActivity = msgDetailActivities.get();
            switch (msg.what) {
                case ASK_FAIFURE:
                    //请求失败
                    msgDetailActivity.dialogHandler.sendEmptyMessage(MyDialogHandler.DISMISS_LOADING_DIALOG);
                    Utils.toast(getApplicationContext(), "请求失败，请重试");
                    break;
                case ASK_SUCCESS:
                    //请求成功
                    msgDetailActivity.dialogHandler.sendEmptyMessage(MyDialogHandler.DISMISS_LOADING_DIALOG);
                    WorkDetail workDetail = (WorkDetail) msg.obj;
                    msgDetailActivity.refreshSuccess(workDetail);
                    break;
                case COMMIT_WORK_FAIFURE:
                    msgDetailActivity.commitDialog.sendEmptyMessage(MyDialogHandler.DISMISS_LOADING_DIALOG);
                    Utils.toast(getApplicationContext(),"提交失败");
                    break;
                case COMMIT_WORK_SUCCESS:
                    msgDetailActivity.commitDialog.sendEmptyMessage(MyDialogHandler.DISMISS_LOADING_DIALOG);
                    Utils.toast(getApplicationContext(),"提交成功");
                    finish();//关闭页面
                    break;
            }
        }
    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return workItems.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            curPostion = position +1;
            if (position == 0){
                imgPre.setVisibility(View.GONE);
            }else {
                imgPre.setVisibility(View.VISIBLE);
            }

            if (position == workItems.size() -1){
                imgNext.setVisibility(View.GONE);
            }else {
                imgNext.setVisibility(View.VISIBLE);
            }
            ViewHolder holder = null ;
            if (view == null){
                holder = new ViewHolder();
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.queitem,null);
                holder.tv_que1 = view.findViewById(R.id.tv_que1);
                holder.rg_choice = view.findViewById(R.id.rg_choice);
                holder.cb_choice1 = view.findViewById(R.id.cb_choice1);
                holder.cb_choice2 = view.findViewById(R.id.cb_choice2);
                holder.cb_choice3 = view.findViewById(R.id.cb_choice3);
                holder.cb_choice4 = view.findViewById(R.id.cb_choice4);
                holder.tv_answer1 = view.findViewById(R.id.tv_answer1);
                holder.tv_look_right_answer = view.findViewById(R.id.tv_look_right_answer);
                holder.tv_you = view.findViewById(R.id.tv_you);
                holder.commit = view.findViewById(R.id.btn_commit);
                view.setTag(holder);
            }else {
                holder = (ViewHolder) view.getTag();
            }
            if (position == workItems.size() - 1){
                //最后一页
                holder.commit.setVisibility(View.VISIBLE);
            }else {
                holder.commit.setVisibility(View.GONE);
            }
            holder.tv_que1.setText(workItems.get(position).title);
            if (workItems.get(position).isChoice){
                switch (workItems.get(position).choicePostion){
                    case 1:
                        holder.rg_choice.check(R.id.cb_choice1);
                        break;
                    case 2:
                        holder.rg_choice.check(R.id.cb_choice2);
                        break;
                    case 3:
                        holder.rg_choice.check(R.id.cb_choice3);
                        break;
                    case 4:
                        holder.rg_choice.check(R.id.cb_choice4);
                        break;
                }

            }
            //设置作业
            holder.cb_choice1.setText("1:"+workItems.get(position).qOne);
            holder.cb_choice2.setText("2:"+workItems.get(position).qTwo);
            holder.cb_choice3.setText("3:"+workItems.get(position).qThree);
            holder.cb_choice4.setText("4:"+workItems.get(position).qFour);
            holder.commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commit();
                }
            });
            final ViewHolder finalHolder = holder;
            holder.rg_choice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    int choicePosition = 0;
                    switch (checkedId){
                        case R.id.cb_choice1:
                            finalHolder.tv_you.setText("你选择的是：1");
                            choicePosition = 1;
                            break;
                        case R.id.cb_choice2:
                            finalHolder.tv_you.setText("你选择的是：2");
                            choicePosition = 2;
                            break;
                        case R.id.cb_choice3:
                            finalHolder.tv_you.setText("你选择的是：3");
                            choicePosition = 3;
                            break;
                        case R.id.cb_choice4:
                            finalHolder.tv_you.setText("你选择的是：4");
                            choicePosition = 4;
                            break;
                    }
                    if (choicePosition == workItems.get(position).answer){
                        workItems.get(position).isRight = true;
                    }else {
                        workItems.get(position).isRight = false;
                    }
                    workItems.get(position).isChoice = true;
                    workItems.get(position).choicePostion = choicePosition;
                }
            });
            final ViewHolder finalHolder1 = holder;
            holder.tv_look_right_answer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalHolder1.tv_answer1.setVisibility(View.VISIBLE);
                    if (workItems.get(position).isRight){
                        finalHolder1.tv_answer1.setText("恭喜您答对啦！正确选项为"+workItems.get(position).answer);
                        finalHolder1.tv_answer1.setTextColor(Color.RED);
                    }else {
                        finalHolder1.tv_answer1.setText("很遗憾！你未能答对，正确选项为"+workItems.get(position).answer);
                    }
                }
            });
            return view;
        }

    }
    private class ViewHolder{
        public TextView tv_que1,tv_you,tv_look_right_answer,tv_answer1;
        public RadioGroup rg_choice;
        public RadioButton cb_choice1,cb_choice2,cb_choice3,cb_choice4;
        public Button commit;
    }
}
