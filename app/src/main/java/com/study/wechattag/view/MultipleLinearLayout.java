package com.study.wechattag.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Greyson on 2016/4/9.
 */
public class MultipleLinearLayout extends ViewGroup {
    final String TAG = "MultipleLinearLayout";

    public MultipleLinearLayout(Context context) {
        super(context);
    }

    public MultipleLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultipleLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mWidth = MeasureSpec.getSize(widthMeasureSpec);//自定义组件的宽
        int nextX = 0;//下一个子组件的左上角X坐标
        int currentHeight = 0;//所有子视图总共需要的高度，不包括当前ViewGroup的padding
        int currentWidth = 0; //所有子视图所能占用的宽度，不包括当前ViewGroup的padding
        int lineNum = 0;
        int count = getChildCount();

        //当前View的padding
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        currentWidth = mWidth - paddingLeft - paddingRight;

        // 设置子空间Child的宽高
        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);
//            measureChildWithMargins(childView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            childView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

            //子View的margin
            int marginLeft = 0;
            int marginTop = 0;
            int marginRight = 0;
            int marginBottom = 0;
            if (childView.getLayoutParams() instanceof MarginLayoutParams) {
                MarginLayoutParams params = (MarginLayoutParams) childView.getLayoutParams();
                marginLeft = params.leftMargin;
                marginTop = params.topMargin;
                marginRight = params.rightMargin;
                marginBottom = params.bottomMargin;
            }

            // TODO: 2018/10/31  如果一个tag的宽度就超过了当前View的宽度，如何处理？！
            if (i == 0) {
                currentHeight = childView.getMeasuredHeight() + marginTop + marginBottom;
            }

            if ((marginLeft + nextX + childView.getMeasuredWidth() + marginRight) > currentWidth) {//一行子视图的长度超出父视图的长度时
                lineNum++;
                nextX = 0;
                currentHeight += marginTop + childView.getMeasuredHeight() + marginBottom;
                nextX = nextX + marginLeft + childView.getMeasuredWidth() + marginRight;
            } else {
                nextX = nextX + marginLeft + childView.getMeasuredWidth() + marginRight;
            }

        }

        setMeasuredDimension(mWidth, currentHeight + paddingTop + paddingBottom);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {//四个方向的参数是当前View相对于它的父视图的坐标
        int mWidth = r - l;//自定义组件的宽
        int nextX = 0;//下一个子组件左上点的X坐标，不包含子它的margin
        int nextY = 0;//下一个子组件左上点的Y坐标，不包含子它的margin
        int lineNum = 0;//当前行数
        int count = getChildCount();

        //当前View的padding
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        nextX += paddingLeft;
        nextY += paddingTop;
        int childViewsContentWidth = mWidth - paddingLeft - paddingRight;

        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);

            int marginLeft = 0;
            int marginTop = 0;
            int marginRight = 0;
            int marginBottom = 0;
            if (childView.getLayoutParams() instanceof MarginLayoutParams) {
                MarginLayoutParams params = (MarginLayoutParams) childView.getLayoutParams();
                marginLeft = params.leftMargin;
                marginTop = params.topMargin;
                marginRight = params.rightMargin;
                marginBottom = params.bottomMargin;
            }


            if ((nextX + marginLeft + childView.getMeasuredWidth() + marginRight) > childViewsContentWidth) {//一行子视图的长度超出父视图的长度时
                lineNum++;
                nextX = paddingLeft;
                nextY += marginTop + childView.getMeasuredHeight() + marginBottom;
                childView.layout(nextX + marginLeft, nextY + marginTop, nextX + marginLeft + childView.getMeasuredWidth(), nextY + marginTop + childView.getMeasuredHeight());
                nextX = nextX + marginLeft + childView.getMeasuredWidth() + marginRight;

            } else {
                childView.layout(nextX + marginLeft, nextY + marginTop, nextX + marginLeft + childView.getMeasuredWidth(), nextY + marginTop + childView.getMeasuredHeight());
                nextX = nextX + marginLeft + childView.getMeasuredWidth() + marginRight;
            }
        }
        hollowX = nextX;
        hollowY = nextY;

    }


    @Override
    public MarginLayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }


    /**
     * 没有被子组件占用的空间的左上角X坐标
     */
    private int hollowX = 0;
    /**
     * 没有被子组件占用的空间的左上角Y坐标
     */
    private int hollowY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent() hollowX:" + hollowX + " - Y:" + hollowY);
        if (hollowY < event.getY() && hollowX < event.getX()) {
            if (mOnChildViewAreaListener != null) {
                mOnChildViewAreaListener.onChildViewAreaOut();
            }
        }
        return super.onTouchEvent(event);
    }

    public OnChildViewAreaListener mOnChildViewAreaListener;

    public void setOnChildViewAreaListener(OnChildViewAreaListener mOnChildViewAreaListener) {
        this.mOnChildViewAreaListener = mOnChildViewAreaListener;
    }

    public interface OnChildViewAreaListener {
        /**
         * 点击未被子视图占用的区间时触发
         */
        void onChildViewAreaOut();
    }

}
