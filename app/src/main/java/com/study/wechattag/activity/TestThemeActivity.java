package com.study.wechattag.activity;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.study.wechattag.R;
import com.study.wechattag.view.FlowTagView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Greyson on 2016/4/26.
 */
public class TestThemeActivity extends MyBaseActivity implements View.OnClickListener {

    private FlowTagView flowTagView;

    @Override
    public void init() {
        super.init();
        setStatusBarViewOptions(false, true, -1, true);
//        setStatusBarViewOptions(true, getResources().getColor(R.color.app_main_green), false);
    }

    @Override
    public int setLayout() {
        return R.layout.act_test_theme;
    }

    @Override
    public void initEvent() {
        super.initEvent();

        ArrayList<String> tagList = new ArrayList<>();
        tagList.add("#制服の诱惑");
        tagList.add("#小女妖的自拍秀");
        tagList.add("#爆笑内涵段子");
        tagList.add("#表白");
        tagList.add("#新手上路");
        tagList.add("#逗比欢乐多");
        tagList.add("#心情随笔录");

        flowTagView = findViewById(R.id.flowTagView);
        flowTagView.setTagList(tagList);

        ((FlowTagView) findViewById(R.id.flowTagView2)).setTagList(tagList);
    }

    public void onClick(View view) {
        List<String> list = flowTagView.getSelectedList();
        String result = "";
        for (String s : list) {
            Log.d("greyson", s);
            result += s;
        }
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
    }
}
