package com.dx.dxloadingview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;


public class DxLoadingView extends View {

    //private static final float sMagicNumber = 0.55228475f;

    private int mWidth,mHeight;
    private int mRadius;
    private int mPadding;
    private float mDensity;
    private Paint mPaint,mPaint2,mPaint3;
    private float[] mDegrees;
    private Circle[] mCircles;


    private Path mPath,mPath2;

    private int mShortDuration,mMediumDuration,mLongDuration;
    private int mIndex;
    private float mDiffDegree;

    public DxLoadingView(Context context) {
        super(context);
        init();
    }

    public DxLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DxLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureDimension((int)(200*mDensity),widthMeasureSpec);
        int height = measureDimension((int)(80*mDensity),heightMeasureSpec);
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mRadius = (Math.min(w,h) - mPadding)/2;

        //init circles radius and center point
        for(int i = 0; i< mCircles.length; i++){
            if(i == 4){
                mCircles[i].radius = mRadius/8;
            }else{
                mCircles[i].radius = mRadius/8 * (1-i*0.2f);
            }
            mCircles[i].center.x = mWidth/2;
            mCircles[i].center.y = (mHeight-mRadius*2)/2+mRadius/8;
        }

        double tan = mRadius/8.0f/(mRadius-mRadius/8.0f);
        double b = Math.atan(tan);
        mDiffDegree = (float) Math.toDegrees(b);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawLine(0,mHeight/2,mWidth,mHeight/2,mPaint2);
        canvas.drawLine(mWidth/2,0,mWidth/2,mHeight,mPaint2);
        canvas.drawCircle(mWidth/2,mHeight/2,mRadius,mPaint2);
        canvas.drawCircle(mWidth/2,mHeight/2,mRadius-mRadius/8,mPaint2);

        for(int i = 0; i< mCircles.length; i++){
            Circle circle = mCircles[i];
            float radian = (float) (mDegrees[i] / 180.0 * Math.PI);
            mCircles[i].center.x = (int)(mWidth/2 + (float)Math.cos(radian) * (mRadius-mRadius/8));
            mCircles[i].center.y = (int)(mHeight/2 + (float)Math.sin(radian) * (mRadius-mRadius/8));
            canvas.drawCircle(circle.center.x,circle.center.y,circle.radius,mPaint2);
        }

        updatePathStart();
        if(mPath != null) canvas.drawPath(mPath,mPaint2);
        if(mPath2 != null) canvas.drawPath(mPath2,mPaint2);

        if(mDegrees[0] < 270+360) {
            mDegrees[0] += 1/3.0f;
            if(mDegrees[0] > 301){
               mIndex = 1;
               mDegrees[mIndex] = mDegrees[0] - 30;
            }
            if(mDegrees[0] > 331){
                mIndex = 2;
                mDegrees[mIndex] = mDegrees[0] - 60;
            }
            if(mDegrees[0] > 361){
                mIndex = 3;
                mDegrees[mIndex] = mDegrees[0] - 90;
            }

            if(mDegrees[0] > 390){
                mIndex = 4;
                mDegrees[mIndex] = mDegrees[0] - 120;
            }
        }else{
            for(int i=1;i<mDegrees.length;i++){
                if(mDegrees[i] < 630){
                    mDegrees[i] += 1/3.0f;
                }
            }
        }


        if(mDegrees[1] > 600 && mDegrees[1] < 620){
            mIndex = 1;
            updatePathEnd();
        }

        if(mDegrees[2] > 600 && mDegrees[2] < 620){
            mIndex = 2;
            updatePathEnd();
        }

        if(mDegrees[3] > 600 && mDegrees[3] < 620){
            mIndex = 3;
            updatePathEnd();
        }

        if(mDegrees[4] > 600 && mDegrees[4] < 620){
            mIndex = 4;
            updatePathEnd();
        }

