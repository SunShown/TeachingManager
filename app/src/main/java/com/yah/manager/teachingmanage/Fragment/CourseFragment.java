package com.yah.manager.teachingmanage.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.yah.manager.teachingmanage.Activity.CourseDetailInfoActivity;
import com.yah.manager.teachingmanage.Adapter.CourseInfoAdapter;
import com.yah.manager.teachingmanage.Adapter.InfoGallery;
import com.yah.manager.teachingmanage.Bean.CourseInfo;
import com.yah.manager.teachingmanage.Bean.GlobalInfo;
import com.yah.manager.teachingmanage.Bean.UserCourse;
import com.yah.manager.teachingmanage.Bean.UserInfo;
import com.yah.manager.teachingmanage.R;
import com.yah.manager.teachingmanage.Utils.Utils;
import com.yah.manager.teachingmanage.db.dao.CourseInfoDao;
import com.yah.manager.teachingmanage.db.dao.GlobalInfoDao;
import com.yah.manager.teachingmanage.db.dao.UserCourseDao;
import com.yah.manager.teachingmanage.db.dao.UserInfoDao;
import com.yah.manager.teachingmanage.widgets.BorderTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2018/4/5.
 */

public class CourseFragment extends Fragment {

    @BindView(R.id.test_empty)
    TextView testEmpty;
    @BindView(R.id.Text_Course_Subhead_Mon)
    TextView TextCourseSubheadMon;
    @BindView(R.id.View_Course_SubWrap_1)
    LinearLayout ViewCourseSubWrap1;
    @BindView(R.id.Text_Course_Subhead_Tue)
    TextView TextCourseSubheadTue;
    @BindView(R.id.View_Course_SubWrap_2)
    LinearLayout ViewCourseSubWrap2;
    @BindView(R.id.Text_Course_Subhead_Wed)
    TextView TextCourseSubheadWed;
    @BindView(R.id.Text_Course_Subhead_Thu)
    TextView TextCourseSubheadThu;
    @BindView(R.id.Text_Course_Subhead_Fri)
    TextView TextCourseSubheadFri;
    @BindView(R.id.Text_Course_Subhead_Sat)
    TextView TextCourseSubheadSat;
    @BindView(R.id.Text_Course_Subhead_Sun)
    TextView TextCourseSubheadSun;
    @BindView(R.id.test_course_rl)
    RelativeLayout testCourseRl;
    @BindView(R.id.scroll_body)
    ScrollView scrollBody;
    Unbinder unbinder;
    @BindView(R.id.Menu_main_textWeeks)
    TextView weekTextView;
    @BindView(R.id.iv_refresh)
    ImageView ivRefresh;

    private Context context;
    /**
     * Dao成员变量
     */
    GlobalInfoDao gInfoDao;
    UserInfoDao uInfoDao;

    CourseInfoDao cInfoDao;
    UserCourseDao uCourseDao;
    /**
     * 数据模型变量
     */
    GlobalInfo gInfo;//需要isFirstUse和activeUserUid
    UserInfo uInfo;//需要username昵称,gender，phone，headshot，institute，major，year
    private SharedPreferences courseSettings; //课程信息设置
    private int cw;//存储当前选择的周数currentWeek
    private int currWeek;//储存当前周
    /**
     * 数据存储变量
     */
    private LinkedList<CourseInfo> courseInfoList;//课程信息链表，存储有包括cid在内的完整信息
    private Map<String, List<CourseInfo>> courseInfoMap;//课程信息，key为星期几，value是这一天的课程信息

    private List<TextView> courseTextViewList;//保存显示课程信息的TextView
    private Map<Integer, List<CourseInfo>> textviewCourseInfoMap;//保存每个textview对应的课程信息 map,key为哪一天（如星期一则key为1）

