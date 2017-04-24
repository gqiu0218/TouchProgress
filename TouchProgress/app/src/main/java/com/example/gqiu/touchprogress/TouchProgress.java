package com.example.gqiu.touchprogress;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class TouchProgress extends View {
    private Paint mPaint, mBackgroundPaint, mTextPaint;
    private int mWidth, mHeight;
    private static final int STROKE_SIZE = 20;
    private RectF mRectF;
    private float mProgress = 0;
    private boolean mInited = false; //默认没有初始化
    private int mStartX, mStartY;
    private int mCenterX, mCenterY;
    private static final int ADD = 0;
    private static final int SUB = 1;


    public TouchProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TouchProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(STROKE_SIZE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.RED);

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setStrokeWidth(STROKE_SIZE);
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setColor(Color.GRAY);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.RED);
        mTextPaint.setTextSize(sp2px(18));
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        mCenterX = mWidth / 2;
        mCenterY = mHeight / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mRectF == null) {
            mRectF = new RectF(STROKE_SIZE, STROKE_SIZE, mWidth - STROKE_SIZE, mHeight - STROKE_SIZE);
        }

        canvas.drawArc(mRectF, -90, 360, false, mBackgroundPaint);
        canvas.drawArc(mRectF, -90, mProgress, false, mPaint);
        int progress = (int) ((mProgress / 360) * 100);
        String txt = progress + "%";
        canvas.drawText(txt, mCenterX - getTextWidth(mTextPaint, txt) / 2, mCenterY, mTextPaint);


        if (!mInited && mProgress < 360) {
            mProgress += 0.6;
            postInvalidateDelayed(50);
            Log.e("gqiu", "当前进度" + mProgress);
        } else {
            mInited = true;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mInited) {
            return super.onTouchEvent(event);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = (int) event.getX();
                mStartY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) event.getX();
                int moveY = (int) event.getY();
                mProgress += jisuan(moveX, moveY);
                if (mProgress > 360) {
                    mProgress = 360;
                } else if (mProgress < 0) {
                    mProgress = 0;
                }

                Log.e("gqiu", "进度：" + mProgress);
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return true;
    }

    private int jisuan(int moveX, int moveY) {
        if (moveX == mStartX || moveY == mStartY) {
            return 0;
        }

        double a = Math.sqrt(Math.pow(Math.abs(mCenterY - mStartY), 2) + Math.pow(Math.abs(mStartX - mCenterX), 2));
        double b = Math.sqrt(Math.pow(Math.abs(mCenterY - moveY), 2) + Math.pow(Math.abs(moveX - mCenterX), 2));
        double c = Math.sqrt(Math.pow(Math.abs(moveY - mStartY), 2) + Math.pow(Math.abs(moveX - mStartX), 2));

        double cosC = (Math.pow(a, 2) + Math.pow(b, 2) - Math.pow(c, 2)) / (2 * a * b);
        int degrees = (int) Math.toDegrees(Math.acos(cosC));

        int flag;
        if (moveX > mCenterX) {
            //圆的右边，，，，
            if (moveY > mStartY) {
                flag = ADD;
            } else {
                flag = SUB;
            }
        } else {
            if (moveY < mStartY) {
                flag = ADD;
            } else {
                flag = SUB;
            }
        }

        mStartX = moveX;
        mStartY = moveY;

        if (flag == ADD) {
            Log.e("gqiu", "增加");
            return (int) (degrees * 0.5f);
        } else {
            Log.e("gqiu", "减少");
            return (int) (-degrees * 0.5f);
        }

    }


    public int dp2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public int sp2px(float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    public int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }
}
