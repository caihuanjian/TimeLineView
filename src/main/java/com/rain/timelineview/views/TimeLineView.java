package com.rain.timelineview.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HwanJ.Choi on 2018-5-25.
 */

public class TimeLineView extends ViewGroup implements OnSelectChangedListener {

    public static final int MODE_SINGLE_CHOOSE = 0;
    public static final int MODE_MULTI_CHOOSE = 1;

    private static final int DEFAULT_DOT_STROKE_WIDTH = 20;
    private static final int DEFAULT_TEXT_TOP_MARGIN = 0;
    private final static int MIN_SPACE = 120;
    private static final int DEFAULT_TEXT_SIZE = 26;
    private int mMode = MODE_SINGLE_CHOOSE;
    private List<DotView> mDotViews;

    private int mDotInsideColor;
    private int mLineColor;
    private int mDotSelectColor;

    private int mTextColor;
    private int mTextSize = DEFAULT_TEXT_SIZE;
    private int textTopMargin = DEFAULT_TEXT_TOP_MARGIN;

    private int dotStrokeWidth = DEFAULT_DOT_STROKE_WIDTH;
    private int dotRadius = dotStrokeWidth * 2;
    private int dotSize = (int) (dotRadius * 1.1f) * 2;

    private View mLineView;
    private int mLineStrokeWidth = DEFAULT_DOT_STROKE_WIDTH;
    private int count;
    private int mSpace = MIN_SPACE;

    public TimeLineView(@NonNull Context context) {
        this(context, null);
    }

    public TimeLineView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeLineView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        mTextColor = Color.parseColor("#373738");
        mDotSelectColor = Color.parseColor("#2777d0");
        mDotInsideColor = Color.WHITE;
        mLineColor = Color.parseColor("#6c6964");
        mLineColor = Color.parseColor("#6c6964");
        mLineView = new View(context);
        mLineView.setBackgroundColor(mLineColor);
        addView(mLineView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;
        View child;
        for (int i = 1; i < getChildCount(); i++) {//第一个view是条线，不测
            child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            int temp = child.getMeasuredHeight();
            height = height > temp ? height : temp;
        }
        View firstChild = mDotViews.get(0);
        View lastChild = mDotViews.get(mDotViews.size() - 1);
        int width = mSpace * (count - 1) + firstChild.getMeasuredWidth() / 2 + lastChild.getMeasuredWidth() / 2 + getPaddingLeft() + getPaddingRight();
        setMeasuredDimension(width, height);
        Log.d("chj", "width:" + width + ",height:" + height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int start = getChildAt(1).getMeasuredWidth() / 2;
        int end = w - getChildAt(getChildCount() - 1).getMeasuredWidth() / 2;
        MarginLayoutParams layoutParams = new MarginLayoutParams(end - start, mLineStrokeWidth);
        layoutParams.leftMargin = start;
        mLineView.setLayoutParams(layoutParams);
    }

    public void initData(List<String> data) {
        mDotViews = new ArrayList<>();
        count = data.size();
        DotView dotView;
        for (int i = 0; i < count; i++) {
            dotView = new DotView(getContext());
            dotView.setText(data.get(i));
            addView(dotView);
            dotView.setOnSelectChangedListener(this);
            mDotViews.add(dotView);
        }
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int parentLeft = getPaddingLeft();
        int parentTop = getPaddingTop();
        int childCount = getChildCount();
        int dotMarginLeft = 0;
        View child;
        int lastChildWidth = 0;
        int confirm = parentLeft;
        for (int i = 0; i < childCount; i++) {
            child = getChildAt(i);
            if (child.getVisibility() == View.GONE)
                continue;
            int childLeft;
            int childTop;
            int childRight;
            int childBottom;
            if (i == 0) {
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                childLeft = parentLeft + lp.leftMargin;
                childTop = parentTop + dotSize / 2 - mLineStrokeWidth / 2;
                childRight = childLeft + child.getLayoutParams().width;
                childBottom = childTop + child.getLayoutParams().height;
            } else {
                childLeft = confirm + caculateMargin(lastChildWidth, child.getMeasuredWidth());
                childTop = parentTop;
                childRight = childLeft + child.getMeasuredWidth();
                childBottom = childTop + child.getMeasuredHeight();
                lastChildWidth = child.getMeasuredWidth();
                confirm = childRight;
            }
            Log.d("chj", "left:" + childLeft + "top:" + childTop + ",right:" + childRight + ",bottom:" + bottom + "margin:" + dotMarginLeft);
            Log.d("chj", "confirm" + confirm);
            child.layout(childLeft, childTop, childRight, childBottom);
        }
    }

    private int caculateMargin(int lastChildWidth, int curChildWidth) {
        if (lastChildWidth == 0) {
            return 0;
        }
        return (int) (mSpace - lastChildWidth * 1.0f / 2 - curChildWidth * 1.0f / 2);
    }

    public void setDotSelectColor(int color) {
        mDotSelectColor = color;
        invalidate();
    }

    public void setLineColor(int color) {
        mLineColor = color;
        invalidate();
    }

    public void setmTextSize(int size) {
        mTextSize = size;
        invalidate();
    }

    public void setTextColor(int color) {
        mTextColor = color;
        invalidate();
    }

    public void setMode(int mode) {
        mMode = mode;
    }

    public void selectByIndex(int index) {
        if (mDotViews == null || mDotViews.size() == 0 || index < 0 || index > mDotViews.size()) {
            return;
        }
        if (mMode == MODE_SINGLE_CHOOSE) {
            clearSelectStatus();
        }
        mDotViews.get(index).select(true);
    }

    public boolean isDotSelect(int index) {
        if (mDotViews == null || mDotViews.size() == 0 || index < 0 || index > mDotViews.size()) {
            return false;
        }
        return mDotViews.get(index).isSelected();
    }

    public List<Integer> getSelectIndex() {
        List<Integer> result = new ArrayList<>();
        if (mDotViews == null || mDotViews.isEmpty())
            return result;
        for (int i = 0; i < mDotViews.size(); i++) {
            if (!isDotSelect(i))
                continue;
            result.add(i);
        }
        return result;
    }

    private void clearSelectStatus() {
        for (DotView dotView : mDotViews) {
            dotView.setSelected(false);
        }
    }

    private DotView mLastSelectDot;

    @Override
    public void onSelectChange(DotView dotView, boolean isSelect) {
        if (mMode == MODE_SINGLE_CHOOSE) {
            if (mLastSelectDot != null && mLastSelectDot != dotView)
                mLastSelectDot.setSelected(false);
        }
        if (mResultListener != null)
            mResultListener.onSelectResult(getSelectIndex());
        mLastSelectDot = dotView;
    }

    class DotView extends View implements View.OnClickListener {

        private IndicateDrawable mIndicate;
        private int textAreaHeight;
        private Paint mPaint;
        private Paint mTextPaint;
        private int centerX;
        private int centerY;
        private String mText;
        private OnSelectChangedListener mOnSelectChangedListener;

        public void setOnSelectChangedListener(OnSelectChangedListener listener) {
            this.mOnSelectChangedListener = listener;
        }

        public DotView(Context context) {
            this(context, null);
        }

        public DotView(Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);

        }

        public DotView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init(context, attrs, defStyleAttr);
        }

