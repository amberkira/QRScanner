package com.amber.qrscanner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by amber_sleepeanuty on 2017/10/11.
 */

public class MaskView extends View {
    int mOritation;
    int mScanMarginTop;
    int mScanWidth;
    int mScanHeight;
    int mScanBoarderSize;
    int mScanBoarderColor;
    int mScanCornerSize;
    int mScanCornerWidth;
    int mScanCornerColor;
    int mMaskARBG;
    int mHintSize;
    Rect mScannerRect;
    String mHintText;
    String mErrorHint;

    int mX;
    int mY;


    public MaskView(Context context) {
        this(context,null);
    }

    public MaskView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mOritation = 0;//0垂直方向 1水平方向
        mScanMarginTop = Utils.dp2px(context,100);

        mScanWidth = Utils.dp2px(context,200);
        mScanHeight = Utils.dp2px(context,200);
        mScanBoarderSize = Utils.dp2px(context,1);
        mScanCornerSize = Utils.dp2px(context,6);
        mScanCornerWidth = Utils.dp2px(context,2);

        mScanBoarderColor = Color.parseColor("#ffffff");
        mScanCornerColor = Color.parseColor("#000000");
        mMaskARBG = Color.parseColor("#80CCCCCC");

        mHintSize = Utils.sp2px(context,16);
        mHintText = "请将二维码放入框中即可自动扫描";
        mErrorHint = "打开相机错误";

        mX = 0;
        mY = 0;

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calFramingRect();
    }

    private void calFramingRect() {
        int leftOffset = (getWidth() - mScanWidth) / 2;
        mScannerRect = new Rect(leftOffset, mScanMarginTop,leftOffset+mScanWidth , mScanMarginTop + mScanHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mScannerRect == null){
            return;
        }

        onDrawMask(canvas);

        onDrawBoarder(canvas);

        onDrawCornor(canvas);

        onDrawScanner(canvas);

        // 画提示文本
        onDrawTipText(canvas);

        // 移动扫描线的位置
        moveScanLine();
        
    }

    private void onDrawMask(Canvas canvas) {
        int w = canvas.getWidth();
        int h = canvas.getHeight();
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(mMaskARBG);
        p.setStyle(Paint.Style.FILL);

        canvas.drawRect(0,0,mScannerRect.left,h,p);
        canvas.drawRect(mScannerRect.left,0,mScannerRect.right,mScanMarginTop,p);
        canvas.drawRect(mScannerRect.right,0,w,h,p);
        canvas.drawRect(mScannerRect.left,mScannerRect.bottom,mScannerRect.right,h,p);

    }

    private void onDrawBoarder(Canvas canvas) {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(mScanBoarderColor);

        canvas.drawRect(mScannerRect,p);

    }

    private void onDrawCornor(Canvas canvas) {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.FILL);
        p.setColor(mScanCornerColor);
        p.setStrokeWidth(mScanCornerWidth);

        //边角水平四条线
        canvas.drawLine(mScannerRect.left-1,mScanMarginTop,mScannerRect.left+mScanCornerSize,mScanMarginTop,p);
        canvas.drawLine(mScannerRect.right,mScanMarginTop,mScannerRect.right-mScanCornerSize+1,mScanMarginTop,p);
        canvas.drawLine(mScannerRect.left-1,mScannerRect.bottom,mScannerRect.left+mScanCornerSize,mScannerRect.bottom,p);
        canvas.drawLine(mScannerRect.right,mScannerRect.bottom,mScannerRect.right-mScanCornerSize+1,mScannerRect.bottom,p);

        //边角竖直四条线
        canvas.drawLine(mScannerRect.left,mScanMarginTop,mScannerRect.left,mScanMarginTop+mScanCornerSize,p);
        canvas.drawLine(mScannerRect.left,mScannerRect.bottom-mScanCornerSize,mScannerRect.left,mScannerRect.bottom,p);
        canvas.drawLine(mScannerRect.right,mScanMarginTop,mScannerRect.right,mScanMarginTop+mScanCornerSize,p);
        canvas.drawLine(mScannerRect.right,mScannerRect.bottom-mScanCornerSize,mScannerRect.right,mScannerRect.bottom,p);

    }

    private void onDrawScanner(Canvas canvas) {
        Paint p = new Paint();
        p.setColor(Color.RED);
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.FILL);


        if (mOritation == 0){
            canvas.drawLine(mScannerRect.left,mScannerRect.top+mY,mScannerRect.right,mScannerRect.top+mY,p);
        }else {
            canvas.drawLine(mScannerRect.left+mX,mScannerRect.top,mScannerRect.left+mX,mScannerRect.bottom,p);
        }
    }

    private void onDrawTipText(Canvas canvas) {

        if (TextUtils.isEmpty(mHintText)||mHintText.length()==0){
            return;
        }

        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.WHITE);
        p.setTextSize(mHintSize);

        Rect r = new Rect();
        p.getTextBounds(mHintText,0,mHintText.length(),r);

        canvas.drawText(mHintText,0,mHintText.length(),(getWidth()-r.right)/2,mScannerRect.bottom+100,p);

    }

    private void moveScanLine() {
        if (mOritation == 0){

            mY +=2;
            if (mY >= mScanHeight){
                mY = 0;
            }
        }else {
            mX =+ 2;
            if (mX >= mScanWidth){
                mX = 0;
            }
        }
        postInvalidateDelayed(10,mScannerRect.left,mScannerRect.top,mScannerRect.right,mScannerRect.bottom);
    }
}
