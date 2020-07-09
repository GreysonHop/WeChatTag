package com.study.wechattag.activity

import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.TextView
import com.study.wechattag.R
import com.study.wechattag.model.Group
import java.io.Serializable
import java.util.*

/**
 * Created by Greyson on 2016/4/26.
 */
class MainActivityKt : MyBaseActivity(), View.OnClickListener {
    private var tvGroup: TextView? = null
    private var groupId: String? = null
    private var groupName: String? = null
    //这里是写死数据，所以就算“添加分组”界面新增了分组也不会刷新此分组！
    // 但在自己的项目，如果新增了分组，不要忘记更新分组哦！
    private val groupList = ArrayList<Group>()

    override fun setLayout(): Int {
        return R.layout.act_main
    }

    override fun initView() {
        super.initView()

        tvGroup = findViewById<View>(R.id.tv_group) as TextView
        tvGroup!!.setOnClickListener(this)
        initData()

        val builder = AlertDialog.Builder(this)
                .setTitle("注意！")
                .setMessage("目前此项目流程只能执行一次！即进入添加分组界面后，里面新创建的分组不会更新到当前的所有分组列表中！")
                .setCancelable(false)
                .setPositiveButton("确定") { dialog, which -> dialog.dismiss() }
        //        builder.show();
    }

    //初始化一些假数据
    private fun initData() {
        groupId = "1,2,3"
        groupName = "11,22,33"
        var group: Group
        for (i in 0..4) {
            group = Group()
            group.setGroupId(i.toString() + "")
            group.setGroupName(i.toString() + "" + i)
            groupList.add(group)
        }
        tvGroup!!.text = groupName
    }

    override fun onClick(v: View) {
        if (v.id == R.id.gotoAct) {
            startActivity(Intent(this, TestThemeActivityKt::class.java))
            return
        }

        val intent = Intent()
        when (v.id) {
            R.id.tv_group//分组标签
            -> {
                intent.setClass(this, AddGroupActivityKt::class.java)
                intent.putExtra("userId", "101")//这里随便传的，大家传自己要更新分组信息的用户ID
                intent.putExtra("groupId", groupId)//传入原来拥有的标签ID
                intent.putExtra("groupName", groupName)//传入原本拥有的标签名
                // intent.putExtra("groupList", groupList as Serializable)
                intent.putParcelableArrayListExtra("groupList", groupList)
                startActivityForResult(intent, 0)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            data?.let {
                val name = it.getStringExtra("groupName")
                val id = it.getStringExtra("groupId")
                Log.i("MainActivity", "groupId$id- groupName$name")
                tvGroup!!.text = name
                groupId = id
                groupName = name
            }
        }
    }
}
