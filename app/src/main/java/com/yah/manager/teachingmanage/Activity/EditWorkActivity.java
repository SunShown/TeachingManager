package com.yah.manager.teachingmanage.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.yah.manager.teachingmanage.Bean.WorkItem;
import com.yah.manager.teachingmanage.R;
import com.yah.manager.teachingmanage.Utils.Utils;
import com.yah.manager.teachingmanage.widgets.MyListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditWorkActivity extends AppCompatActivity {

    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.ll_content)
    LinearLayout listView;
    List<WorkItem> workItems = new ArrayList<>();
    @BindView(R.id.tv_add_item)
    TextView tvAddItem;
    int position =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_work);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_add_item,R.id.tb_tv_right,R.id.tb_iv_left})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.tv_add_item:
                addView();
                break;
            case R.id.tb_tv_right:
                //提交作业
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

        }
    }
    public void addView(){
        LinearLayout view = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_edit_work,null);
        EditText etTitle = (EditText) view.getChildAt(1);
        EditText etQestion1 = (EditText) view.getChildAt(3);
        EditText etQestion2 = (EditText) view.getChildAt(4);
        EditText etQestion3 = (EditText) view.getChildAt(5);
        EditText etQestion4 = (EditText) view.getChildAt(6);
        RadioGroup radioGroup = (RadioGroup) view.getChildAt(8);
        listView.addView(view);
    }
}
