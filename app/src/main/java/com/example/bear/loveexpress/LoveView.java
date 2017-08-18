package com.example.bear.loveexpress;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Random;

/**
 * Created by haichen.cui on 2017.08.17
 */

public class LoveView extends View {
    private static final int SPAN_COUNT = 5;
    Paint mPaint = new Paint();

    //文字相关配置
    String mContent = "ting";
    int mContentTextSize = 150;
    int mContentWidth;

    //曲线相关参数-------------------------------------------------------------------------
    Shader mLinearShader = new LinearGradient(0, 0,
            getResources().getDisplayMetrics().widthPixels, 0,
            new int[]{
                    Color.parseColor("#E91E63"),
                    Color.parseColor("#2196F3"),
                    Color.parseColor("#906866")},
            null,
            Shader.TileMode.MIRROR);

    Xfermode pdXferMode = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);

    Path mPath = new Path();

    private final Random mRandom = new Random(System.currentTimeMillis());
    float mTextBaseLine = mContentTextSize + 100;
    float mStartLine = mTextBaseLine + mContentTextSize / 2;
    float mEndLine = mStartLine;
    private float mSpan;

    float[] mCtlPoints = new float[SPAN_COUNT * 2];
    float[] mEndPoints = new float[SPAN_COUNT * 2];

    //曲线相关配置-------------------------------------------------------------------------

    {
        //计算content文字的宽度
        float[] charWidths = new float[mContent.length()];
        mPaint.setTextSize(mContentTextSize);
        mPaint.getTextWidths(mContent, charWidths);

        for (float charWidth : charWidths) {
            mContentWidth += charWidth;
        }

        mSpan = mContentWidth / (SPAN_COUNT + 1);

        for (int i = 0; i < mCtlPoints.length; i += 2) {
            mCtlPoints[i] = (i + 1) * mSpan - mSpan / 2;
            mCtlPoints[i + 1] = mEndLine + mRandom.nextInt(50);
        }

        for (int i = 0; i < mEndPoints.length; i += 2) {
            mEndPoints[i] = (i + 1) * mSpan;
            mEndPoints[i + 1] = mEndLine;
        }
    }

    public LoveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 贝塞尔曲线上涨高度的dy
     *
     * @param dy
     * @return 是否已经填满了文字
     */
    public boolean letUsRise(float dy) {
        if (mEndLine < mTextBaseLine - mContentTextSize) {
            return true;
        }

        mEndLine -= dy;

        for (int i = 0; i < mCtlPoints.length; i += 2) {
            mCtlPoints[i + 1] = mEndLine + mRandom.nextInt(50);
        }

        for (int i = 0; i < mEndPoints.length; i += 2) {
            mEndPoints[i + 1] = mEndLine;
        }
        postInvalidate();
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int saved = canvas.saveLayer(
                0, 0, getResources().getDisplayMetrics().widthPixels,
                getResources().getDisplayMetrics().heightPixels,
                null, Canvas.ALL_SAVE_FLAG);

        //绘制文字部分，作为PorterDuff.Mode的DST
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLACK);
        mPaint.setFakeBoldText(true);
        mPaint.setTextSize(mContentTextSize);
        canvas.drawText(mContent, 0, mTextBaseLine, mPaint);

        //绘制贝塞尔曲线，作为PorterDuff.Mode的SRC
        mPaint.setShader(mLinearShader);
        mPaint.setXfermode(pdXferMode);
        mPath.reset();
        mPath.moveTo(0, mStartLine);
        mPath.lineTo(0, mEndLine);
        for (int i = 0; i < mCtlPoints.length; i += 2) {
            mPath.quadTo(mCtlPoints[i], mCtlPoints[i + 1], mEndPoints[i], mEndPoints[i + 1]);
        }
        mPath.lineTo(mContentWidth, mStartLine);
        mPath.close();
        canvas.drawPath(mPath, mPaint);
        mPaint.setShader(null);
        mPaint.setXfermode(null);
        canvas.restoreToCount(saved);
    }
}
