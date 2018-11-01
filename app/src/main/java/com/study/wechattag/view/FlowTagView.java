package com.study.wechattag.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.study.wechattag.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Greyson on 2018/10/29.
 */
public class FlowTagView extends MultipleLinearLayout {

    private List<String> tagList;
    private List<String> selectedTags;
    private int[] selectedPosition = new int[0];


    private int mTagResId;


    public FlowTagView(Context context) {
        this(context, null);
    }

    public FlowTagView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowTagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttrs(context, attrs, defStyleAttr);

    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
//        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlowTagView);
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlowTagView, defStyleAttr, 0);

        mTagResId = typedArray.getResourceId(R.styleable.FlowTagView_tvTagResId, 0);

        typedArray.recycle();
    }

    public List<String> getSelectedList() {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < selectedPosition.length; i++) {
            if (selectedPosition[i] == 1) {
                result.add(tagList.get(i));
            }
        }
        return result;
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
        this.removeAllViews();

        if (tagList == null || tagList.size() == 0) {
            return;
        }
        this.selectedPosition = new int[tagList.size()];

        for (int i = 0; i < tagList.size(); i++) {
            String title = tagList.get(i);
            addView(createDefaultTag(title, i));
        }

    }

    private View createDefaultTag(String contain, int position) {
        TextView textView;
        if (mTagResId != 0) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View tempView = inflater.inflate(mTagResId, this, false);

            final Resources res = getContext().getResources();
            String resourceName = res.getResourceName(mTagResId);

            if (tempView instanceof TextView) {
                textView = (TextView) tempView;
                System.out.println("greyson's params = " + tempView.getLayoutParams() + " - resourceName=" + resourceName);
            } else if (tempView instanceof ViewGroup) {
                ViewGroup tempGroup = (ViewGroup) tempView;
                if (tempGroup.getChildCount() == 1 && tempGroup.getChildAt(0) instanceof TextView) {
                    textView = (TextView) tempGroup.getChildAt(0);
                } else {
                    throw new RuntimeException("the ViewGroup of TagResId has child view more than one, or has a only child view that is not TextView!");
                }
            } else {
                throw new RuntimeException("the root view of the " + resourceName + " is not TextView");
            }

        } else {
            textView = new TextView(getContext());
            textView.setSingleLine();
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            textView.setBackgroundResource(R.drawable.ic_tag_all_bg);

            textView.setPadding(transfer(TypedValue.COMPLEX_UNIT_DIP, 10)
                    , transfer(TypedValue.COMPLEX_UNIT_DIP, 5)
                    , transfer(TypedValue.COMPLEX_UNIT_DIP, 10)
                    , transfer(TypedValue.COMPLEX_UNIT_DIP, 5));

            MarginLayoutParams params = new MarginLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            params.rightMargin = transfer(TypedValue.COMPLEX_UNIT_DIP, 10);
            params.bottomMargin = transfer(TypedValue.COMPLEX_UNIT_DIP, 10);
            textView.setLayoutParams(params);
        }

        textView.setText(contain);
        textView.setTag(position);

        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick(view, (Integer) view.getTag());
            }
        });

        return textView;
    }

    private void onItemClick(View view, int position) {
        if (selectedPosition.length < position) {
            return;
        }

        if (selectedPosition[position] == 1) {
            view.getBackground().setLevel(0);
            view.setSelected(false);
            selectedPosition[position] = 0;
        } else {
            view.getBackground().setLevel(1);
            view.setSelected(true);
            selectedPosition[position] = 1;
        }
    }

    private int transfer(int unit, float value) {
        return (int) TypedValue.applyDimension(unit, value, getResources().getDisplayMetrics());
    }
}
