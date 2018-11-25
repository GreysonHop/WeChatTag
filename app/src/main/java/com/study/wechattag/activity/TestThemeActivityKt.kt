package com.study.wechattag.activity

import android.util.Log
import android.view.View
import android.widget.Toast
import com.study.wechattag.R
import com.study.wechattag.view.FlowTagView

/**
 * Created by Greyson on 2018/11/25.
 */
class TestThemeActivityKt : MyBaseActivity() {

    var flowTagView: FlowTagView? = null

    override fun init() {
        super.init()
        setStatusBarViewOptions(false, true, -1, true)
    }

    override fun setLayout(): Int {
        return R.layout.act_test_theme
    }

    override fun initEvent() {
        super.initEvent()

        val tagList = Array(6, {
            "#制服の诱惑"
            "#小女妖的自拍秀"
            "#爆笑内涵段子"
            "#表白"
            "#新手上路"
            "#逗比欢乐多"
        })
        tagList.plus("#心情随笔录")

        flowTagView = findViewById(R.id.flowTagView)
        flowTagView?.setTagList(tagList.asList())

        (findViewById<View>(R.id.flowTagView2) as FlowTagView).setTagList(tagList.toList())
    }

    override fun onClick(arg0: View?) {
        val list = flowTagView!!.selectedList
        var result = ""
        for (s in list) {
            Log.d("greyson", s)
            result += s
        }
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
    }
}