    protected int aveWidth;//课程格子平均宽度
    protected int screenWidth;//屏幕宽度
    protected int gridHeight = 80;//格子高度
    // 每天的课程数
    private final int dayCourseNum = 12;
    private ProgressDialog progressDialog;
    private View popupWindowLayout;
    private ListView weekListView;
    private PopupWindow weekListWindow;
    private int uid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course, null);
        context = getActivity().getApplicationContext();
        unbinder = ButterKnife.bind(this, view);
        initView();
        initTable();
        refresh();
        initListener();
        return view;
    }

    /**
     * 初始化课程表布局
     */
    public void initView() {
        // 初始化Dao成员变量
        gInfoDao = new GlobalInfoDao(context);
        uInfoDao = new UserInfoDao(context);

        cInfoDao = new CourseInfoDao(context);
        uCourseDao = new UserCourseDao(context);
//获取课表配置信息
        courseSettings = context.getSharedPreferences("course_setting", MODE_PRIVATE);
        // 初始化数据模型变量
        gInfo = gInfoDao.query();
        if(gInfo == null){
            gInfo = new GlobalInfo();
        }
        uid = gInfo.getActiveUserUid();
// 初始化数据存储变量
        courseInfoList = new LinkedList<CourseInfo>();
        courseTextViewList = new ArrayList<TextView>();
        textviewCourseInfoMap = new HashMap<Integer, List<CourseInfo>>();

        // 初始化临时变量
        currWeek = Utils.getWeeks(gInfo.getTermBegin());
        cw = Utils.getWeeks(gInfo.getTermBegin());
        weekTextView.setTextSize(20);
        weekTextView.setPadding(15, 2, 15, 2);
        //右边白色倒三角
        Drawable down = getResources().getDrawable(R.drawable.title_down);
        down.setBounds(0, 0, down.getMinimumWidth(), down.getMinimumHeight());
        weekTextView.setCompoundDrawables(null, null, down, null);
        weekTextView.setCompoundDrawablePadding(2);
        //计算并显示上周数
        weekTextView.setText("第" + Utils.getWeeks(gInfo.getTermBegin()) + "周(本周)");
    }

    private void initDate() {

    }

    //初始化课程表格
    private void initTable() {
        // 列表布局文件
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        //屏幕宽度
        int width = dm.widthPixels;
        //平均宽度
        int aveWidth = width / 8;
        //给列头设置宽度
        this.screenWidth = width;
        this.aveWidth = aveWidth;

        //屏幕高度
        int height = dm.heightPixels;
        gridHeight = height / dayCourseNum;

        //设置课表界面，动态生成8 * dayCourseNum个textview
        for (int i = 1; i <= dayCourseNum; i++) {

            for (int j = 1; j <= 8; j++) {
                BorderTextView tx = new BorderTextView(getActivity());
                tx.setId((i - 1) * 8 + j);
                //相对布局参数
                RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(
                        aveWidth * 33 / 32 + 1,
                        gridHeight);
                //文字对齐方式
                tx.setGravity(Gravity.CENTER);
                //字体样式
                tx.setTextAppearance(context, R.style.courseTableText);
                //如果是第一列，需要设置课的序号（1 到 12）
                if (j == 1) {
                    tx.setBackgroundDrawable(getResources().getDrawable(R.drawable.main_table_first_colum));
                    tx.setText(String.valueOf(i));
                    rp.width = aveWidth * 3 / 4;
                    //设置他们的相对位置
                    if (i == 1)
                        rp.addRule(RelativeLayout.BELOW, testEmpty.getId());
                    else
                        rp.addRule(RelativeLayout.BELOW, (i - 1) * 8);
                } else {
                    rp.addRule(RelativeLayout.RIGHT_OF, (i - 1) * 8 + j - 1);
                    rp.addRule(RelativeLayout.ALIGN_TOP, (i - 1) * 8 + j - 1);
                    tx.setText("");
                }

                tx.setLayoutParams(rp);
                testCourseRl.addView(tx);
            }
        }

    }

    private void initListener(){
        //设置点击事件
        weekTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showWeekListWindow(weekTextView);
            }
        });
    }
    //爬取课表信息并且上传至服务器，服务器返回带有cid的完整课表信息，再存入本地数据库
    private void refresh() {
        // 显示状态对话框
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.loading_tip));
        progressDialog.setCancelable(true);
        progressDialog.show();

        //如果还未爬取过课表信息，需要爬取
        if(courseSettings.getBoolean("needCrawling_" + uid, false) == true) {
            // 开启连接线程，登录教务处，爬取学生课表信息...
            //将课表信息上传至课表服务器...
            //上传成功后插入本地数据库...
            //刷新课表界面...
            //此处省略部分代码，因初始化的SharedPreferences信息均为false，因此此处条件语句并不会执行

            //设置该用户课程信息已抓取过的标志
            courseSettings.edit().putBoolean("needCrawling_" + uid, false).commit();
        }
        //如果爬取过课表信息，那就直接访问服务器获取课表显示
        else{
            //此处判断课表是否被增删过，就防止不进行增删操作只跳转页面带来的延时损耗
            if(!courseSettings.getBoolean("needRefresh_" + uid, false)){
                getCourseFromServer(uid);//从服务器获取课程数据存入courseInfoList中
                //设置该用户信息已从服务器获取过的标志
                courseSettings.edit().putBoolean("needRefresh_" + uid, false).commit();
            }
            else{ //未修改过就直接本地读取
                getFromLocal(cw);
            }
        }
    }

    //直接从本地数据库缓存提取数据显示
    private void getFromLocal(int cur){
        courseInfoList = uCourseDao.query(uid);
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        //初始化课表
        initCourse();
        //显示课表内容
        initCourseTableBody(cur);
    }
    //从服务器端获取课表，此处demo为简单起见设置了两个例子展示一下效果
    private void getCourseFromServer(int userid){
        //模拟从服务器获取的效果
        CourseInfo cInfo1 = new CourseInfo();
        cInfo1.setCid(1);
        cInfo1.setWeekfrom(2);
        cInfo1.setWeekto(18);
        cInfo1.setWeektype(1);
        cInfo1.setDay(3);
        cInfo1.setLessonfrom(3);
        cInfo1.setLessonto(4);
        cInfo1.setCoursename("数据库原理");
        cInfo1.setTeacher("李华");
        cInfo1.setPlace("第一教学楼302");
        CourseInfo cInfo2 = new CourseInfo();
        cInfo2.setCid(2);
        cInfo2.setWeekfrom(1);
        cInfo2.setWeekto(8);
        cInfo2.setWeektype(1);
        cInfo2.setDay(1);
        cInfo2.setLessonfrom(5);
        cInfo2.setLessonto(7);
        cInfo2.setCoursename("操作系统原理");
        cInfo2.setTeacher("王芳");
        cInfo2.setPlace("第三教学楼105");

        courseInfoList.add(cInfo1);
        courseInfoList.add(cInfo2);

        //如果从服务器获取成功，则插入数据库
        saveCourse();

        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        //初始化课表
        initCourse();
        //显示课表内容
        initCourseTableBody(cw);

    }

    //将课程列表存入数据库
    private boolean saveCourse() {
        if (uCourseDao.clear(uid)) {
            for (CourseInfo cInfo : courseInfoList) {
//                int cid = cInfoDao.insert(cInfo);
                cInfoDao.insert(cInfo);
                int cid = cInfo.getCid();
                if (cid == 0) {
                    return false;
                }
                UserCourse uCourse = new UserCourse();
                uCourse.setUid(uid);
                uCourse.setCid(cid);
                if(!uCourseDao.insert(uCourse)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    //初始化课表，分配空间，将courseInfoList中的课程放入courseInfoMap中
    private void initCourse() {
        courseInfoMap = new HashMap<String, List<CourseInfo>>();
        for (int i =1 ; i <= 7; i++) {
            LinkedList<CourseInfo> dayCourses = new LinkedList<CourseInfo>();
            for (CourseInfo courseInfo : courseInfoList) {
                int day = courseInfo.getDay();
                if(day==i) {
                    dayCourses.add(courseInfo);
                }
            }
            courseInfoMap.put(String.valueOf(i),dayCourses);
        }
    }

    private void initCourseTableBody(int currentWeek){
        for(Map.Entry<String, List<CourseInfo>> entry: courseInfoMap.entrySet())
        {
            //查找出最顶层的课程信息（顶层课程信息即显示在最上层的课程，最顶层的课程信息满足两个条件 1、当前周数在该课程的周数范围内 2、该课程的节数跨度最大
            CourseInfo upperCourse = null;
            //list里保存的是一周内某 一天的课程
            final List<CourseInfo> list = new ArrayList<CourseInfo>(entry.getValue());
            //按开始的时间（哪一节）进行排序
            Collections.sort(list, new Comparator<CourseInfo>(){
                @Override
                public int compare(CourseInfo arg0, CourseInfo arg1) {

                    if(arg0.getLessonfrom() < arg1.getLessonfrom())
                        return -1;
                    else
                        return 1;
                }

            });
            int lastListSize;
            do {

                lastListSize = list.size();
                Iterator<CourseInfo> iter = list.iterator();
                //先查找出第一个在周数范围内的课
                while(iter.hasNext())
                {
                    CourseInfo c = iter.next();
                    if(((c.getWeekfrom() <= currentWeek && c.getWeekto() >= currentWeek) || currentWeek == -1) && c.getLessonto() <= 12)
                    {
                        //判断当前周是否要放置该课程（该课程是否符合当前周单双周上课要求）
                        if(Utils.isCurrWeek(c,currentWeek)) {
                            //从list中移除该项，并设置这节课为顶层课
                            iter.remove();
                            upperCourse = c;
                            break;
                        }
                    }
                }
                if(upperCourse != null)
                {
                    List<CourseInfo> cInfoList = new ArrayList<CourseInfo>();
                    cInfoList.add(upperCourse);
                    int index = 0;
                    iter = list.iterator();
                    //查找这一天有哪些课与刚刚查找出来的顶层课相交
                    while(iter.hasNext())
                    {
                        CourseInfo c = iter.next();
                        //先判断该课程与upperCourse是否相交，如果相交加入cInfoList中
                        if((c.getLessonfrom() <= upperCourse.getLessonfrom()
                                &&upperCourse.getLessonfrom() < c.getLessonto())
                                ||(upperCourse.getLessonfrom() <= c.getLessonfrom()
                                && c.getLessonfrom() < upperCourse.getLessonto()))
                        {
                            cInfoList.add(c);
                            iter.remove();
                            //在判断哪个跨度大，跨度大的为顶层课程信息
                            if((c.getLessonto() - c.getLessonto()) > (upperCourse.getLessonto() - upperCourse.getLessonfrom())
                                    && ((c.getWeekfrom() <= currentWeek && c.getWeekto() >= currentWeek) || currentWeek == -1))
                            {
                                upperCourse = c;
                                index ++;
                            }

                        }

                    }

                    //五种颜色的背景
                    int[] background = {R.drawable.main_course1, R.drawable.main_course2,
                            R.drawable.main_course3, R.drawable.main_course4,
                            R.drawable.main_course5};
                    //记录顶层课程在cInfoList中的索引位置
                    final int upperCourseIndex = index;
                    // 动态生成课程信息TextView
                    TextView courseInfo = new TextView(context);
                    courseInfo.setId(1000 + upperCourse.getDay() * 100 + upperCourse.getLessonfrom() * 10 + upperCourse.getCid());//设置id区分不同课程
                    int id = courseInfo.getId();
                    textviewCourseInfoMap.put(id, cInfoList);
                    courseInfo.setText(upperCourse.getCoursename() + "\n@" + upperCourse.getPlace());
                    //该textview的高度根据其节数的跨度来设置
                    RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                            aveWidth * 31 / 32,
                            (gridHeight - 5) * 2 + (upperCourse.getLessonto() - upperCourse.getLessonfrom() - 1) * gridHeight);
                    //textview的位置由课程开始节数和上课的时间（day of week）确定
                    rlp.topMargin = 5 + (upperCourse.getLessonfrom() - 1) * gridHeight;
                    rlp.leftMargin = 1;
                    // 前面生成格子时的ID就是根据Day来设置的，偏移由这节课是星期几决定
                    rlp.addRule(RelativeLayout.RIGHT_OF, upperCourse.getDay());
                    //字体居中中
                    courseInfo.setGravity(Gravity.CENTER);
                    //选择一个颜色背景
                    int colorIndex = ((upperCourse.getLessonfrom() - 1) * 8 + upperCourse.getDay()) % (background.length - 1);
                    courseInfo.setBackgroundResource(background[colorIndex]);
                    courseInfo.setTextSize(12);
                    courseInfo.setLayoutParams(rlp);
                    courseInfo.setTextColor(Color.WHITE);
                    //设置不透明度
                    courseInfo.getBackground().setAlpha(200);
                    // 设置监听事件
                    courseInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            Log.v("text_view", String.valueOf(arg0.getId()));
                            Map<Integer, List<CourseInfo>> map = textviewCourseInfoMap;
                            final List<CourseInfo> tempList = map.get(arg0.getId());
                            if(tempList.size() > 1)
                            {
                                //如果有多个课程，则设置点击弹出gallery 3d 对话框
                                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View galleryView = layoutInflater.inflate(R.layout.info_gallery_layout, null);
                                final Dialog coursePopupDialog = new AlertDialog.Builder(getActivity()).create();
                                coursePopupDialog.setCanceledOnTouchOutside(true);
                                coursePopupDialog.setCancelable(true);
                                coursePopupDialog.show();
                                WindowManager.LayoutParams params = coursePopupDialog.getWindow().getAttributes();
                                params.width = WindowManager.LayoutParams.MATCH_PARENT;
                                coursePopupDialog.getWindow().setAttributes(params);
                                CourseInfoAdapter adapter = new CourseInfoAdapter(getActivity(), tempList, screenWidth, cw);
                                InfoGallery gallery = (InfoGallery) galleryView.findViewById(R.id.info_gallery);
                                gallery.setSpacing(10);
                                gallery.setAdapter(adapter);
                                gallery.setSelection(upperCourseIndex);
                                gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(
                                            AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {
                                        CourseInfo courseInfo = tempList.get(arg2);
                                        Intent intent = new Intent();
                                        Bundle mBundle = new Bundle();
                                        mBundle.putSerializable("courseInfo", courseInfo);
                                        intent.putExtras(mBundle);
                                        intent.setClass(getActivity(), CourseDetailInfoActivity.class);
                                        startActivity(intent);
                                        coursePopupDialog.dismiss();
                                    }
                                });
                                coursePopupDialog.setContentView(galleryView);
                            }
                            else
                            {
                                Intent intent = new Intent();
                                Bundle mBundle = new Bundle();
                                mBundle.putSerializable("courseInfo", tempList.get(0));
                                intent.putExtras(mBundle);
                                intent.setClass(getActivity(), CourseDetailInfoActivity.class);
                                startActivity(intent);
                            }

                        }

                    });
                    testCourseRl.addView(courseInfo);
                    courseTextViewList.add(courseInfo);

                    upperCourse = null;
                }
            } while(list.size() < lastListSize && list.size() != 0);
        }

    }

    /**
     * 将爬取课程信息上传课表服务器
     * 若上传成功，则取回服务器端分配的cid及所有课程信息，存入本地数据库
     * @param couInfo
     */
    private void uploadCourseInfo(final CourseInfo couInfo) {

    }

    /**
     * 显示周数下拉列表悬浮窗
     * @param parent
     */
    private void showWeekListWindow(View parent){

        if(weekListWindow == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //获取layout
            popupWindowLayout = layoutInflater.inflate(R.layout.week_list_layout, null);
            popupWindowLayout.setBackgroundColor(Color.rgb(216,216,216));
            weekListView = (ListView) popupWindowLayout.findViewById(R.id.week_list_view_body);

            List<Map<String, Object>> weekList = new ArrayList<Map<String, Object>>();
            //默认25周
            for(int i = 1; i <= 25; i ++)
            {
                Map<String, Object> rowData = new HashMap<String, Object>();
                rowData.put("week_index", "第" + i + "周");
                weekList.add(rowData);
            }

            //设置listview的adpter
            SimpleAdapter listAdapter = new SimpleAdapter(getActivity(),
                    weekList, R.layout.week_list_item_layout,
                    new String[]{"week_index"},
                    new int[]{R.id.week_list_item});

            //设置recyclerview类型的listview的adpter()
//            WeekAdapter listAdapter = new WeekAdapter(weekList);
            weekListView.setAdapter(listAdapter);
            weekListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adpater, View arg1,
                                        int arg2, long arg3) {
                    int index = 0;
                    String indexStr = weekTextView.getText().toString();
                    indexStr = indexStr.replace("第", "").replace("周(本周)", "");
                    indexStr = indexStr.replace("周(非本周)", "");
                    if(!indexStr.equals("全部"))//没啥用
                        index = Integer.parseInt(indexStr);
                    if(currWeek == (arg2 + 1)){
                        weekTextView.setText("第" + (arg2 + 1) + "周(本周)");
                    }
                    else{
                        weekTextView.setText("第" + (arg2 + 1) + "周(非本周)");
                    }
                    weekListWindow.dismiss();
                    if((arg2 + 1) != index)
                    {
                        cw = arg2+1;
                        Log.v("courseActivity", "cw值改变："+ cw);
                        Log.v("courseActivity", "清空当前课程信息");
                        for(TextView tx : courseTextViewList)
                        {
                            testCourseRl.removeView(tx);
                        }
                        courseTextViewList.clear();
                        //重新设置课程信息
                        initCourse();
                        initCourseTableBody(cw);

                    }
                }
            });
            int width = weekTextView.getWidth();
            //实例化一个popupwindow
            weekListWindow = new PopupWindow(popupWindowLayout, width + 100, width + 120);

        }

        weekListWindow.setFocusable(true);
        //设置点击外部可消失
        weekListWindow.setOutsideTouchable(true);
        weekListWindow.setBackgroundDrawable(new BitmapDrawable());
        //消失的时候恢复按钮的背景（消除"按下去"的样式）
        weekListWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                weekTextView.setBackgroundDrawable(null);
            }
        });
        weekListWindow.showAsDropDown(parent, -50, 0);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
