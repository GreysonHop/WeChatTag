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
public class MultipleLinearLayout extends ViewGroup{
    final String TAG = "MultipleLinearLayout";

    private int marginLeft = 10;
    int marginTop = 3;
    int marginRight = 10;
    int marginBottom = 3;

    public MultipleLinearLayout(Context context){
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
         int nextX = 0;//最后一个子组件的右上角X坐标
         int currentHeight = 0;//父视图的高度
        int lineNum = 0;
        int count = getChildCount();
        // 设置子空间Child的宽高
        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);
            childView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

            if(i == 0){
                currentHeight = childView.getMeasuredHeight()+marginTop+marginBottom;
            }

            if((marginLeft+nextX + childView.getMeasuredWidth()+marginRight) > mWidth){//一行子视图的长度超出父视图的长度时
                lineNum ++;
                nextX = 0;
                currentHeight = currentHeight+marginTop+marginBottom+childView.getMeasuredHeight();
                nextX = nextX+marginLeft+marginRight +childView.getMeasuredWidth();
            }else{
                nextX = nextX+ childView.getMeasuredWidth()+marginLeft+marginRight;
            }

        }
         setMeasuredDimension(mWidth, currentHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int mWidth = r;//自定义组件的宽
        int nextX = l;//下一个子组件左上点的X坐标，不包含margin
        int nextY = t;//下一个子组件左上点的Y坐标，不包含margin
        int lineNum = 0;//当前行数
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);

            if((nextX + childView.getMeasuredWidth()+marginLeft+marginRight) > mWidth){//一行子视图的长度超出父视图的长度时
                lineNum ++;
                nextX = 0;
                nextY = nextY+marginTop+ childView.getMeasuredHeight()+marginBottom;
                childView.layout(nextX+marginLeft, nextY+marginTop, nextX+marginLeft + childView.getMeasuredWidth(), nextY + marginTop + childView.getMeasuredHeight());
                nextX = nextX + marginLeft + childView.getMeasuredWidth() + marginRight;

            }else{
                childView.layout(nextX+marginLeft, nextY+marginTop, nextX+marginLeft + childView.getMeasuredWidth(), nextY + marginTop + childView.getMeasuredHeight());
                nextX = nextX + marginLeft + childView.getMeasuredWidth() + marginRight;
            }
        }
            hollowX = nextX - marginRight;
            hollowY = nextY;

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
        Log.i(TAG, "onTouchEvent() hollowX:"+hollowX+" - Y:"+hollowY);
        if(hollowY < event.getY() && hollowX < event.getX()){
            if(mOnChildViewAreaListener != null){
                mOnChildViewAreaListener.onChildViewAreaOut();
            }
        }
        return super.onTouchEvent(event);
    }

    public OnChildViewAreaListener mOnChildViewAreaListener;

    public void setOnChildViewAreaListener(OnChildViewAreaListener mOnChildViewAreaListener) {
        this.mOnChildViewAreaListener = mOnChildViewAreaListener;
    }

    public interface OnChildViewAreaListener{
        /**
         * 点击未被子视图占用的区间时触发
         */
        void onChildViewAreaOut();
    }

}
