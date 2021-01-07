package com.yxj.mylibrary;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Yxj(yanxujun @ dxy.cn)
 * @CreateDate: 2021/1/5 2:36 PM
 * @Description:
 */
public class SwitchButton extends FrameLayout {

    int mItemWidth = 0;
    int mItemHeight = 0;
    int mItemPadding = 0;
    int mRadius = 0;
    int mBackgroundColor = 0;
    int mSelectBackgroundColor = 0;
    int mTextSize = 0;
    int mTextColor = 0;
    int mSelectTextColor = 0;
    boolean mSelectTextBold = false;
    CharSequence[] mMenuArray;

    int mSelectedIndex = 0;
    List<TextView> mTextViewList = new ArrayList<TextView>();
    private View mMoveView;

    public SwitchButton(@NonNull Context context) {
        this(context, null);
    }

    public SwitchButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        init(context);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwitchButton);
        mItemWidth = typedArray.getDimensionPixelOffset(R.styleable.SwitchButton_item_width, 0);
        mItemHeight = typedArray.getDimensionPixelOffset(R.styleable.SwitchButton_item_height, 0);
        mItemPadding = typedArray.getDimensionPixelOffset(R.styleable.SwitchButton_item_padding, 0);
        mRadius = typedArray.getDimensionPixelOffset(R.styleable.SwitchButton_radius, 0);
        mBackgroundColor = typedArray.getColor(R.styleable.SwitchButton_background_color, R.color.color_999999);
        mSelectBackgroundColor = typedArray.getColor(R.styleable.SwitchButton_select_background_color, R.color.color_666666);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.SwitchButton_text_size, 0);
        mTextColor = typedArray.getColor(R.styleable.SwitchButton_text_color, R.color.black);
        mSelectTextColor = typedArray.getColor(R.styleable.SwitchButton_text_selected_color, R.color.white);
        mSelectTextBold = typedArray.getBoolean(R.styleable.SwitchButton_text_selected_bold, false);
        mMenuArray = typedArray.getTextArray(R.styleable.SwitchButton_menu_array);

        typedArray.recycle();
    }

    private void init(Context context) {
        drawBackground();
        drawSelectedTag(context);
        drawWords(context);
    }

    private void drawBackground() {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(mRadius + 2 * mItemPadding);
        gradientDrawable.setColor(mBackgroundColor);
        this.setBackground(gradientDrawable);
    }

    private void drawSelectedTag(Context context) {
        mMoveView = new View(context);
        FrameLayout.LayoutParams moveParams = new FrameLayout.LayoutParams(mItemWidth - dp2px(2), mItemHeight - dp2px(2));
        mMoveView.setLayoutParams(moveParams);
        addView(mMoveView);
        GradientDrawable moveDrawable = new GradientDrawable();
        moveDrawable.setCornerRadius(mRadius);
        moveDrawable.setColor(mSelectBackgroundColor);
        mMoveView.setBackground(moveDrawable);
        mMoveView.setTranslationX(mSelectedIndex * mItemWidth);
    }

    private void drawWords(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        LayoutParams layoutParams = new LayoutParams(mItemWidth * mMenuArray.length + 2 * mItemPadding, mItemHeight + 2 * mItemPadding);
        linearLayout.setLayoutParams(layoutParams);
        addView(linearLayout);

        for (int i = 0; i < mMenuArray.length; i++) {
            final int index = i;
            TextView tv = new TextView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mItemWidth, mItemHeight);
            if (i == 0) {
                params.leftMargin = mItemPadding;
            }
            params.topMargin = mItemPadding;

            tv.setLayoutParams(params);
            tv.setText(mMenuArray[i]);
            tv.setGravity(Gravity.CENTER);

            if (i == mSelectedIndex) {
                tv.setTextColor(mSelectTextColor);
            } else {
                tv.setTextColor(mTextColor);
            }
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            tv.setBackgroundColor(Color.TRANSPARENT);
            linearLayout.addView(tv);

            mTextViewList.add(tv);

            tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectedIndex != index) {
                        mSelectedIndex = index;
                        final int currentTranslationX = (int) mMoveView.getTranslationX();
                        ValueAnimator animator = ValueAnimator.ofFloat(0, 1f);
                        animator.setDuration(300);
                        animator.setInterpolator(new DecelerateInterpolator());
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float fraction = animation.getAnimatedFraction();
                                int targetTranslationX = mSelectedIndex * mItemWidth;
                                int deltaX = targetTranslationX - currentTranslationX;
                                mMoveView.setTranslationX(currentTranslationX + deltaX * fraction);
                            }
                        });
                        animator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                renderTag();
                                renderWords();
                            }
                        });
                        animator.start();
                    }
                }
            });
        }
    }

    private void renderTag() {
        mMoveView.setTranslationX(mSelectedIndex * mItemWidth);
    }

    private void renderWords() {
        for (int i = 0; i < mTextViewList.size(); i++) {
            if (i == mSelectedIndex) {
                mTextViewList.get(i).setTextColor(mSelectTextColor);
            } else {
                mTextViewList.get(i).setTextColor(mTextColor);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mMoveView.layout(mItemPadding, mItemPadding, mItemWidth + mItemPadding, mItemHeight + mItemPadding);
    }

    private int dp2px(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(displayMetrics.density * dp + 0.5f);
    }
}
