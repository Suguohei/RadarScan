package com.example.suguoqing.radarscan.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.example.suguoqing.radarscan.R;
import com.example.suguoqing.radarscan.been.Info;

public class RadarViewGroup extends ViewGroup implements RadarView.IScannerListener {
    private int mWidth,mHight;
    private SparseArray<Float> scanAngleList = new SparseArray<>();// 记录item所在的扫描位置的角度
    private SparseArray<Info> infoList;
    private int dataLength;//数据源的长度
    private int minItemPostion;//最小的距离的item所在的位置,也就是角度
    private CircleView currentCircleView;//当前的item,我们需要将他的头像变大
    private CircleView minItem;//距离最小的item


    public RadarViewGroup(Context context) {
        super(context,null);
    }

    public RadarViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs,0);
    }

    public RadarViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSize(widthMeasureSpec),MeasureSize(heightMeasureSpec));
        mHight = getHeight();
        mWidth = getWidth();
        mWidth = mHight = Math.min(mHight,mWidth);
        // 计算出所有的childView的宽和高
        measureChildren(widthMeasureSpec,heightMeasureSpec);
        for(int i = 0;i<getChildCount();i++){
            View child = getChildAt(i);
            ((RadarView) child).setiScannerListener(this);
            if(infoList != null && infoList.size() > 0){
                ((RadarView) child).setMaxScanItemCount(infoList.size());
                ((RadarView) child).startScan();
            }
            continue;
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    public void onScanner(int currentPostion, int currentAngle) {

    }

    @Override
    public void onSuccess() {

    }

    public int MeasureSize(int measureSpec){
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
}
