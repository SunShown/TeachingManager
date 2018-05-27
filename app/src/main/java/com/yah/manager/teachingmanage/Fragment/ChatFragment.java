package com.yah.manager.teachingmanage.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yah.manager.teachingmanage.Activity.TradeCircleActivity;
import com.yah.manager.teachingmanage.Adapter.MsgListAdapter;
import com.yah.manager.teachingmanage.Bean.MsgList;
import com.yah.manager.teachingmanage.R;
import com.yah.manager.teachingmanage.Utils.API;
import com.yah.manager.teachingmanage.Utils.Utils;
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

public class ChatFragment extends Fragment {
    public static final int ASK_SUCCESS = 0;
    public static final int ASK_FAIFURE = 1;
    Unbinder unbinder;
    @BindView(R.id.srf_refresh)
    SwipeRefreshLayout srfRefresh;
    @BindView(R.id.listView)
    ListView listView;

    MyHander hander;
    MsgListAdapter adapter;
    ArrayList<MsgList> msgList = new ArrayList<>();
    @BindView(R.id.fab_prac)
    FloatingActionButton fabPrac;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, null);
        unbinder = ButterKnife.bind(this, view);
        initView();
        if (srfRefresh != null) {
            srfRefresh.setRefreshing(true);
        }
        refresh();
        return view;
    }


    public void initView() {
        srfRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        hander = new MyHander(this);
        adapter = new MsgListAdapter(getActivity(), msgList);
        listView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 刷新列表
     */
    public void refresh() {
        OkHttpUtils.post()
                .url(API.IP_MSG_LIST)
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
                        if (srfRefresh != null) {
                            Gson gson = new Gson();
                            ArrayList<MsgList> msgLists = gson.fromJson(response, new TypeToken<ArrayList<MsgList>>() {
                            }.getType());
                            Message message = new Message();
                            message.what = ASK_SUCCESS;
                            message.obj = msgLists;
                            hander.sendMessage(message);
                        }
                    }
                });
    }

    public void refreshSuccess(ArrayList<MsgList> msgLists) {

        srfRefresh.setRefreshing(false);
        if (msgLists == null || msgLists.size() == 0) {
            Utils.toast(getActivity(), "暂无新数据");
            return;
        }
        msgList.clear();//清除所有列表
        msgList.addAll(msgLists);//重新设置新数据
        adapter.notifyDataSetChanged();//更新UI
    }

    @OnClick(R.id.fab_prac)
    public void onViewClicked() {
        Intent intent = new Intent(getActivity(), TradeCircleActivity.class);
        startActivity(intent);
    }

    class MyHander extends Handler {
        WeakReference<ChatFragment> chatFragments;

        public MyHander(ChatFragment chatFragments) {
            this.chatFragments = new WeakReference<>(chatFragments);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ChatFragment chatFragment = chatFragments.get();
            switch (msg.what) {
                case ASK_FAIFURE:
                    //请求失败
                    chatFragment.srfRefresh.setRefreshing(false);
                    Utils.toast(chatFragment.getActivity(), "请求失败，请重试");
                    break;
                case ASK_SUCCESS:
                    //请求成功
                    chatFragment.srfRefresh.setRefreshing(false);
                    ArrayList<MsgList> msgLists = (ArrayList<MsgList>) msg.obj;
                    chatFragment.refreshSuccess(msgLists);
                    break;

            }
        }
    }
}
