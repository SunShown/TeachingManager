package com.yah.manager.teachingmanage.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yah.manager.teachingmanage.Activity.EditWorkActivity;
import com.yah.manager.teachingmanage.Activity.TradeCircleActivity;
import com.yah.manager.teachingmanage.Activity.WorkDetailActivity;
import com.yah.manager.teachingmanage.Bean.WorkList;
import com.yah.manager.teachingmanage.Preferences;
import com.yah.manager.teachingmanage.R;
import com.yah.manager.teachingmanage.Utils.API;
import com.yah.manager.teachingmanage.Utils.Utils;
import com.yah.manager.teachingmanage.widgets.MyListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;

/**
 * Created by Administrator on 2018/4/5.
 */

public class TestFragment extends Fragment {
    Preferences preferences;
    private static final int ASK_SUCCESS = 0;
    private static final int ASK_FAIFURE = 1;
    Unbinder unbinder;
    @BindView(R.id.listView)
    MyListView listView;
    @BindView(R.id.srf_refresh)
    SwipeRefreshLayout srfRefresh;
    TestAdapter testAdapter;
    @BindView(R.id.fab_prac)
    FloatingActionButton fabPrac;
    private MyHander hander;
    private ArrayList<WorkList> workLists = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, null);
        preferences = Preferences.getInstance(getActivity().getApplicationContext());
        unbinder = ButterKnife.bind(this, view);
        hander = new MyHander(this);
        testAdapter = new TestAdapter();
        listView.setAdapter(testAdapter);
        srfRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        refresh();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (preferences.isTeacher()) {
            //如果是老师，就显示发布作业按钮
            fabPrac.setVisibility(View.VISIBLE);
        } else {
            fabPrac.setVisibility(View.GONE);
        }
    }

    public void refreshSuccess(ArrayList<WorkList> workListsServe) {
        if (srfRefresh== null ){
            return;
        }
        srfRefresh.setRefreshing(false);
        if (workListsServe == null || workListsServe.size() == 0) {
            Utils.toast(getActivity().getApplicationContext(), "暂无数据");
            return;
        }
        Utils.toast(getActivity().getApplicationContext(), "刷新成功");
        workLists.clear();
        workLists.addAll(workListsServe);
        testAdapter.notifyDataSetChanged();
    }

    public void refresh() {
        srfRefresh.setRefreshing(true);
        OkHttpUtils.get()
                .url(API.IP_GET_WORK_LIST)
                .id(1)
                .addParams(API.GET_TEST_LIST.userId, Preferences.getInstance(getActivity().getApplicationContext()).getUserMsg().getId() + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (getActivity() != null && !getActivity().isFinishing()) {

                            hander.sendEmptyMessage(ASK_FAIFURE);
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (getActivity() != null && !getActivity().isFinishing()) {
                            Gson gson = new Gson();
                            try {
                                ArrayList<WorkList> msgLists = gson.fromJson(response, new TypeToken<ArrayList<WorkList>>() {
                                }.getType());
                                Message message = new Message();
                                message.what = ASK_SUCCESS;
                                message.obj = msgLists;
                                hander.sendMessage(message);
                            }catch (Exception e){
                                hander.sendEmptyMessage(ASK_FAIFURE);
                            }

                        }
                    }
                });
    }

    @OnClick(R.id.fab_prac)
    public void onViewClicked() {
        Intent intent = new Intent(getActivity(), EditWorkActivity.class);
        startActivity(intent);
    }

    class MyHander extends Handler {
        WeakReference<TestFragment> chatFragments;

        public MyHander(TestFragment chatFragments) {
            this.chatFragments = new WeakReference<>(chatFragments);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TestFragment chatFragment = chatFragments.get();
            switch (msg.what) {
                case ASK_FAIFURE:
                    //请求失败
                    chatFragment.srfRefresh.setRefreshing(false);
                    Utils.toast(chatFragment.getActivity(), "请求失败，请重试");
                    break;
                case ASK_SUCCESS:
                    //请求成功
                    ArrayList<WorkList> msgLists = (ArrayList<WorkList>) msg.obj;
                    chatFragment.refreshSuccess(msgLists);
                    break;

            }
        }
    }

    class TestAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return workLists.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_work_list, null);
                viewHolder.title = convertView.findViewById(R.id.tv_title);
                viewHolder.username = convertView.findViewById(R.id.tv_teacher_name);
                viewHolder.time = convertView.findViewById(R.id.tv_time);
                viewHolder.parent = convertView.findViewById(R.id.ll_parent);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.title.setText(workLists.get(position).workTitle);
            viewHolder.time.setText(workLists.get(position).time);
            viewHolder.username.setText(workLists.get(position).user.username);
            viewHolder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), WorkDetailActivity.class);
                    intent.putExtra("workId", workLists.get(position).workId);
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }

    private class ViewHolder {
        public TextView title;
        public TextView username;
        public TextView time;
        public CardView parent;
    }
}
