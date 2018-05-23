package com.yah.manager.teachingmanage.Utils;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.yah.manager.teachingmanage.Dialog.LoadingDialog;

/**
 * Created by Administrator on 2018/4/5.
 */

public class MyDialogHandler extends Handler {

    public static final int SHOW_LOADING_DIALOG = 0x0102;
    public static final int DISMISS_LOADING_DIALOG = 0x0103;
    private LoadingDialog dialog;
    private Context context;
    private String tip;

    public MyDialogHandler(Context context, String tip) {
        this.context = context;
        this.tip = tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    @Override
    public void handleMessage(Message msg) {


        switch (msg.what) {

            case SHOW_LOADING_DIALOG: {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                dialog=new LoadingDialog(context, tip);
                dialog.show();

                Log.i("dialog-state", "dialog-showing!!!!");
            }
            break;

            case DISMISS_LOADING_DIALOG: {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                Log.i("dialog-state", "dialog-dismissed!!!!");
            }
            break;

        }

    }
}
