package com.dx.dxloadingview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;


public class DxLoadingView extends View {

    private static final int SIZE_LARGE = 88;
    private static final int SIZE_SMALL = 64;

    private static final int DEGREE_START = 270;         //animation start degree
    private static final int DEGREE_END = 270 + 360;
    private static final int DEGREE_DISCONNECT_DIFF = 30; //while little circle rotate degree less than this degree, show the connected path

    private static final int COUNT_CIRCLE = 5;
    private static final float MAX_SCALE_RATE = 0.8f;

    private int mWidth,mHeight;
    private int mRadius;         //little circles rotate radius
    private int mDotRadius;
    private int mPadding;
    private float mDensity;
    private Paint mPaint;
    private float[] mDegrees;
    private float mDegree = 270.0f;
    private Circle[] mCircles;
    private ValueAnimator mAnimator;

    private Path mPath;
    private int mDuration; //duration of single cycle animation
    private int mSize;
    private int mIndex; //index of need show connected path


    public DxLoadingView(Context context) {
        super(context);
        init(context,null);
    }

    public DxLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public DxLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureDimension((int)(mSize * mDensity),widthMeasureSpec);
        int height = measureDimension((int)(mSize * mDensity),heightMeasureSpec);
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mRadius = (Math.min(w,h) - mPadding)/2;
        mDotRadius = mRadius/8;

        for(int i = 0; i< mCircles.length; i++){
            if(i == 4){
                mCircles[i].radius = mDotRadius;
            }else{
                mCircles[i].radius = mDotRadius * (MAX_SCALE_RATE - i * 0.1f);
            }
            mCircles[i].centerX = mWidth / 2;
            mCircles[i].centerY = (mHeight - mRadius * 2) / 2 + mDotRadius;
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for(int i = 0; i< mCircles.length; i++){
            Circle circle = mCircles[i];
            mDegrees[i] = mDegree - i * DEGREE_DISCONNECT_DIFF;

            if(mDegrees[i] < DEGREE_START){
                mDegrees[i] = DEGREE_START;
            }

            if(mDegrees[i] > DEGREE_END){
                mDegrees[i] = DEGREE_END;
            }

            if(mDegree <= DEGREE_START + (mCircles.length - 1) * DEGREE_DISCONNECT_DIFF){
                mIndex = (int)(mDegree - DEGREE_START) / DEGREE_DISCONNECT_DIFF;
            }

            if(mDegree > DEGREE_END - DEGREE_DISCONNECT_DIFF  + i * DEGREE_DISCONNECT_DIFF){
                mIndex = i;
            }

            float radian = mDegrees[i] / 180.0f * (float)Math.PI;
            mCircles[i].centerX = mWidth / 2 + (float)Math.cos(radian) * (mRadius - mDotRadius);
            mCircles[i].centerY = mHeight / 2 + (float)Math.sin(radian) * (mRadius - mDotRadius);
            canvas.drawCircle(circle.centerX,circle.centerY,circle.radius,mPaint);
        }

        if(mDegree > DEGREE_START && mDegree <= DEGREE_START + (mCircles.length - 1) * DEGREE_DISCONNECT_DIFF){
            updatePathStart(canvas);
        }

        if(mDegree >= DEGREE_END){
            updatePathEnd(canvas);
        }
    }


    //start loading Animation
    public void startAnimation(){
        if(mAnimator != null && mAnimator.isRunning()){
            return;
        }
        playAnimation();
    }

    //cancel loading animation
    public void cancelAnimation(){
        if(mAnimator != null && mAnimator.isRunning()){
            mAnimator.cancel();
            reset();
        }
    }


    private void init(Context context,AttributeSet attrs){

        int color;
        if(Build.VERSION.SDK_INT >= 23){
            color  =  context.getResources().getColor(R.color.colorPrimary,context.getTheme());
        } else{
            color =  ContextCompat.getColor(context,R.color.colorPrimary);
        }

        if(attrs != null){
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DxLoadingView, 0, 0);
            color = ta.getInt(R.styleable.DxLoadingView_loadingColor,color);
            mDuration = ta.getInt(R.styleable.DxLoadingView_animationDuration,3000);
            int size = ta.getInt(R.styleable.DxLoadingView_loadingViewSize,0);
            mSize = size == 0 ? SIZE_LARGE : SIZE_SMALL;
            ta.recycle();
        }

