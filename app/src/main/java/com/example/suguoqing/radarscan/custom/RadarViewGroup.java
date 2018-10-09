package com.example.suguoqing.radarscan.custom;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.example.suguoqing.radarscan.R;
import com.example.suguoqing.radarscan.been.Gender;
import com.example.suguoqing.radarscan.been.Info;

public class RadarViewGroup extends ViewGroup implements RadarView.IScannerListener {
    private static final String TAG = "RadarViewGroup";
    private int mWidth,mHight;
    private SparseArray<Float> scanAngleList = new SparseArray<>();// 记录item所在的扫描位置的角度
    private SparseArray<Info> infoList;
    private int dataLength;//数据源的长度
    private int minItemPostion;//最小的距离的item所在的位置,也就是在infolist中的第几个是最小的距离
    private CircleView currentCircleView;//当前的item,我们需要将他的头像变大
    private CircleView minItem;//距离最小的item
    private IRadarClickListen radarClickListen;


    public RadarViewGroup(Context context) { 
        super(context,null);

    }

    public RadarViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs,0);
        //initData();
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
            if(child.getId() == R.id.Radar_scan){
                ((RadarView) child).setiScannerListener(this);
                if(infoList != null && infoList.size() > 0){
                    ((RadarView) child).setMaxScanItemCount(infoList.size());
                    ((RadarView) child).startScan();
                }
            }
            continue;
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        //先放置雷达扫描图
        View view = findViewById(R.id.Radar_scan);
        if(view != null){
            view.layout(0,0,view.getMeasuredWidth(),view.getMeasuredHeight());
            Log.d(TAG, "onLayout: view"+view.getMeasuredWidth()+"--"+view.getMeasuredHeight());
        }
        for(int i = 0;i < childCount;i++){
            final View child = getChildAt(i);
            if(child.getId() == R.id.Radar_scan){
                continue;
                //如果不是circleView那么就跳过
            }

            //设置小圆点的坐标信息
            //坐标: x = (角度-5)变换成弧度,cos  再乘以 所在半径

           /* ((CircleView)child).setDisX((float) Math.cos(Math.toRadians(scanAngleList.get(i) -5))
                *((CircleView)child).getProportion() * mWidth / 2);

            ((CircleView)child).setDisY((float) Math.sin(Math.toRadians(scanAngleList.get(i) -5))
                    *((CircleView)child).getProportion() * mWidth / 2);
            */

            /*((CircleView) child).setDisX(((CircleView)child).getX());
            ((CircleView) child).setDisY(((CircleView)child).getY());*/

            final int temp = i-1<0?i:i-1;
            ((CircleView)child).setDisX((float) Math.cos(Math.toRadians(scanAngleList.get(temp)))
                    *((CircleView)child).getProportion() * mWidth / 2);

            ((CircleView)child).setDisY((float) Math.sin(Math.toRadians(scanAngleList.get(temp)))
                    *((CircleView)child).getProportion() * mWidth / 2);

            Log.d(TAG, "onLayout: zzzzzzzzzzz"+((CircleView) child).getDisY()+"zzzzzzzzz"+((CircleView) child).getDisX());
            //扫描了一整圈
            if(scanAngleList.get(temp) == 0){
                continue;
            }

            //将小圆点加入这个viewgroup中来,在这里如果角度在第一象限那么 x值是正的,根据象限有符号正负
            child.layout((int) ((CircleView) child).getDisX() + mWidth/2,(int) ((CircleView) child).getDisY() + mHight/2,
                    (int) ((CircleView) child).getDisX() + mWidth/2 + child.getMeasuredWidth(),
                    (int) ((CircleView) child).getDisY() + mWidth/2 + child.getMeasuredHeight()
                    );

            //设置监听

            child.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //先还原当前item大小
                    resetAnim(currentCircleView);
                    currentCircleView = (CircleView)child;
                    startAnim(currentCircleView,temp);
                    if(radarClickListen != null){
                        radarClickListen.onRadarItemClick(temp);
                    }
                }
            });
        }

    }

    @Override
    public void onScanner(int currentPostion, int currentAngle) {
        if(currentAngle == 0){
            scanAngleList.put(currentPostion,1f);
        }else{
            scanAngleList.put(currentPostion, (float) currentAngle);
        }
        requestLayout();

    }

    @Override
    public void onSuccess() {
        resetAnim(currentCircleView);
        currentCircleView = minItem;
        startAnim(currentCircleView, minItemPostion);
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


    public void setInfoList(SparseArray<Info> infoList) {
        this.infoList = infoList;
        dataLength = infoList.size();
        float min = Float.MIN_VALUE;
        float max = Float.MAX_VALUE;
        //找到距离的最大值,最小值是minItemPostion
        for(int i = 0;i<dataLength;i++){
            Info info = infoList.get(i);
            if(info.getDistance() < min){
                min = (float) info.getDistance();
                minItemPostion = i;
            }

            if(info.getDistance() > max){
                max = (float) info.getDistance();

            }
            //设置item的初始化角度,都是0,
            scanAngleList.put(i,0f);
        }

        for(int j = 0;j<dataLength;j++){

            CircleView circleView = new CircleView(getContext());
            //判断男女,如果男就蓝色,如果女就是红色
            if(infoList.get(j).getGender() == Gender.男){
                circleView.setPaintColor(getResources().getColor(R.color.bg_color_blue));
            }else{
                circleView.setPaintColor(getResources().getColor(R.color.bg_color_pink));
            }

            //根据远近距离的不同计算得到的应该占的半径比例 0.312-0.832
            if(infoList.get(j).getDistance() <= 3){
                circleView.setProportion((float) 0.3);
            }else{
                circleView.setProportion((float) ((infoList.get(j).getDistance() / 10 )));
            }

            if(minItemPostion == j){
                minItem = circleView;
            }
            addView(circleView);
        }

    }

    public void setRadarClickListen(IRadarClickListen listen){
        radarClickListen = listen;
    }

    public interface IRadarClickListen {
        void onRadarItemClick(int postion);
    }


    private void resetAnim(CircleView object) {
        if (object != null) {
            ObjectAnimator.ofFloat(object, "scaleX", 1f).setDuration(300).start();
            ObjectAnimator.ofFloat(object, "scaleY", 1f).setDuration(300).start();
        }

    }


    private void startAnim(CircleView object, int position) {
        if (object != null) {
            ObjectAnimator.ofFloat(object, "scaleX", 2f).setDuration(300).start();
            ObjectAnimator.ofFloat(object, "scaleY", 2f).setDuration(300).start();
        }
    }


    public void setCurrentShowItem(int position) {
        CircleView child = (CircleView) getChildAt(position + 1);
        resetAnim(currentCircleView);
        currentCircleView = child;
        startAnim(currentCircleView, position);
    }


}
