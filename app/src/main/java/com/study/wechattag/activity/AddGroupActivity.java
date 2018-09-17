package com.study.wechattag.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.study.wechattag.R;
import com.study.wechattag.model.Group;
import com.study.wechattag.view.MultipleLinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Administrator on 2016/3/17 0017.
 * 给患者分组并可以添加新分组的界面
 */
public class AddGroupActivity extends MyBaseActivity
        implements View.OnClickListener, MultipleLinearLayout.OnChildViewAreaListener {
    private List<Group> groupList = new ArrayList<>();  //分组集合

//    private String from ;  //

    /**
     * 手动输入、待上传的标签集合
     */
    List<View> tagsFromEdit = new ArrayList<>();
    MultipleLinearLayout llSelectTag;//要新增或删除标签的自定义组件
    MultipleLinearLayout llAllTag;//包含所有标签的自定义组件
    /**
     * 添加标签的输入框
     */
    private EditText etTag;
    private final String TAG = "AddGroupActivity";
    /**
     * 当前界面的操作意图，"createGroup"即只创建新的分组
     */
    private String intentStr;////////////////////////////////////////添加“只新增分组”的功能

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_add_group);

        setStatusBarViewOptions(true, getResources().getColor(R.color.app_main_green), false);
        immerseStatusBar();

        initView();
        initData();
    }

    /**
     * 初始化页面
     */
    private void initView() {
        // 顶部bar
        mTitleText = (TextView) findViewById(R.id.tv_title_center);
        mTitleText.setText("添加分组");
        mLeftBtn = (Button) findViewById(R.id.iv_tools_left);
        mLeftBtn.setOnClickListener(this);
        mRightBtn = (Button) findViewById(R.id.iv_tools_right);
        mRightBtn.setOnClickListener(this);
        mRightBtn.setText("保存");
        mRightBtn.setVisibility(View.VISIBLE);

        llSelectTag = (MultipleLinearLayout) findViewById(R.id.ll_group_selected);
        llSelectTag.setOnChildViewAreaListener(this);//设置多行LinearLayout的子视图区间点击监听器
        llAllTag = (MultipleLinearLayout) findViewById(R.id.ll_group_all);

        etTag = (EditText) findViewById(R.id.edit_label);
    }

    /**
     * 保存从上个页面传过来的数据，即目前患者所属的分组ID。
     */
    private String groupId = "";
    /**
     * 保存从上个页面传过来的数据，即目前患者所属的分组名。
     */
    private String groupName;//当前已选的分组
    /**
     * 保存从上个页面传过来的患者Id；如果为空或Null，说明是从AddPatientActivity界面跳过来
     */
    private String userId;

    /**
     * 初始化数据
     */
    private void initData() {
        Intent intent = this.getIntent();
        groupList = (List<Group>) intent.getSerializableExtra("groupList");
        intentStr = intent.getStringExtra("intent");

        groupName = intent.getStringExtra("groupName");
        groupId = intent.getStringExtra("groupId");
        userId = intent.getStringExtra("userId");//这个大家自己传
        Log.i(TAG, "从上一个界面接收到的分组：" + groupName + "\n分组ID：" + groupId + "\n患者UserID:" + userId);
        String groupIdForMatch = "," + groupId + ",";
        String tempGroupName;
        for (int i = 0; i < groupList.size(); i++) {
            tempGroupName = groupList.get(i).getGroupName();
            TextView et = new TextView(this);
            et.setSingleLine();
            et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            et.setBackgroundResource(R.drawable.ic_tag_all_bg);
            et.setPadding(etTag.getPaddingLeft(), etTag.getPaddingTop(), etTag.getPaddingRight(), etTag.getPaddingBottom());
            et.setText(tempGroupName);

            String patternStr = "," + groupList.get(i).getGroupId() + ",";
            Log.i(TAG, "要匹配的pattern:" + patternStr + "-groupId:" + groupIdForMatch);
            if (groupIdForMatch.contains(patternStr)) {//匹配则说明全部标签一栏中此View要添加到新增一栏
                et.setTextColor(getResources().getColor(R.color.app_main_green));
                et.getBackground().setLevel(1);
                View newView = addTag(tempGroupName);
                allToCreateMap.put(i, newView);
                createToAllMap.put(newView, i);
                selectedInAllMap.put(i, et);
            } else {
                et.setTextColor(getResources().getColor(R.color.text_black));
            }

            et.setId(0);
            if (!"createGroup".equals(intentStr)) {
//                Log.i(TAG, "intentStr:"+intentStr);
                et.setOnClickListener(this);
            }
            llAllTag.addView(et);
        }

    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:   //添加分组
                    if ("0".equals(msg.getData().getString("code"))) {

                        Toast.makeText(AddGroupActivity.this, "添加分组成功", Toast.LENGTH_LONG).show();
                        String result = msg.getData().getString("groupId");
                        Log.i(TAG, "返回的ID:" + result);
                        uploadedGroupId = result;//如果新增了分组，不要忘记更新分组哦
                        savePatientGroupInfo();
                    } else {
                        Toast.makeText(mContext, "create success", Toast.LENGTH_SHORT).show();
                    }
                    break;
                //修改用户分组信息
                case 1:
                    closeLoadingDialog();
                    if ("0".equals(msg.getData().getString("code"))) {
                        Toast.makeText(mContext, "修改分组成功", Toast.LENGTH_SHORT).show();
                        groupName = getNewGroupName();
                        groupId = getNewGroupId();
                        setResultAndBack();
                    } else {
                        Toast.makeText(mContext, "修改分组失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }

        }
    };

    /**
     * 按钮事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_tools_left:
                setResultAndBack();
                break;
            case R.id.iv_tools_right:
                //如果有新创建的分组，先将其上传到后台
                if (!"".equals(getGroupNameFromEdit())) {
                    showLoadingDialog("", "正在上传新分组...", false);
                    Log.i(TAG, "onClick() 新的标签：" + getNewGroupName() + "-要上传的标签：" + getGroupNameFromEdit());
//                    Http_addGroup_Event event = new Http_addGroup_Event(getGroupNameFromEdit(), MyDataUtil.getDocId(mContext));

                    //这里模仿请求网络，让后台保存新添加的分组标签名，并且返回他们对应的ID数组。
                    mLoadingDialog = ProgressDialog.show(mContext, "", "正在保存新分组", true);
                    Message msg = new Message();
                    msg.what = 0;
                    Bundle bundle = new Bundle();
                    bundle.putString("code", "0");
                    bundle.putString("groupId", "6,7");//返回数据的格式，这里写死了，如果是自己的后台就要返回自己新增分组后分组的ID，有几个就返回几个
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);

                } else {//如果没有要创建的分组，则直接保存用户的分组信息
                    savePatientGroupInfo();
                }

                break;
            case -1://新增标签一栏中子视图点击事件处理
                for (int i = 0; i < llSelectTag.getChildCount(); i++) {
                    View view = llSelectTag.getChildAt(i);
                    if (v.equals(view)) {
                        if (v == clickedSelectTag) {//如果当前按到的组件跟上次按的是同一个
                            llSelectTag.removeViewAt(i);
                            tagsFromEdit.remove(view);
                            Integer num;
                            if ((num = createToAllMap.get(view)) != null) {
                                createToAllMap.remove(view);
                                View temp = llAllTag.getChildAt(num);
                                temp.getBackground().setLevel(0);
                                ((TextView) temp).setTextColor(getResources().getColor(R.color.text_black));
                                selectedInAllMap.remove(num);
                                allToCreateMap.remove(num);
                            }
//                            tagList.remove(i);
                            clickedSelectTag = null;
                        } else {
                            if (clickedSelectTag == null) {//第一次选择
                            } else {//第二次选择，上次按过不同的一个
                                TextView text = (TextView) clickedSelectTag;
                                text.setTextColor(getResources().getColor(R.color.app_main_green));
                                text.getBackground().setLevel(0);
                            }
                            view.getBackground().setLevel(1);//选中状态：1
                            ((TextView) view).setTextColor(getResources().getColor(R.color.app_text_white_color));
                            clickedSelectTag = llSelectTag.getChildAt(i);
                        }
                        return;
                    }
                }
                break;
            case 0://全部标签一栏中子视图点击事件处理
                for (int i = 0; i < llAllTag.getChildCount(); i++) {
                    View view = llAllTag.getChildAt(i);
                    if (v.equals(view)) {
                        for (Integer position : selectedInAllMap.keySet()) {//判断点击的项是否已经点击过
                            if (position == i) {
                                View temp = selectedInAllMap.get(i);
                                temp.getBackground().setLevel(0);
                                ((TextView) temp).setTextColor(getResources().getColor(R.color.text_black));
//                                Log.i("ActWeChatTag", "要添加的顶对应位置：" + i + "-" + allToCreateMap.get(i));
                                createToAllMap.remove(allToCreateMap.get(i));
                                llSelectTag.removeView(allToCreateMap.get(i));
//                                tagList.remove(i);//这里暂时无法获取到达新增标签一栏中的子视图对应位置
                                selectedInAllMap.remove(i);
                                allToCreateMap.remove(i);
                                return;
                            }
                        }
                        view.getBackground().setLevel(1);
                        ((TextView) view).setTextColor(getResources().getColor(R.color.app_main_green));
                        View newView = addTag(((TextView) view).getText().toString());
                        allToCreateMap.put(i, newView);
                        createToAllMap.put(newView, i);
                        selectedInAllMap.put(i, view);
//                        Log.i("ActWeChatTag", "要添加的顶对应位置："+i+"-");
                        return;
                    }
                }
                break;
        }
    }

    /**
     * 返回键监听
     *
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            setResultAndBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 在新增标签一栏中添加一个标签
     *
     * @param contain 标签内容
     * @return 返回新增标签对象
     */
    private View addTag(String contain) {
        TextView textView = new TextView(this);
        textView.setSingleLine();
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        textView.setBackgroundResource(R.drawable.ic_tag_new_bg);
        textView.setPadding(etTag.getPaddingLeft(), etTag.getPaddingTop(), etTag.getPaddingRight(), etTag.getPaddingBottom());

        textView.setText(contain);
        textView.setTextColor(getResources().getColor(R.color.app_main_green));
        textView.setOnClickListener(this);
        int positionToAdd = llSelectTag.getChildCount() - 1;
        llSelectTag.addView(textView, positionToAdd);

        return textView;
    }

    /**
     * 新增标签一栏中，标记第一次点击的项
     */
    View clickedSelectTag = null;

    /**
     * 所有标签一栏中被选中的标签，标签位置和标签View对象
     */
    HashMap<Integer, View> selectedInAllMap = new HashMap<>();
    /**
     * 所有标签一栏中的项添加到新增一栏中后，前后者对应的位置
     */
    HashMap<Integer, View> allToCreateMap = new HashMap<>();
    HashMap<View, Integer> createToAllMap = new HashMap<>();

    @Override
    public void onChildViewAreaOut() {
        if (!TextUtils.isEmpty(etTag.getText().toString())) {
            tagsFromEdit.add(addTag(etTag.getText().toString()));
            etTag.setText("");
        }
    }

    /**
     * 新上传的分组的id
     */
    private String uploadedGroupId;

    /**
     * 更新患者所属分组
     */
    private void savePatientGroupInfo() {
        showLoadingDialog("", "正在保存分组...", false);
        if (TextUtils.isEmpty(userId)) {//如果没有患者ID则直接保存当前界面的分组信息并返回给上个界面
            Toast.makeText(mContext, "修改分组成功", Toast.LENGTH_SHORT).show();
            groupName = getNewGroupName();
            groupId = getNewGroupId();
            closeLoadingDialog();
            setResultAndBack();
        } else {
            String testid = getNewGroupId();
            Log.i(TAG, "更新患者分组信息groupId：" + testid + "-uploadedGroupId:" + uploadedGroupId);

            updateGroup(testid);
        }
    }

    /**
     * 模仿网络请求保存用户的分组信息
     *
     * @param groupIds 分组标签ID
     */
    private void updateGroup(String groupIds) {
        Message msg = new Message();
        msg.what = 1;
        Bundle bundle = new Bundle();
        bundle.putString("code", "0");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    /**
     * 将当前界面的分组名和分组ID保存在Result传回上个Activity，并finish界面
     */
    private void setResultAndBack() {
        Intent intent = new Intent();
        intent.putExtra("groupName", groupName);
        intent.putExtra("groupId", groupId);
        setResult(0, intent);

        finish();
    }

    /**
     * 获取当前新增一栏中所有标签的id
     *
     * @return 通过逗号隔开的标签的id组成的字符串
     */
    private String getNewGroupId() {
        String groupId = "";
        if (selectedInAllMap.size() != 0) {
            for (Integer position : selectedInAllMap.keySet()) {
                Log.i(TAG, "有新增标签是从所有标签中选出的,位置为：" + position);
                Group temp = groupList.get(position);
                if (!"".equals(groupId))
                    groupId += "," + temp.getGroupId();
                else {
                    groupId = temp.getGroupId();
                }
            }
        }
        if (!TextUtils.isEmpty(uploadedGroupId)) {//如果有新上传的标签则接上
            if (!"".equals(groupId))
                groupId += "," + uploadedGroupId;
            else {
                groupId = uploadedGroupId;
            }
        }
        return groupId;
    }

    /**
     * 获取当前新增一栏中所有标签名
     *
     * @return 通过逗号隔开的标签的名字组成的字符串
     */
    private String getNewGroupName() {
        String groupName = "";
        for (int i = 0; i < llSelectTag.getChildCount() - 1; i++) {//减去最后一个编辑框
            if ("".equals(groupName))
                groupName = ((TextView) llSelectTag.getChildAt(i)).getText().toString();
            else
                groupName += "," + ((TextView) llSelectTag.getChildAt(i)).getText().toString();
        }
        return groupName;
    }

    /**
     * 获取所有手动输入的标签的名字
     *
     * @return 通过逗号隔开的标签的名字组成的字符串，没有返回""
     */
    private String getGroupNameFromEdit() {
        String groupName = "";
        for (View v : tagsFromEdit) {//减去最后一个编辑框
            if ("".equals(groupName))
                groupName = ((TextView) v).getText().toString();
            else
                groupName += "," + ((TextView) v).getText().toString();
        }
        return groupName;
    }
}
