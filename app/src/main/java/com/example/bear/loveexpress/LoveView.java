package com.example.bear.loveexpress;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

/**
 * Created by haichen.cui on 2017.08.17
 */

public class LoveView extends View {
    private static final int SPAN_COUNT = 5;
    Paint mPaint = new Paint();

    //文字相关配置
    String mContent = "Ting";
    int mContentWidth;

    //曲线相关配置
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
    float[] mCtlPoints = new float[SPAN_COUNT * 2];
    float[] mEndPoints = new float[SPAN_COUNT * 2];

    float baseLine = 400;
    private float mSpan;

    public void setBaseLine(float baseLine) {
        this.baseLine = baseLine;

        for (int i = 0; i < mCtlPoints.length; i += 2) {
            mCtlPoints[i + 1] = baseLine - mRandom.nextInt(50);
        }

        for (int i = 0; i < mEndPoints.length; i += 2) {
            mEndPoints[i + 1] = baseLine;
        }
        invalidate();
    }

    public float getBaseLine() {
        return baseLine;
    }

    {
        //对View设置硬件加速
//        setLayerType(LAYER_TYPE_HARDWARE, null);

        //计算content文字的宽度
        float[] charWidths = new float[mContent.length()];
        mPaint.setTextSize(300);
        mPaint.getTextWidths(mContent, charWidths);
        for (float charWidth : charWidths) {
            mContentWidth += charWidth;
        }

        mSpan = mContentWidth / (SPAN_COUNT + 1);

        for (int i = 0; i < mCtlPoints.length; i += 2) {
            mCtlPoints[i] = (i + 1) * mSpan - mSpan / 2;
            mCtlPoints[i + 1] = baseLine - mRandom.nextInt(50);
        }

        for (int i = 0; i < mEndPoints.length; i += 2) {
            mEndPoints[i] = (i + 1) * mSpan;
            mEndPoints[i + 1] = baseLine;
        }
    }

    public LoveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int saved = canvas.saveLayer(
                0, 0, getResources().getDisplayMetrics().widthPixels,
                getResources().getDisplayMetrics().heightPixels,
                null, Canvas.ALL_SAVE_FLAG);

        //绘制文字部分
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(300);
        canvas.drawText(mContent, 0, 400, mPaint);

        mPaint.setShader(mLinearShader);
        mPaint.setXfermode(pdXferMode);
        mPath.reset();
        mPath.moveTo(0, 400 + 100);
        for (int i = 0; i < mCtlPoints.length; i += 2) {
            mPath.quadTo(mCtlPoints[i], mCtlPoints[i + 1], mEndPoints[i], mEndPoints[i + 1]);
        }
        mPath.lineTo(mEndPoints[mEndPoints.length - 2], 400 + 100);
        mPath.close();
        canvas.drawPath(mPath, mPaint);
        mPaint.setXfermode(null);
        canvas.restoreToCount(saved);
    }
}
