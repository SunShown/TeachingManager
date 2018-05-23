package com.yah.manager.teachingmanage.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yah.manager.teachingmanage.R;
import com.yah.manager.teachingmanage.Utils.Utils;

/**
 * Created by Administrator on 2018/4/15.
 */

public class TitleBar extends RelativeLayout {

    private Context context;
    private float dencity;
    private int height;
    private int space;

    private int bgColor;
    private String title = "";
    private String leftText;
    private String rightText;
    private int leftSrc;
    private int rightSrc;
    private int rightSrc1;
    private int middleLayout;
    private int rightLayout;
    private ImageView.ScaleType imageScaleType = ImageView.ScaleType.CENTER_CROP;
    private int touchBack;
    private int imagePadding;


    public TitleBar(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
        init();
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.setBackgroundColor(getResources().getColor(R.color.colorTitlebar));
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        dencity = metrics.densityDpi / 160f;
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.TitleBar);
//        bgColor = typedArray.getColor(
//                R.styleable.TitleBar_leftDrawable, Color.WHITE);
        title = typedArray.getString(R.styleable.TitleBar_title);
        leftText = typedArray.getString(R.styleable.TitleBar_leftText);
        rightText = typedArray.getString(R.styleable.TitleBar_rightText);
        leftSrc = typedArray.getResourceId(R.styleable.TitleBar_leftSrc, 0);
        rightSrc = typedArray.getResourceId(R.styleable.TitleBar_rightSrc, 0);
        rightSrc1 = typedArray.getResourceId(R.styleable.TitleBar_rightSrc1, 0);
        middleLayout = typedArray.getResourceId(R.styleable.TitleBar_middleLayout, 0);
        rightLayout = typedArray.getResourceId(R.styleable.TitleBar_rightLayout, 0);
        typedArray.recycle();
        height = (int) getResources().getDimension(R.dimen.title_bar_height);
        touchBack = R.drawable.tr_sl_half_tran;
        space = Utils.dip2px(context, 2);
        imagePadding= 3;
        init();
    }

    private void init() {
        addMiddleView();
        addLeftView();
        addRightView();
    }

    private void addMiddleView() {
        if (!TextUtils.isEmpty(title)) {
            TextView textView = new TextView(context);
            textView.setId(R.id.tb_tv_title);
            textView.setText(title);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER);
            RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height);
            rl.addRule(RelativeLayout.CENTER_IN_PARENT);
            textView.setLayoutParams(rl);
            addView(textView);
        } else if (middleLayout != 0) {
            inflate(context,middleLayout,this);
        }
    }


    private void addLeftView() {
        if (leftSrc != 0) {
            ImageView imageView = new ImageView(context);
            imageView.setId(R.id.tb_iv_left);
            imageView.setScaleType(imageScaleType);
            imageView.setImageResource(leftSrc);
            imageView.setBackgroundResource(touchBack);
            imageView.setPadding(imagePadding,imagePadding,imagePadding,imagePadding);
            RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(height, height);
            imageView.setLayoutParams(rl);
            addView(imageView);
        } else if (!TextUtils.isEmpty(leftText)) {
            TextView textView = new TextView(context);
            textView.setId(R.id.tb_tv_left);
            textView.setText(leftText);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER);
            textView.setClickable(true);
            textView.setPadding(space * 4, 0, space * 4, 0);
            textView.setBackgroundResource(touchBack);
            RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height);
            textView.setLayoutParams(rl);
            addView(textView);
        }
    }


    private void addRightView() {
        if (rightSrc != 0) {
            ImageView imageView = new ImageView(context);
            imageView.setId(R.id.tb_iv_right);
            imageView.setImageResource(rightSrc);
            imageView.setScaleType(imageScaleType);
            imageView.setBackgroundResource(touchBack);
            imageView.setPadding(imagePadding,imagePadding,imagePadding,imagePadding);
            RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(height, height);
            rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            imageView.setLayoutParams(rl);
            addView(imageView);
            if (rightSrc1 != 0) {
                ImageView imageView1 = new ImageView(context);
                imageView1.setId(R.id.tb_iv_right1);
                imageView1.setImageResource(rightSrc1);
                imageView1.setScaleType(imageScaleType);
                imageView1.setBackgroundResource(touchBack);
                imageView1.setPadding(imagePadding,imagePadding,imagePadding,imagePadding);
                RelativeLayout.LayoutParams rl1 = new RelativeLayout.LayoutParams(height, height);
                rl1.addRule(RelativeLayout.LEFT_OF, R.id.tb_iv_right);
                imageView1.setLayoutParams(rl1);
                addView(imageView1);
            }
        } else if (!TextUtils.isEmpty(rightText)) {
            TextView textView = new TextView(context);
            textView.setId(R.id.tb_tv_right);
            textView.setText(rightText);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER);
            textView.setClickable(true);
            textView.setPadding(space * 4, 0, space * 4, 0);
            textView.setBackgroundResource(touchBack);
            RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height);
            rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            textView.setLayoutParams(rl);
            addView(textView);
        }else if (rightLayout != 0) {
            inflate(context,rightLayout,this);
        }
    }
}
