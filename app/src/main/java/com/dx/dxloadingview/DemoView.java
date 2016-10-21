package com.dx.dxloadingview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Miutrip_iMac on 2016/10/21.
 */

public class DemoView extends View{


    private Paint mPaint = new Paint();
    private Path mPath = new Path();

    public DemoView(Context context) {
        super(context);
        init();
    }

    public DemoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DemoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mPaint.setStrokeWidth(3);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.RED);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPath.reset();

        mPath.addCircle(300,300,5, Path.Direction.CCW);

        mPath.moveTo(100,100);
        mPath.lineTo(500,100);

        mPath.moveTo(100,500);
        mPath.lineTo(500,500);



        mPath.quadTo(300,100,100,500);

        canvas.drawPath(mPath,mPaint);
    }
}