        if(mDegrees[4] < 616+5){
            invalidate();
        }


    }


    public void start(){
        //playAnimation();
        //playRotateAnimation();
    }


    private void init(){

        mShortDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mMediumDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        mLongDuration = getResources().getInteger(android.R.integer.config_longAnimTime);

        mDensity = getResources().getDisplayMetrics().density;
        mPadding = (int)(4 * mDensity);
        mDegrees = new float[]{270f,270f,270f,270f,270f};
        mIndex = 0;

        mCircles = new Circle[5];
        for(int i=0;i<5;i++){
            mCircles[i] = new Circle();
            mCircles[i].center = new Point();
        }

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLUE);

        mPaint2 = new Paint();
        mPaint2.setAntiAlias(true);
        mPaint2.setStyle(Paint.Style.STROKE);
        mPaint2.setColor(Color.GRAY);
        mPaint2.setStrokeWidth(3);

        mPaint3 = new Paint();
        mPaint3.setAntiAlias(true);
        mPaint3.setStyle(Paint.Style.FILL);
        mPaint3.setColor(Color.GREEN);

        //mMatrix = new Matrix();
    }

    private int measureDimension(int defaultSize, int measureSpec) {

        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(defaultSize, specSize);
        }
        else {
            result = defaultSize;
        }
        return result;
    }

    private void updatePathStart(){

        float mDegree = mDegrees[mIndex];

        if(mDegree - 270 < 10 || mIndex == 4){
            return;
        }

        if(mPath == null){
            mPath = new Path();
        }else{
            mPath.reset();
        }

        Circle circle = mCircles[mIndex];

        float currentD = mDegree + (mDegree-270)/2;
        currentD = currentD > 315 ? 315 : currentD;
        float radian = (float) (currentD / 180.0 * Math.PI);
        float x = mCircles[4].center.x + (float)Math.cos(radian) * mCircles[4].radius;
        float y = mCircles[4].center.y + (float)Math.sin(radian) * mCircles[4].radius;

        float radian2 = (float) ((currentD+120)/ 180.0 * Math.PI);
        float x2 = mCircles[4].center.x + (float)Math.cos(radian2) * mCircles[4].radius;
        float y2 = mCircles[4].center.y + (float)Math.sin(radian2) * mCircles[4].radius;

        float currentD2 = 270-(mDegree-270)/2;
        currentD2 = currentD2 > 285 ? 285 : currentD2;
        float radian21 = (float) (currentD2 / 180.0 * Math.PI);
        float x21 = circle.center.x + (float)Math.cos(radian21) * circle.radius;
        float y21 = circle.center.y + (float)Math.sin(radian21) * circle.radius;

        float radian22 = (float) ((currentD2-120)/ 180.0 * Math.PI);
        float x22 = circle.center.x + (float)Math.cos(radian22) * circle.radius;
        float y22 = circle.center.y + (float)Math.sin(radian22) * circle.radius;

        float centerD = 270+(mDegree-270)/2;
        centerD = centerD > 285 ? 285 : centerD;
        float radian3 = (float) (centerD / 180.0 * Math.PI);
//        float midX = mCircle.center.x + (float)Math.cos(radian3) * (mCircle.radius - circle.radius);
//        float midY = mCircle.center.y + (float)Math.sin(radian3) * (mCircle.radius - circle.radius);

        float midX2 = mWidth/2 + (float)Math.cos(radian3) * mRadius;
        float midY2 = mHeight/2 + (float)Math.sin(radian3) * mRadius;

        float midX3 = mWidth/2 + (float)Math.cos(radian3) * (mRadius-mRadius/8*2);
        float midY3 = mHeight/2 + (float)Math.sin(radian3) * (mRadius-mRadius/8*2);

        float progress = (mDegree-270-10)/20.0f;
        if(progress < 1){
            float mDiff = circle.radius * 2 + (mIndex-1)*circle.radius/2;
            mPath.moveTo(x,y);
            mPath.lineTo(x2,y2);
            mPath.quadTo(midX2,midY3-mDiff*progress,x22,y22);

            mPath.lineTo(x21,y21);
            mPath.quadTo(midX3,midY2+(mDiff/4*3)*progress,x,y);

            mCircles[4].radius = mRadius/8*0.20f*(5-mIndex) - progress*mRadius/32;
        }
    }

    private void updatePathEnd(){
        float mDegree = mDegrees[mIndex];
        if(mDegree < 600){
           return;
        }

        if(mPath2 == null){
            mPath2 = new Path();
        }else{
            mPath2.reset();
        }

        Circle circle = mCircles[mIndex];

        float currentD = 585 + (mDegree-600)/2;
        currentD = currentD > 600 ? 600 : currentD;
        float radian = (float) (currentD / 180.0 * Math.PI);
        float x = mCircles[0].center.x + (float)Math.cos(radian) * mCircles[0].radius;
        float y = mCircles[0].center.y + (float)Math.sin(radian) * mCircles[0].radius;

        float radian2 = (float) ((currentD-120)/ 180.0 * Math.PI);
        float x2 = mCircles[0].center.x + (float)Math.cos(radian2) * mCircles[0].radius;
        float y2 = mCircles[0].center.y + (float)Math.sin(radian2) * mCircles[0].radius;

        float currentD2 = 285 + (mDegree-600)/2;
        currentD2 = currentD2 > 300 ? 300 : currentD2;
        float radian21 = (float) (currentD2 / 180.0 * Math.PI);
        float x21 = circle.center.x + (float)Math.cos(radian21) * circle.radius;
        float y21 = circle.center.y + (float)Math.sin(radian21) * circle.radius;

        float radian22 = (float) ((currentD2+120)/ 180.0 * Math.PI);
        float x22 = circle.center.x + (float)Math.cos(radian22) * circle.radius;
        float y22 = circle.center.y + (float)Math.sin(radian22) * circle.radius;

        float cDegree = mDiffDegree*(5-mIndex)*0.2f;
        float moveDegree = 30 - mDiffDegree - cDegree;
        float centerD = mDegree+cDegree + ((630-mDiffDegree) - (mDegree+cDegree))/2;
        float radian3 = (float) (centerD / 180.0 * Math.PI);
        float midX = mWidth/2 + (float)Math.cos(radian3) * (mRadius - mCircles[0].radius);
        float midY = mHeight/2 + (float)Math.sin(radian3) * (mRadius - mCircles[0].radius);

        float midX2 = mWidth/2 + (float)Math.cos(radian3) * mRadius;
        float midY2 = mHeight/2 + (float)Math.sin(radian3) * mRadius;

        float midX3 = mWidth/2 + (float)Math.cos(radian3) * (mRadius-circle.radius);
        float midY3 = mHeight/2 + (float)Math.sin(radian3) * (mRadius-circle.radius);

        float progress = (mDegree-600)/moveDegree;
        float mDiff = circle.radius;
        if(progress < 1){
            mPath2.addCircle(midX,midY,5, Path.Direction.CCW);
            mPath2.moveTo(x,y);
            mPath2.lineTo(x2,y2);
            //bottom
            mPath2.quadTo(midX,midY,x22,y22);

            mPath2.lineTo(x21,y21);
            //top
            mPath2.quadTo(midX,midY,x,y);

        }
    }

    private void playAnimation(){
        ValueAnimator animator = ValueAnimator.ofFloat(270,270+360);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mDegrees[0] = (Float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        animator.setDuration(2000);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addListener(new AnimatorEndListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //playReverseAnimation();
            }
        });
        animator.start();

    }


    public abstract class AnimatorEndListener implements Animator.AnimatorListener{

        @Override
        public void onAnimationStart(Animator animator){}

        @Override
        public void onAnimationCancel(Animator animator){}

        @Override
        public void onAnimationRepeat(Animator animator){}
    }


    private class Circle{
        public Point center;
        public float radius;
    }


}
