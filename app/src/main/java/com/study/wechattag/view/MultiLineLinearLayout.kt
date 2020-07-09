package com.study.wechattag.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup

/**
 * Created by Greyson on 2018/11/25.
 */
class MultiLineLinearLayout : ViewGroup {

    private val TAG = "MultiLineLinearLayout"

    constructor(cxt: Context) : super(cxt)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        var nextX = 0
        var currentHeight = 0
        val currentWidth: Int
        val count = this.childCount

        //当前View的padding
        val paddingLeft = this.paddingLeft
        val paddingTop = this.paddingTop
        val paddingRight = this.paddingRight
        val paddingBottom = this.paddingBottom
        currentWidth = width - paddingLeft - paddingRight

        // 设置子空间Child的宽高
        for (i in 0 until count) {
            val childView = getChildAt(i)
            childView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

            //子View的margin
            var marginLeft = 0
            var marginTop = 0
            var marginRight = 0
            var marginBottom = 0
            if (childView.layoutParams is MarginLayoutParams) {
                val params = childView.layoutParams as MarginLayoutParams
                marginLeft = params.leftMargin
                marginTop = params.topMargin
                marginRight = params.rightMargin
                marginBottom = params.bottomMargin
            }

            if (i == 0) {
                currentHeight = childView.measuredHeight + marginTop + marginBottom
            }

            if (marginLeft + nextX + childView.measuredWidth + marginRight > currentWidth) {//一行子视图的长度超出父视图的长度时
                nextX = 0
                currentHeight += marginTop + childView.measuredHeight + marginBottom
                nextX += marginLeft + childView.measuredWidth + marginRight
            } else {
                nextX += marginLeft + childView.measuredWidth + marginRight
            }

        }

        setMeasuredDimension(width, currentHeight + paddingTop + paddingBottom)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width = r - l
        var nextX = 0
        var nextY = 0
        val count = this.childCount

        //当前View的padding
        val paddingLeft = this.paddingLeft
        val paddingTop = this.paddingTop
        val paddingRight = this.paddingRight
        nextX += paddingLeft
        nextY += paddingTop
        val childViewsContentWidth = width - paddingLeft - paddingRight

        for (i in 0 until count) {
            val childView = getChildAt(i)

            var marginLeft = 0
            var marginTop = 0
            var marginRight = 0
            var marginBottom = 0
            val layoutParams = childView.layoutParams
            if (layoutParams is MarginLayoutParams) {
                marginLeft = layoutParams.leftMargin
                marginTop = layoutParams.topMargin
                marginRight = layoutParams.rightMargin
                marginBottom = layoutParams.bottomMargin
            }

            if ((nextX + marginLeft + childView.measuredWidth + marginRight) > childViewsContentWidth) {//一行子视图的长度超出父视图的长度时
                nextX = paddingLeft
                nextY += marginTop + childView.measuredHeight + marginBottom
                childView.layout(nextX + marginLeft, nextY + marginTop, nextX + marginLeft + childView.measuredWidth, nextY + marginTop + childView.measuredHeight)
                nextX += marginLeft + childView.measuredWidth + marginRight
            } else {
                childView.layout(nextX + marginLeft, nextY + marginTop, nextX + marginLeft + childView.measuredWidth, nextY + marginTop + childView.measuredHeight)
                nextX += marginLeft + childView.measuredWidth + marginRight
            }

        }
        hollowX = nextX
        hollowY = nextY
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    /**
     * 没有被子组件占用的空间的左上角X坐标
     */
    private var hollowX = 0
    /**
     * 没有被子组件占用的空间的左上角Y坐标
     */
    private var hollowY = 0

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.i(TAG, "onTouchEvent() hollowX:$hollowX - Y:$hollowY")
        if (hollowY < event.y && hollowX < event.x) {
            mOnChildViewAreaListener?.onChildViewAreaOut()
        }
        return super.onTouchEvent(event)
    }

    var mOnChildViewAreaListener: OnChildViewAreaListener? = null

    interface OnChildViewAreaListener {
        /**
         * 点击未被子视图占用的区间时触发
         */
        fun onChildViewAreaOut()
    }

}