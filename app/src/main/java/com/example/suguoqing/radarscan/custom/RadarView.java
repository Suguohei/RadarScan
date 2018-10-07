package com.example.suguoqing.radarscan.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.suguoqing.radarscan.R;

import java.net.InterfaceAddress;


public class RadarView extends View {
    private static final String TAG = "RadarView";
    private Paint paintLine;//画圆的线需要的画笔
    private Paint paintCircle;//画圆所需要的画笔
    private Paint paintRadar;//画扫描所需要的画笔

    private int mWidth,mHight;//整个圆形的长度和宽度;

    private Matrix matrix = new Matrix();//旋转所需要的矩阵
    private int scanAngle;//扫描的旋转角度
    private Shader scanShader;//扫描之后渲染阴影
    private Bitmap bitmap;//中间的一个小图标

    private static float[] circleProportion = {1/13f,2/13f,3/13f,4/13f,5/13f,6/13f};
    private int scanSpeed = 5;//扫描的速度

    private int currentScanningCount;//当前扫描的次数
    private int currentScanningItem;//当前扫描的头像
    private int maxScanItemCount;//最大扫描次数
    private boolean startScan = false;//只有设置了数据才会扫描
    private IScannerListener iScannerListener;

    public void setMaxScanItemCount(int maxScanItemCount) {
        this.maxScanItemCount = maxScanItemCount;
    }

    public void setiScannerListener(IScannerListener iScannerListener) {
        this.iScannerListener = iScannerListener;
    }

    public interface IScannerListener{
        //正在扫描
        void onScanner(int currentPostion,int currentAngle);

        //扫描成功
        void onSuccess();
    }

    public RadarView(Context context) {
        this(context,null);
    }

    public RadarView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public RadarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        post(run);//更新ui

    }

    private Runnable run = new Runnable() {
        @Override
        public void run() {
            scanAngle = (scanAngle + scanSpeed) % 360;
            matrix.postRotate(scanSpeed,mWidth/2,mHight/2);
            invalidate();
            postDelayed(run, 130);
            if(startScan && currentScanningCount <= (360 /scanSpeed)){
                if(iScannerListener != null && currentScanningCount % scanSpeed == 0 && currentScanningItem < maxScanItemCount){
                    //监听不能为空,没5次刷一次头像,只扫描一圈
                    iScannerListener.onScanner(currentScanningItem,scanAngle);
                    currentScanningItem ++;
                }else if(iScannerListener != null && currentScanningItem == maxScanItemCount){
                    iScannerListener.onSuccess();
                }
                currentScanningCount++;
            }


        }
    };

    private void init() {
        Log.d(TAG, "init: zzzzzzzzzzzzzzzz");
        //初始化画笔
        paintLine = new Paint();
        paintLine.setColor(getResources().getColor(R.color.bg_color_blue));
        paintLine.setAntiAlias(true);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(1);

        paintCircle = new Paint();
        paintCircle.setColor(Color.WHITE);
        paintCircle.setAntiAlias(true);

        paintRadar = new Paint();
        paintRadar.setStyle(Paint.Style.FILL_AND_STROKE);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw: zzzzzzzzzzz");
        drawCricle(canvas);
        drawCenterIcon(canvas);
        drawRadar(canvas);
    }

    private void drawCricle(Canvas canvas) {
        canvas.drawCircle(mWidth/2,mHight/2,mWidth*circleProportion[0],paintLine);
        canvas.drawCircle(mWidth/2,mHight/2,mWidth*circleProportion[1],paintLine);
        canvas.drawCircle(mWidth/2,mHight/2,mWidth*circleProportion[2],paintLine);
        canvas.drawCircle(mWidth/2,mHight/2,mWidth*circleProportion[3],paintLine);
        canvas.drawCircle(mWidth/2,mHight/2,mWidth*circleProportion[4],paintLine);
        canvas.drawCircle(mWidth/2,mHight/2,mWidth*circleProportion[5],paintLine);
    }

    private void drawRadar(Canvas canvas) {
        canvas.save();
        paintRadar.setShader(scanShader);
        canvas.concat(matrix);
        canvas.drawCircle(mWidth/2,mHight/2,mWidth*circleProportion[4],paintRadar);
        canvas.restore();

    }

    private void drawCenterIcon(Canvas canvas){
        canvas.drawBitmap(bitmap,null, new Rect((int) (mWidth / 2 - mWidth * circleProportion[0]), (int) (mHight / 2 - mWidth * circleProportion[0]),
                (int) (mWidth / 2 + mWidth * circleProportion[0]), (int) (mHight / 2 + mWidth * circleProportion[0])),paintCircle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure: zzzzzzzzzzzzzzzzz");
        int widthSzie = measureSize(widthMeasureSpec);
        int heightSzie = measureSize(heightMeasureSpec);
        setMeasuredDimension(widthSzie,heightSzie);

        mWidth = getMeasuredWidth();
        mHight = getMeasuredHeight();
        mWidth = mHight = Math.min(mWidth,mHight);
        bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.circle_photo);
        //设置扫描的渲染
        scanShader = new SweepGradient(mWidth / 2, mHight / 2,
                new int[]{Color.TRANSPARENT, Color.parseColor("#84B5CA")}, null);
    }


    private int measureSize(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if(specMode == MeasureSpec.EXACTLY){
            result = specSize;
        }else{
            result = 300;
            if(specMode == MeasureSpec.AT_MOST){
                result = Math.min(result,specSize);
            }
        }
        return result;
    }

    public void startScan(){
        startScan = true;
    }


}
