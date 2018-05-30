package com.rain.timelineview.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by HwanJ.Choi on 2018-5-29.
 */

public class TimeSelectionBar extends View {

    private static final float LINE_HEIGHT_FACTOR = 0.6f;
    private static final int DEFAULT_HEIGHT = 80;
    private static final int DEFAULT_WIDTH = 960;

    private int mWidth;
    private int mHeight;
    private int mSliderHeight;
    private int mSliderWidth;
    private int mLineHeight;

    private int mBackgroundColor;
    private int mForegroundColor;
    private int mBarStrokeColor;
    private int mBarMainColor;

    private Paint mPaint;

    private BarDrawable mSliderLeftDrawable;
    private BarDrawable mSliderRightDrawable;
    private int mBarStrokeWidth = 2;

    private int mLeftSliderX;
    private int mRightSliderX;
    private Path mPath;
    private int roundRectWidth;

    private int effectiveProgress;//有效的进度，除去两个滑块宽度
    private int maxProgress = 100;

    public TimeSelectionBar(Context context) {
        this(context, null);
    }

    public TimeSelectionBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeSelectionBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {

        mBackgroundColor = Color.parseColor("#43a0afaf");
        mForegroundColor = Color.parseColor("#2777d0");
        mBarStrokeColor = Color.parseColor("#6c706e");
        mBarMainColor = Color.parseColor("#f3f4f1");
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = mSliderHeight = h;
        mSliderWidth = (int) (mHeight * 0.3f);
        roundRectWidth = mHeight / 2;
        mLineHeight = (int) (mHeight * LINE_HEIGHT_FACTOR);
        mLeftSliderX = 0;//左滑块的left
        mRightSliderX = mWidth - mLeftSliderX;//右滑块的right
        effectiveProgress = mWidth - 2 * mSliderWidth;

        mSliderLeftDrawable = new BarDrawable(true);
        mSliderLeftDrawable.setBounds(mLeftSliderX, 0, mSliderWidth, mSliderHeight);
        mSliderRightDrawable = new BarDrawable(false);
        mSliderRightDrawable.setBounds(mRightSliderX - mSliderWidth, 0, mRightSliderX, mSliderHeight);

        mPath = new Path();
        mPath.moveTo(roundRectWidth / 2, (mHeight - mLineHeight) / 2);
        RectF rectF = new RectF(0, (mHeight - mLineHeight) / 2, roundRectWidth, (mSliderHeight + mLineHeight) / 2);
        mPath.addArc(rectF, -90, -180);
        mPath.lineTo(mWidth - roundRectWidth / 2, (mSliderHeight + mLineHeight) / 2);
        rectF = new RectF(mWidth - roundRectWidth, (mHeight - mLineHeight) / 2, mWidth, (mSliderHeight + mLineHeight) / 2);
        mPath.addArc(rectF, 90, -180);
        mPath.lineTo(roundRectWidth / 2, (mHeight - mLineHeight) / 2);
        mPath.close();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec, true), measure(heightMeasureSpec, false));
    }

    private int measure(int spec, boolean measureWidth) {
        int defaultSize = measureWidth ? DEFAULT_WIDTH : DEFAULT_HEIGHT;
        int size = MeasureSpec.getSize(spec);
        int mode = MeasureSpec.getMode(spec);
        int result = defaultSize;
        switch (mode) {
            case MeasureSpec.EXACTLY:
                result = size;
            case MeasureSpec.AT_MOST:
                result = Math.min(result, size);
            case MeasureSpec.UNSPECIFIED:
                break;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //剪一个进度条区域
        int lineTop = (mHeight - mLineHeight) / 2;
        int lineBottom = (mSliderHeight + mLineHeight) / 2;
        canvas.save();
        mPaint.setColor(mBackgroundColor);
        canvas.clipPath(mPath);
        //画灰色部分
        canvas.drawRect(0, lineTop, mLeftSliderX, lineBottom, mPaint);
        canvas.drawRect(mRightSliderX, lineTop, mWidth, lineBottom, mPaint);
        //画中间部分
        mPaint.setColor(mForegroundColor);
        canvas.drawRect(mLeftSliderX, lineTop, mRightSliderX, lineBottom, mPaint);
        canvas.restore();
        //画两个滑块
        drawBar(canvas);
    }

    private void drawBar(Canvas canvas) {
        mSliderRightDrawable.draw(canvas);
        mSliderLeftDrawable.draw(canvas);
    }

    private int mSelectBar;// 左0右1
    private float mDownX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                float downY = event.getY();
                mSelectBar = selectWhich(mDownX, downY);
                Log.d("chj", "mSelectBar:" + mSelectBar);
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = (event.getX() - mDownX) * 0.8f;
                Log.d("chj", "deltaX:" + deltaX);
                switch (mSelectBar) {
                    case 0:
                        mLeftSliderX = (int) Math.min(Math.max(mLeftSliderX + deltaX, 0), mRightSliderX - mSliderWidth * 2);
                        mSliderLeftDrawable.updateLocation(mLeftSliderX);
                        invalidate();
                        break;
                    case 1:
                        mRightSliderX = (int) Math.min(Math.max(mRightSliderX + deltaX, mLeftSliderX + 2 * mSliderWidth), mWidth);
                        mSliderRightDrawable.updateLocation(mRightSliderX);
                        invalidate();
                        break;
                    case -1:
                    default:
                        break;
                }
                onProgressChange(mSelectBar);
                mDownX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                mSelectBar = -1;
                break;
        }
        return true;
    }

    void onProgressChange(int which) {
        if (mListener == null || which == -1)
            return;
        int leftProgress = Math.round(mLeftSliderX * 1.0f / effectiveProgress * maxProgress);
        int rightProgress = Math.round((mWidth - mRightSliderX) * 1.0f / effectiveProgress * maxProgress);
        int centerPercent = maxProgress - leftProgress - rightProgress;
        mListener.onProgressChange(leftProgress, rightProgress, centerPercent);
    }

    /**
     * @return -1,0,1分别为无选中，选左，选右
     */
    int selectWhich(float downX, float downY) {
        boolean leftBarSelect = isBarSelect(downX, downY, true);
        boolean rightBarSelect = isBarSelect(downX, downY, false);
        if (leftBarSelect && rightBarSelect) {
            return checkBest(downX, downY);
        } else if (leftBarSelect) {
            return 0;
        } else if (rightBarSelect) {
            return 1;
        }
        return -1;
    }

    int checkBest(float downX, float downY) {
        RectF barRectF = getCurrentLeftBarRectF();
        float xDistance = barRectF.centerX() - downX;
        float yDistance = barRectF.centerY() - downY;
        float leftBarDistance = (float) Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2));
        barRectF = getCurrentRightBarRectF();
        xDistance = barRectF.centerX() - downX;
        yDistance = barRectF.centerY() - downY;
        float rightBarDistance = (float) Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2));
        return leftBarDistance < rightBarDistance ? 0 : 1;
    }

    boolean isBarSelect(float downX, float downY, boolean leftBar) {
        RectF barRectF = leftBar ? getCurrentLeftBarRectF() : getCurrentRightBarRectF();
        if (barRectF.contains(downX, downY))
            return true;
        float xDistance = barRectF.centerX() - downX;
        float yDistance = barRectF.centerY() - downY;
        return Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2)) < mSliderWidth * 1.5;
    }

    private RectF getCurrentLeftBarRectF() {
        return new RectF(mLeftSliderX, 0, mLeftSliderX + mSliderWidth, mSliderHeight);
    }

    private RectF getCurrentRightBarRectF() {
        return new RectF(mRightSliderX - mSliderWidth, 0, mRightSliderX, mSliderHeight);
    }

    class BarDrawable extends Drawable {

        private Paint mBarPaint;
        private Rect mBounds;
        private boolean leftBar;

        BarDrawable(boolean leftBar) {
            mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            this.leftBar = leftBar;
        }

        void updateLocation(int x) {
            int width = mBounds.width();
            if (leftBar) {
                mBounds.left = x;
                mBounds.right = x + width;
            } else {
                mBounds.right = x;
                mBounds.left = x - width;
            }
            setBounds(mBounds);
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            Log.d("chj", "onBoundsChange");
            mBounds = bounds;
            if (leftBar) {
                mBounds.offset(mBarStrokeWidth / 2, mBarStrokeWidth / 2);
            } else {
                mBounds.offset(-mBarStrokeWidth / 2, mBarStrokeWidth / 2);
            }
            mBounds.bottom = mBounds.bottom - mBarStrokeWidth;
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            canvas.save();
            mBarPaint.setColor(mBarMainColor);
            mBarPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(mBounds, mBarPaint);
            mBarPaint.setStyle(Paint.Style.STROKE);
            mBarPaint.setStrokeWidth(mBarStrokeWidth);
            mBarPaint.setColor(mBarStrokeColor);
            canvas.drawRect(mBounds, mBarPaint);
            canvas.restore();
        }

        @Override
        public void setAlpha(int alpha) {
            mBarPaint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            mBarPaint.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSPARENT;
        }
    }

    private OnProgressChangeListener mListener;

    public void setOnProgressChangeListener(OnProgressChangeListener listener) {
        mListener = listener;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public interface OnProgressChangeListener {
        void onProgressChange(int leftProgress, int rightProgress, int centerProgress);
    }
}
