package com.study.wechattag.activity;

import android.os.Bundle;
import android.view.View;

import com.study.wechattag.R;

/**
 * Created by Administrator on 2016/4/26.
 */
public class TestThemeActivity extends MyBaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_test_theme);

        setStatusBarViewOptions(false, -1, true);
        immerseStatusBar();

    }

}
