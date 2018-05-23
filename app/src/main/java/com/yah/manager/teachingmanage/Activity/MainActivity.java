package com.yah.manager.teachingmanage.Activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yah.manager.teachingmanage.Fragment.ChatFragment;
import com.yah.manager.teachingmanage.Fragment.CourseFragment;
import com.yah.manager.teachingmanage.Fragment.TestFragment;
import com.yah.manager.teachingmanage.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    ChatFragment chatFragment;
    CourseFragment courseFragment;
    TestFragment testFragment;
    LinearLayout llCourse,llTest,llChat;
    ImageView ivCourse,ivTest,ivChat;
    TextView tvCourse,tvText,tvChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    public void initView(){
        llCourse = findViewById(R.id.ll_course);
        llTest = findViewById(R.id.ll_test);
        llChat = findViewById(R.id.ll_chat);
        ivCourse = findViewById(R.id.iv_course);
        ivTest = findViewById(R.id.iv_test);
        ivChat = findViewById(R.id.iv_chat);
        tvCourse = findViewById(R.id.tv_course);
        tvText = findViewById(R.id.tv_test);
        tvChat = findViewById(R.id.tv_chat);
//        llCourse.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
        llCourse.setOnClickListener(this);
        llChat.setOnClickListener(this);
        llTest.setOnClickListener(this);
        chatFragment = new ChatFragment();
        courseFragment = new CourseFragment();
        testFragment = new TestFragment();
        changeTab(R.id.ll_course);
    }

    public void changeTab(int btnId){
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        switch (btnId){
            case R.id.ll_course://
                transaction.replace(R.id.content,courseFragment);
                break;
            case R.id.ll_test:
                transaction.replace(R.id.content,testFragment);
                break;
            case R.id.ll_chat:
                transaction.replace(R.id.content,chatFragment);
                break;
        }
        transaction.commit();
    }
    public void changeColor(int btnId){
        if (btnId == R.id.ll_course){
            ivCourse.setImageResource(R.drawable.ic_course_press);
            tvCourse.setTextColor(getResources().getColor(R.color.colorTitlebar));
        }else {
            ivCourse.setImageResource(R.drawable.ic_course_normal);
            tvCourse.setTextColor(getResources().getColor(R.color.text_normal));
        }

        if (btnId == R.id.ll_test){
            ivTest.setImageResource(R.drawable.ic_test_press);
            tvText.setTextColor(getResources().getColor(R.color.colorTitlebar));
        }else {
            ivTest.setImageResource(R.drawable.ic_test_normal);
            tvText.setTextColor(getResources().getColor(R.color.text_normal));
        }

        if(btnId == R.id.ll_chat){
            ivChat.setImageResource(R.drawable.ic_chat_press);
            tvChat.setTextColor(getResources().getColor(R.color.colorTitlebar));
        }else {
            ivChat.setImageResource(R.drawable.ic_chat_normal);
            tvChat.setTextColor(getResources().getColor(R.color.text_normal));
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_course:
                changeTab(view.getId());
                changeColor(view.getId());
                break;
            case R.id.ll_test:
                changeTab(view.getId());
                changeColor(view.getId());
                break;
            case R.id.ll_chat:
                changeTab(view.getId());
                changeColor(view.getId());
                break;

        }
    }
}
