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

    private var flowTagView: FlowTagView? = null

    override fun init() {
        super.init()
        setStatusBarViewOptions(false, true, -1, true)
    }

    override fun setLayout(): Int {
        return R.layout.act_test_theme
    }

    override fun initEvent() {
        super.initEvent()

//        val tagList = Array(6, { i -> "#逗比欢乐多" + i })
        val tagList = mutableListOf<String>()
        tagList.add("#制服の诱惑")
        tagList.add("#小女妖的自拍秀")
        tagList.add("#爆笑内涵段子")
        tagList.add("#表白")
        tagList.add("#新手上路")
        tagList.add("#逗比欢乐多")
        tagList.add("#心情随笔录")

        val tagList2 = arrayListOf<String>()
        tagList2.add("#制服の诱惑")
        tagList2.add("#小女妖的自拍秀")
        tagList2.add("#爆笑内涵段子")
        tagList2.add("#表白")
        tagList2.add("#新手上路")
        tagList2.add("#逗比欢乐多")
        tagList2.add("#心情随笔录")

        flowTagView = findViewById(R.id.flowTagView)
        flowTagView?.setTagList(tagList)

        (findViewById<View>(R.id.flowTagView2) as FlowTagView).setTagList(tagList2)
    }

    override fun onClick(arg0: View?) {
        /*val list = flowTagView!!.selectedList//这种情况下flowTagView不能为空*/

        val list = flowTagView?.selectedList//这种情况下flowTagView可为空，直接返回null给list变量
        //list不为空时才执行 let 后面的方法体。
        list?.takeIf { it.size > 0 }?.let {
            printList(it)
        } ?: Toast.makeText(this, "没有选择任何项", Toast.LENGTH_SHORT).show()

        /*list?.let {
            printList(it)
//            return //退出整个onClick方法
//            return@let //只退出当前let方法体
        } ?: Toast.makeText(this, "没有选择任何项", Toast.LENGTH_SHORT).show()*/

    }

    private fun printList(list : List<String>) {
        var result = ""
        for (s in list) {
            Log.d("greyson", s)
            result += s
        }
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
    }
}