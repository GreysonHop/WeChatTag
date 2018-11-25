package com.study.wechattag.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import com.study.wechattag.R
import com.study.wechattag.model.Group
import com.study.wechattag.view.MultiLineLinearLayout
import com.study.wechattag.view.MultipleLinearLayout

import java.util.ArrayList
import java.util.HashMap


/**
 * Created by Greyson on 2016/3/17 0017.
 * 给患者分组并可以添加新分组的界面
 */
class AddGroupActivityKt : MyBaseActivity(), View.OnClickListener, MultipleLinearLayout.OnChildViewAreaListener {
    private var groupList: List<Group> = ArrayList()  //分组集合

    //    private String from ;  //

    /**
     * 手动输入、待上传的标签集合
     */
    private var tagsFromEdit: MutableList<View> = ArrayList()
    private var llSelectTag: MultipleLinearLayout? = null//要新增或删除标签的自定义组件
    private var llAllTag: MultiLineLinearLayout? = null//包含所有标签的自定义组件
    /**
     * 添加标签的输入框
     */
    private var etTag: EditText? = null
    private val TAG = "AddGroupActivity"
    /**
     * 当前界面的操作意图，"createGroup"即只创建新的分组
     */
    private var intentStr: String? = null////////////////////////////////////////添加“只新增分组”的功能

    /**
     * 保存从上个页面传过来的数据，即目前患者所属的分组ID。
     */
    private var groupId: String? = ""
    /**
     * 保存从上个页面传过来的数据，即目前患者所属的分组名。
     */
    private var groupName: String? = null//当前已选的分组
    /**
     * 保存从上个页面传过来的患者Id；如果为空或Null，说明是从AddPatientActivity界面跳过来
     */
    private var userId: String? = null

