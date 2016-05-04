package com.study.wechattag.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentActivity;
import android.view.View;
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
     * @param title
     * @param message
     * @param indeterminate
     */
    public void showLoadingDialog(String title, String message, boolean indeterminate){
        if(mLoadingDialog == null){
            mLoadingDialog = ProgressDialog.show(mContext, title, message, indeterminate);
        }else{
            mLoadingDialog.setMessage(message);
            mLoadingDialog.setTitle(title);
            mLoadingDialog.setIndeterminate(indeterminate);
        }
    }

    /**
     * 关闭加载对话框
     */
    public  void closeLoadingDialog(){
        if(mLoadingDialog != null){
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }
}