        private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {

            mIndicate = new IndicateDrawable(this, mDotSelectColor);
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint.setColor(mTextColor);
            mTextPaint.setTextSize(mTextSize);
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            setOnClickListener(this);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            boolean selected = isSelected();
            if (selected) {
                mIndicate.draw(canvas);
            } else {
                mPaint.setColor(mLineColor);
                canvas.drawCircle(centerX, centerY, dotRadius, mPaint);
                mPaint.setColor(mDotInsideColor);
                canvas.drawCircle(centerX, centerY, dotRadius - dotStrokeWidth, mPaint);
            }
            drawText(canvas);
        }

        private void drawText(Canvas canvas) {
            canvas.drawText(mText, getWidth() / 2, dotSize + textAreaHeight + textTopMargin, mTextPaint);
        }

        void select(boolean check) {
            setSelected(check);
            if (mOnSelectChangedListener != null)
                mOnSelectChangedListener.onSelectChange(this, check);
        }

        void setText(String text) {
            mText = text;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            float textWidth = mTextPaint.measureText(mText);
            textAreaHeight = Math.abs((int) (mTextPaint.getFontMetrics().descent - mTextPaint.getFontMetrics().ascent));
            float height = textAreaHeight + textTopMargin + dotSize;
            setMeasuredDimension((int) Math.max(dotSize, textWidth), (int) height);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            Log.d("chj", dotStrokeWidth + "," + dotRadius + "," + dotSize);
            centerX = w / 2;
            centerY = dotSize / 2;
            mIndicate.setBounds(centerX - dotSize / 2, centerY - dotSize / 2, centerX + dotSize / 2, centerY + dotSize / 2);
        }

        @Override
        public void onClick(View v) {
            boolean selected = isSelected();
            select(!selected);
            if (!selected) {//非选时点击
                mIndicate.setupAnimation();
            }
            invalidate();
        }
    }

    static class IndicateDrawable extends Drawable {

        private int size;
        private Paint mPaint;
        private View mTargetView;
        private float curRadius;

        IndicateDrawable(View targetView, int color) {
            mTargetView = targetView;
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(color);
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            if (bounds == null)
                return;
            size = Math.min(bounds.width(), bounds.height());
            curRadius = size / 2;
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            canvas.drawCircle(size / 2, size / 2, curRadius, mPaint);
        }

        @Override
        public void setAlpha(int alpha) {
            mPaint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            mPaint.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSPARENT;
        }

        private void setupAnimation() {
            ValueAnimator radiusAnimator = ValueAnimator.ofFloat(size / 4, size / 2);
            radiusAnimator.setDuration(400);
            radiusAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    curRadius = (float) animation.getAnimatedValue();
                    mTargetView.invalidate();
                }
            });
            radiusAnimator.start();
        }
    }

    private OnSelectResultListener mResultListener;

    public void setOnSelectResultListener(OnSelectResultListener listener) {
        mResultListener = listener;
    }

    public interface OnSelectResultListener {
        void onSelectResult(List<Integer> selections);
    }
}