    internal var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0   //添加分组
                -> if ("0" == msg.data.getString("code")) {

                    Toast.makeText(this@AddGroupActivityKt, "添加分组成功", Toast.LENGTH_LONG).show()
                    val result = msg.data.getString("groupId")
                    Log.i(TAG, "返回的ID:" + result!!)
                    uploadedGroupId = result//如果新增了分组，不要忘记更新分组哦
                    savePatientGroupInfo()
                } else {
                    Toast.makeText(mContext, "create success", Toast.LENGTH_SHORT).show()
                }
            //修改用户分组信息
                1 -> {
                    closeLoadingDialog()
                    if ("0" == msg.data.getString("code")) {
                        Toast.makeText(mContext, "修改分组成功", Toast.LENGTH_SHORT).show()
                        groupName = newGroupName
                        groupId = newGroupId
                        setResultAndBack()
                    } else {
                        Toast.makeText(mContext, "修改分组失败，请重试", Toast.LENGTH_SHORT).show()
                    }
                }
                else -> {
                }
            }

        }
    }

    /**
     * 新增标签一栏中，标记第一次点击的项
     */
    private var clickedSelectTag: View? = null

    /**
     * 所有标签一栏中被选中的标签，标签位置和标签View对象
     */
    private var selectedInAllMap = HashMap<Int, View>()
    /**
     * 所有标签一栏中的项添加到新增一栏中后，前后者对应的位置
     */
    private var allToCreateMap = HashMap<Int, View>()
    private var createToAllMap = HashMap<View, Int>()

    /**
     * 新上传的分组的id
     */
    private var uploadedGroupId: String? = null

    /**
     * 获取当前新增一栏中所有标签的id
     *
     * @return 通过逗号隔开的标签的id组成的字符串
     */
    private//如果有新上传的标签则接上
    val newGroupId: String?
        get() {
            var groupId: String? = ""
            if (selectedInAllMap.size != 0) {
                for (position in selectedInAllMap.keys) {
                    Log.i(TAG, "有新增标签是从所有标签中选出的,位置为：" + position)
                    val temp = groupList[position]
                    if ("" != groupId)
                        groupId += "," + temp.getGroupId()
                    else {
                        groupId = temp.getGroupId()
                    }
                }
            }
            if (!TextUtils.isEmpty(uploadedGroupId)) {
                if ("" != groupId)
                    groupId += "," + uploadedGroupId!!
                else {
                    groupId = uploadedGroupId
                }
            }
            return groupId
        }

    /**
     * 获取当前新增一栏中所有标签名
     *
     * @return 通过逗号隔开的标签的名字组成的字符串
     */
    private//减去最后一个编辑框
    val newGroupName: String
        get() {
            var groupName = ""
            for (i in 0 until llSelectTag!!.childCount - 1) {
                if ("" == groupName)
                    groupName = (llSelectTag!!.getChildAt(i) as TextView).text.toString()
                else
                    groupName += "," + (llSelectTag!!.getChildAt(i) as TextView).text.toString()
            }
            return groupName
        }

    /**
     * 获取所有手动输入的标签的名字
     *
     * @return 通过逗号隔开的标签的名字组成的字符串，没有返回""
     */
    private//减去最后一个编辑框
    val groupNameFromEdit: String
        get() {
            var groupName = ""
            for (v in tagsFromEdit) {
                if ("" == groupName)
                    groupName = (v as TextView).text.toString()
                else
                    groupName += "," + (v as TextView).text.toString()
            }
            return groupName
        }

    override fun init() {
        setStatusBarViewOptions(true, resources.getColor(R.color.app_main_green), false)
    }

    override fun setLayout(): Int {
        return R.layout.activity_add_group
    }

    /**
     * 初始化页面
     */
    override fun initView() {
        // 顶部bar
        mTitleText = findViewById<View>(R.id.tv_title_center) as TextView
        mTitleText.text = "添加分组"
        mLeftBtn = findViewById<View>(R.id.iv_tools_left) as Button
        mLeftBtn.setOnClickListener(this)
        mRightBtn = findViewById<View>(R.id.iv_tools_right) as Button
        mRightBtn.setOnClickListener(this)
        mRightBtn.text = "保存"
        mRightBtn.visibility = View.VISIBLE

        llSelectTag = findViewById<View>(R.id.ll_group_selected) as MultipleLinearLayout
        llSelectTag?.mOnChildViewAreaListener = this//设置多行LinearLayout的子视图区间点击监听器
        llAllTag = findViewById<View>(R.id.ll_group_all) as MultiLineLinearLayout

        etTag = findViewById<View>(R.id.edit_label) as EditText
        initData()
    }

    /**
     * 初始化数据
     */
    private fun initData() {
        val intent = this.intent
        groupList = intent.getSerializableExtra("groupList") as List<Group>
        intentStr = intent.getStringExtra("intent")

        groupName = intent.getStringExtra("groupName")
        groupId = intent.getStringExtra("groupId")
        userId = intent.getStringExtra("userId")//这个大家自己传
        Log.i(TAG, "从上一个界面接收到的分组：$groupName\n分组ID：$groupId\n患者UserID:$userId")
        val groupIdForMatch = ",$groupId,"
        var tempGroupName: String
        for (i in groupList.indices) {
            tempGroupName = groupList[i].getGroupName()
            val et = TextView(this)
            et.setSingleLine()
            et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            et.setBackgroundResource(R.drawable.ic_tag_all_bg)
            et.setPadding(etTag!!.paddingLeft, etTag!!.paddingTop, etTag!!.paddingRight, etTag!!.paddingBottom)
            et.text = tempGroupName

            val patternStr = "," + groupList[i].getGroupId() + ","
            Log.i(TAG, "要匹配的pattern:$patternStr-groupId:$groupIdForMatch")
            if (groupIdForMatch.contains(patternStr)) {//匹配则说明全部标签一栏中此View要添加到新增一栏
                et.setTextColor(resources.getColor(R.color.app_main_green))
                et.background.level = 1
                val newView = addTag(tempGroupName)
                allToCreateMap.put(i, newView)
                createToAllMap.put(newView, i)
                selectedInAllMap.put(i, et)
            } else {
                et.setTextColor(resources.getColor(R.color.text_black))
            }

            et.id = 0
            if ("createGroup" != intentStr) {
                //                Log.i(TAG, "intentStr:"+intentStr);
                et.setOnClickListener(this)
            }
            llAllTag?.addView(et)
        }

    }

    /**
     * 按钮事件
     *
     * @param v
     */
    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.iv_tools_left -> setResultAndBack()
            R.id.iv_tools_right ->
                //如果有新创建的分组，先将其上传到后台
                if ("" != groupNameFromEdit) {
                    showLoadingDialog("", "正在上传新分组...", false)
                    Log.i(TAG, "onClick() 新的标签：$newGroupName-要上传的标签：$groupNameFromEdit")
                    //                    Http_addGroup_Event event = new Http_addGroup_Event(getGroupNameFromEdit(), MyDataUtil.getDocId(mContext));

                    //这里模仿请求网络，让后台保存新添加的分组标签名，并且返回他们对应的ID数组。
                    mLoadingDialog = ProgressDialog.show(mContext, "", "正在保存新分组", true)
                    val msg = Message()
                    msg.what = 0
                    val bundle = Bundle()
                    bundle.putString("code", "0")
                    bundle.putString("groupId", "6,7")//返回数据的格式，这里写死了，如果是自己的后台就要返回自己新增分组后分组的ID，有几个就返回几个
                    msg.data = bundle
                    mHandler.sendMessage(msg)

                } else {//如果没有要创建的分组，则直接保存用户的分组信息
                    savePatientGroupInfo()
                }
            -1//新增标签一栏中子视图点击事件处理
            -> for (i in 0 until llSelectTag!!.childCount) {
                val view = llSelectTag!!.getChildAt(i)
                if (v == view) {
                    if (v === clickedSelectTag) {//如果当前按到的组件跟上次按的是同一个
                        llSelectTag!!.removeViewAt(i)
                        tagsFromEdit.remove(view)
                        val num = createToAllMap[view]
                        if (num != null) {
//                        if ((num = createToAllMap[view]) != null) {
                            createToAllMap.remove(view)
                            val temp = llAllTag!!.getChildAt(num!!)
                            temp.background.level = 0
                            (temp as TextView).setTextColor(resources.getColor(R.color.text_black))
                            selectedInAllMap.remove(num)
                            allToCreateMap.remove(num)
                        }
                        //                            tagList.remove(i);
                        clickedSelectTag = null
                    } else {
                        if (clickedSelectTag == null) {//第一次选择
                        } else {//第二次选择，上次按过不同的一个
                            val text = clickedSelectTag as TextView?
                            text!!.setTextColor(resources.getColor(R.color.app_main_green))
                            text.background.level = 0
                        }
                        view.background.level = 1//选中状态：1
                        (view as TextView).setTextColor(resources.getColor(R.color.app_text_white_color))
                        clickedSelectTag = llSelectTag!!.getChildAt(i)
                    }
                    return
                }
            }
            0//全部标签一栏中子视图点击事件处理
            -> for (i in 0 until llAllTag!!.childCount) {
                val view = llAllTag!!.getChildAt(i)
                if (v == view) {
                    for (position in selectedInAllMap.keys) {//判断点击的项是否已经点击过
                        if (position == i) {
                            val temp = selectedInAllMap[i]
                            temp?.getBackground()?.level = 0
                            (temp as TextView).setTextColor(resources.getColor(R.color.text_black))
                            //                                Log.i("ActWeChatTag", "要添加的顶对应位置：" + i + "-" + allToCreateMap.get(i));
                            createToAllMap.remove(allToCreateMap[i])
                            llSelectTag!!.removeView(allToCreateMap[i])
                            //                                tagList.remove(i);//这里暂时无法获取到达新增标签一栏中的子视图对应位置
                            selectedInAllMap.remove(i)
                            allToCreateMap.remove(i)
                            return
                        }
                    }
                    view.background.level = 1
                    (view as TextView).setTextColor(resources.getColor(R.color.app_main_green))
                    val newView = addTag(view.text.toString())
                    allToCreateMap.put(i, newView)
                    createToAllMap.put(newView, i)
                    selectedInAllMap.put(i, view)
                    //                        Log.i("ActWeChatTag", "要添加的顶对应位置："+i+"-");
                    return
                }
            }
        }
    }

    /**
     * 返回键监听
     *
     * @param keyCode
     * @param event
     * @return
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
            setResultAndBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * 在新增标签一栏中添加一个标签
     *
     * @param contain 标签内容
     * @return 返回新增标签对象
     */
    private fun addTag(contain: String): View {
        val textView = TextView(this)
        textView.setSingleLine()
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
        textView.setBackgroundResource(R.drawable.ic_tag_new_bg)
        textView.setPadding(etTag!!.paddingLeft, etTag!!.paddingTop, etTag!!.paddingRight, etTag!!.paddingBottom)

        textView.text = contain
        textView.setTextColor(resources.getColor(R.color.app_main_green))
        textView.setOnClickListener(this)
        val positionToAdd = llSelectTag!!.childCount - 1
        llSelectTag!!.addView(textView, positionToAdd)

        return textView
    }

    override fun onChildViewAreaOut() {
        if (!TextUtils.isEmpty(etTag!!.text.toString())) {
            tagsFromEdit.add(addTag(etTag!!.text.toString()))
            etTag!!.setText("")
        }
    }

    /**
     * 更新患者所属分组
     */
    private fun savePatientGroupInfo() {
        showLoadingDialog("", "正在保存分组...", false)
        if (TextUtils.isEmpty(userId)) {//如果没有患者ID则直接保存当前界面的分组信息并返回给上个界面
            Toast.makeText(mContext, "修改分组成功", Toast.LENGTH_SHORT).show()
            groupName = newGroupName
            groupId = newGroupId
            closeLoadingDialog()
            setResultAndBack()
        } else {
            val testid = newGroupId
            Log.i(TAG, "更新患者分组信息groupId：$testid-uploadedGroupId:$uploadedGroupId")

            updateGroup(testid)
        }
    }

    /**
     * 模仿网络请求保存用户的分组信息
     *
     * @param groupIds 分组标签ID
     */
    private fun updateGroup(groupIds: String?) {
        val msg = Message()
        msg.what = 1
        val bundle = Bundle()
        bundle.putString("code", "0")
        msg.data = bundle
        mHandler.sendMessage(msg)
    }

    /**
     * 将当前界面的分组名和分组ID保存在Result传回上个Activity，并finish界面
     */
    private fun setResultAndBack() {
        val intent = Intent()
        intent.putExtra("groupName", groupName)
        intent.putExtra("groupId", groupId)
        setResult(0, intent)

        finish()
    }
}
