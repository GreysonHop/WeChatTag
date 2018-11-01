package com.study.wechattag.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.study.wechattag.R;

import java.lang.reflect.Field;


/**
 * Created by hx on 2015/11/13.
 */
public abstract class MyBaseActivity extends FragmentActivity implements View.OnClickListener {

    // 标题栏
    public TextView mTitleText;
    public Button mLeftBtn;
    public Button mRightBtn;

    public Context mContext;
    public ProgressDialog mLoadingDialog;

    //沉浸式状态相关参数
    private boolean willImmerseStatusBar = true;
    private boolean willPadContent = true;          //布局内容是否不扩展到状态栏下
    private boolean willPadContentWithoutBg = false; //布局内容（背景图会全屏）是否不扩展到状态栏下
    private int statusBarColor = Color.WHITE;       //填充start bar的View的背景色，注意不是colorID
    private boolean needShadow = true;              //是否加阴影。某些手机（如5.0-5.1）状态栏字体白色，而且还不能设置为黑的

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mContext = this;
        init();
        setContentView(setLayout());
        if (willImmerseStatusBar) {
            immerseStatusBar();
        }
        initView();
        initEvent();

        /*if(Build.VERSION.SDK_INT >= 23){//解决状态栏浅灰色？
            try {
                Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
                field.setAccessible(true);
                field.setInt(getWindow().getDecorView(), Color.TRANSPARENT);  //改为透明
            } catch (Exception e) {}
        }*/
    }

    /**
     * invoked before custom method {@link #setLayout()}. it means being invoked before {@link Activity#setContentView(View)};
     */
    public void init() {
    }

    /**
     * get a layout resource to be the param of {@link Activity#setContentView(View)}
     *
     * @return id of layout resource
     */
    @LayoutRes
    public abstract int setLayout();

    /**
     * in this method, init view like invoking findViewById(); invoked after custom method {@link #setLayout()}. it means being invoked after {@link Activity#setContentView(View)};
     */
    public void initView() {
    }

    /**
     *
     */
    public void initEvent() {
    }

    public void setImmerseStatusBar(boolean bol) {
        willImmerseStatusBar = bol;
    }

    /**
     * 设置沉浸状态栏参数，请在{@link #initView()}方法中或周期排在它之前的方法中设置！
     *
     * @param willPadContent true时则内容不会扩展到状态栏下
     * @param colorInt       颜色值，不是资源ID。预留两个值：0为使用默认颜色，-1表示透明颜色
     * @param needShadow     true则为6.0以上手机设置黑色状态栏字体，5.0-5.1设置阴影
     */
    public void setStatusBarViewOptions(boolean willPadContent, int colorInt, boolean needShadow) {
        this.setStatusBarViewOptions(willPadContent, false, colorInt, needShadow);
    }

    /**
     * 设置沉浸状态栏参数，请在{@link #initView()}方法中或周期排在它之前的方法中设置！
     *
     * @param willPadContent          true时则内容不会扩展到状态栏下
     * @param willPadContentWithoutBg 变量为true时根布局的内容不会扩展到状态栏下，背景图除外
     *                                （即给根布局增加跟StatusBar高度一样的paddingTop）。
     *                                但必须当willPadContent为false时该变量才起作用！
     * @param colorInt                颜色值，不是资源ID。预留两个值：0为使用默认颜色，-1表示透明颜色
     * @param needShadow              true则为6.0以上手机设置黑色状态栏字体，5.0-5.1设置阴影
     */
    public void setStatusBarViewOptions(boolean willPadContent, boolean willPadContentWithoutBg, int colorInt, boolean needShadow) {
        this.willPadContent = willPadContent;
        this.willPadContentWithoutBg = willPadContentWithoutBg;
        if (colorInt != 0) {
            this.statusBarColor = colorInt;
        }
        this.needShadow = needShadow;
    }

    /**
     * 沉浸状态栏，让内容延伸到状态栏下
     */
    protected final void immerseStatusBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }

        Window window = getWindow();
        ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    /*| View.SYSTEM_UI_FLAG_LAYOUT_STABLE*/;
            decorView.setSystemUiVisibility(option);
            window.setStatusBarColor(Color.TRANSPARENT);

        } else {
            WindowManager.LayoutParams attributes = window.getAttributes();
            int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            attributes.flags |= flagTranslucentStatus;
            window.setAttributes(attributes);

        }

        //根布局添加占位状态栏
        ImageView statusBarView = new ImageView(this);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getStatusBarHeight());
        decorView.addView(statusBarView, lp);

        if (willPadContent) {
            ViewGroup rootView = (ViewGroup) decorView.findViewById(Window.ID_ANDROID_CONTENT);
            rootView.setPadding(0, getStatusBarHeight(), 0, 0);
            if (statusBarColor != -1) {
                statusBarView.setBackgroundColor(statusBarColor);
            } else {
                statusBarView.setBackgroundColor(Color.TRANSPARENT);
            }
        } else {
            if (willPadContentWithoutBg) {
                ViewGroup rootView = decorView.findViewById(Window.ID_ANDROID_CONTENT);
                if (rootView.getChildCount() > 0) {
                    rootView.getChildAt(0).setPadding(0, getStatusBarHeight(), 0, 0);
                }
            }
            statusBarView.setBackgroundColor(Color.TRANSPARENT);
        }

        if (needShadow) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                statusBarView.setImageResource(R.drawable.bg_grad_shadow);
            }
        }
    }

    protected final int getStatusBarHeight() {
        int result = 0;
        //获取状态栏高度的资源id
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        if (result == 0) {
            result = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());
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