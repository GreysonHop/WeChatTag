package com.study.wechattag.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.study.wechattag.R;
import com.study.wechattag.model.Group;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/26.
 */
public class MainActivity extends MyBaseActivity implements View.OnClickListener {
    private TextView tvGroup;
    private String groupId;
    private String groupName;
    //这里是写死数据，所以就算“添加分组”界面新增了分组也不会刷新此分组！
    // 但在自己的项目，如果新增了分组，不要忘记更新分组哦！
    private List<Group> groupList = new ArrayList<>();

    @Override
    public int setLayout() {
        return R.layout.act_main;
    }

    @Override
    public void initView() {
        super.initView();

        tvGroup = (TextView) findViewById(R.id.tv_group);
        tvGroup.setOnClickListener(this);
        initData();

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("注意！")
                .setMessage("目前此项目流程只能执行一次！即进入添加分组界面后，里面新创建的分组不会更新到当前的所有分组列表中！")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
//        builder.show();
    }

    //初始化一些假数据
    private void initData() {
        groupId = "1,2,3";
        groupName = "11,22,33";
        Group group;
        for (int i = 0; i < 5; i++) {
            group = new Group();
            group.setGroupId(i + "");
            group.setGroupName(i + "" + i);
            groupList.add(group);
        }
        tvGroup.setText(groupName);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.gotoAct) {
            startActivity(new Intent(this, TestThemeActivity.class));
            return;
        }

        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.tv_group://分组标签
                intent.setClass(this, AddGroupActivity.class);
                intent.putExtra("userId", "101");//这里随便传的，大家传自己要更新分组信息的用户ID
                intent.putExtra("groupId", groupId);//传入原来拥有的标签ID
                intent.putExtra("groupName", groupName);//传入原本拥有的标签名
                intent.putExtra("groupList", (Serializable) groupList);
                startActivityForResult(intent, 0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            String name = data.getStringExtra("groupName");
            String id = data.getStringExtra("groupId");
            Log.i("MainActivity", "groupId" + id + "- groupName" + name);
            tvGroup.setText(name);
            groupId = id;
            groupName = name;
        }
    }
}