        mDensity = getResources().getDisplayMetrics().density;
        mPadding = (int)(2 * mDensity);
        mIndex = 0;

        mDegrees = new float[COUNT_CIRCLE];
        mCircles = new Circle[COUNT_CIRCLE];
        for(int i=0;i<5;i++){
            mCircles[i] = new Circle();
            mDegrees[i] = DEGREE_START;
        }

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(color);

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

    //draw connection path between two circle at animation start
    private void updatePathStart(Canvas canvas){

        if(mIndex == 4){
            return;
        }

        float mDegree = mDegrees[mIndex];
        Circle circle = mCircles[mIndex];
        //rotated degree
        float currentRotatedDegree = mDegree - DEGREE_START;

        float progress = currentRotatedDegree / DEGREE_DISCONNECT_DIFF;

        float diffDegree = getDegree(mCircles[4].radius);
        //scale the last circle radius
        float currentRadius = mIndex == 0 ? mDotRadius : mDotRadius * (MAX_SCALE_RATE - mIndex * 0.1f);
        float destRadius = mDotRadius * (MAX_SCALE_RATE - (mIndex+1) * 0.1f);
        mCircles[4].radius = currentRadius - progress * (currentRadius - destRadius);

        if(currentRotatedDegree < diffDegree || progress > 1){
           return;
        }

        if(mPath == null){
            mPath = new Path();
        }else{
            mPath.reset();
        }

        //draw two oblique lines and arcs
        float d1 = 300 + currentRotatedDegree / 2;
        float[] p1t = getPositionInArc(d1, mCircles[4].centerX, mCircles[4].centerY, mCircles[4].radius);
        float[] p1b = getPositionInArc(d1 + (180 - progress * 60), mCircles[4].centerX, mCircles[4].centerY, mCircles[4].radius);

        float d2 = 270 - currentRotatedDegree / 2;
        float[] p2t = getPositionInArc(d2, circle.centerX, circle.centerY, circle.radius);
        float[] p2b = getPositionInArc(d2 - (180.0f - progress * 60), circle.centerX, circle.centerY, circle.radius);

        float centerDegree = DEGREE_START + currentRotatedDegree / 2;

        float avgLength = (currentRadius+destRadius) / 2;
        float rc = mRadius - mDotRadius - avgLength * (float)Math.sin(currentRotatedDegree / 180.0 * Math.PI);

        float progress2 = (currentRotatedDegree - diffDegree) / (30.0f - diffDegree);
        float distance1 = getDistance(p1t,p1b);
        float distance2 = getDistance(p2t,p2b);
        float r1 = rc - distance1/2 + progress2 * distance1;
        float r2 = rc + distance2/2 - progress2 * distance2;

        float[] csp = getPositionInArc(centerDegree,mWidth/2,mHeight/2,r1);
        float[] clp = getPositionInArc(centerDegree,mWidth/2,mHeight/2,r2);

        mPath.moveTo(p1t[0],p1t[1]);
        mPath.lineTo(p1b[0],p1b[1]);
        mPath.quadTo(csp[0],csp[1],p2b[0],p2b[1]);
        mPath.lineTo(p2t[0],p2t[1]);
        mPath.quadTo(clp[0],clp[1],p1t[0],p1t[1]);

        canvas.drawPath(mPath,mPaint);
    }

