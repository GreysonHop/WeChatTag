package com.study.wechattag.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by hx on 2015/11/13.
 */
public class MyBaseActivity extends FragmentActivity implements View.OnClickListener {

    // 标题栏
    public TextView mTitleText;
    public Button mLeftBtn;
    public Button mRightBtn;

    public Context mContext;
    public ProgressDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mContext = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                //导航栏颜色也可以正常设置
//                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                Window window = getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
//                attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }

        addStatusViewWithColor(this, Color.TRANSPARENT);
    }

    private void addStatusViewWithColor(Activity activity, int color) {
        //设置 paddingTop
        ViewGroup rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        rootView.setPadding(0, getStatusBarHeight(), 0, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0 以上直接设置状态栏颜色
            activity.getWindow().setStatusBarColor(color);
        } else {
            //根布局添加占位状态栏
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            View statusBarView = new View(activity);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    getStatusBarHeight());
            statusBarView.setBackgroundColor(color);
            decorView.addView(statusBarView, lp);
        }

    }

    public int getStatusBarHeight() {
        int result = 0;
        //获取状态栏高度的资源id
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub

    }

    public void goActivity(Context context, Class<?> mclass, Intent mIntent) {
        Intent intent = new Intent(context, mclass);
        if (null != mIntent && null != mIntent.getExtras()) {
            intent.putExtras(mIntent.getExtras());
        }
        context.startActivity(intent);
    }

    public String getActivityName() {
        return this.getClass().getSimpleName();
    }

    /**
     * 显示加载对话框
     *
     * @param title
     * @param message
     * @param indeterminate
     */
    public void showLoadingDialog(String title, String message, boolean indeterminate) {
        if (mLoadingDialog == null) {
            mLoadingDialog = ProgressDialog.show(mContext, title, message, indeterminate);
        } else {
            mLoadingDialog.setMessage(message);
            mLoadingDialog.setTitle(title);
            mLoadingDialog.setIndeterminate(indeterminate);
        }
    }

    /**
     * 关闭加载对话框
     */
    public void closeLoadingDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }
}