    //draw connection path between two circle at animation end
    private void updatePathEnd(Canvas canvas){

        float mDegree = mDegrees[mIndex];

        if(mDegree < DEGREE_END - DEGREE_DISCONNECT_DIFF || mIndex == 0 ){
           return;
        }

        float currentRotatedDegree = mDegree - DEGREE_END + DEGREE_DISCONNECT_DIFF;
        float progress = currentRotatedDegree / DEGREE_DISCONNECT_DIFF;
        float diffRadius = mDotRadius * MAX_SCALE_RATE / 4 / 4;
        float cRadius = mDotRadius * MAX_SCALE_RATE + mIndex * diffRadius;
        float diffDegree = getDegree(mCircles[0].radius);
        mCircles[0].radius = cRadius - diffRadius + progress * diffRadius;

        if(currentRotatedDegree > DEGREE_DISCONNECT_DIFF - diffDegree){
            return;
        }

        if(mPath == null){
            mPath = new Path();
        }else{
            mPath.reset();
        }

        Circle circle = mCircles[mIndex];

        float d1 = DEGREE_END - MAX_SCALE_RATE / 2 + currentRotatedDegree / 2;
        float[] p1t = getPositionInArc(d1,mCircles[0].centerX,mCircles[0].centerY,mCircles[0].radius);
        float[] p1b = getPositionInArc(d1 - 120,mCircles[0].centerX,mCircles[0].centerY,mCircles[0].radius);

        float d2 = DEGREE_START + MAX_SCALE_RATE / 2 + currentRotatedDegree / 2;
        float[] p2t = getPositionInArc(d2,circle.centerX,circle.centerY,circle.radius);
        float[] p2b = getPositionInArc(d2 + 120,circle.centerX,circle.centerY,circle.radius);

        float centerDegree = DEGREE_END - DEGREE_DISCONNECT_DIFF / 2 + currentRotatedDegree / 2;

        float avgLength = (mCircles[0].radius + circle.radius)/2;
        float mRadius1 = mRadius - mCircles[0].radius - avgLength * (float)Math.sin(currentRotatedDegree / 180.0 * Math.PI);

        float progress2 = currentRotatedDegree / (DEGREE_DISCONNECT_DIFF - diffDegree);
        float diff = (getDistance(p1t,p1b) + getDistance(p2t,p2b))/2;
        float r1 = mRadius1 + diff/2 - progress2 * diff;
        float r2 = mRadius1 - diff/2 + progress2 * diff;

        float[] clp = getPositionInArc(centerDegree,mWidth/2,mHeight/2,r1);
        float[] csp = getPositionInArc(centerDegree,mWidth/2,mHeight/2,r2);

        mPath.moveTo(p1t[0],p1t[1]);
        mPath.lineTo(p1b[0],p1b[1]);
        mPath.quadTo(clp[0],clp[1],p2b[0],p2b[1]);
        mPath.lineTo(p2t[0],p2t[1]);
        mPath.quadTo(csp[0],csp[1],p1t[0],p1t[1]);

        canvas.drawPath(mPath,mPaint);
    }

    //start INFINITE repeat animation
    private void playAnimation(){
        mAnimator = ValueAnimator.ofFloat(DEGREE_START,DEGREE_END + (COUNT_CIRCLE - 1) * DEGREE_DISCONNECT_DIFF);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mDegree = (Float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        mAnimator.setDuration(mDuration);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                reset();
            }
        });
        mAnimator.start();

    }

    private void reset(){
        mIndex = 0;
        mDegree = 270f;
        mCircles[4].radius = mDotRadius;
        mCircles[0].radius = mDotRadius * MAX_SCALE_RATE;
        invalidate();
    }

    //get the position at arc with degree
    private float[] getPositionInArc(float degree, float centerX, float centerY,float radius){
        float radian = (float) (degree / 180.0 * Math.PI);
        float x = centerX + (float)Math.cos(radian) * radius;
        float y = centerY + (float)Math.sin(radian) * radius;
        return new float[]{x,y};
    }

    //convert the arc length to degree in circle
    private float getDegree(float length){
        double tan = length / (mRadius - mDotRadius);
        return (float) Math.toDegrees(Math.atan(tan));
    }

    //get distance of two point
    private float getDistance(float[] point1,float[] point2){
        float xDiff = Math.abs(point1[0]-point2[0]);
        float yDiff = Math.abs(point1[1]-point2[1]);
        return (float) Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }

    private class Circle{
        public float centerX;
        public float centerY;
        public float radius;
    }


}
