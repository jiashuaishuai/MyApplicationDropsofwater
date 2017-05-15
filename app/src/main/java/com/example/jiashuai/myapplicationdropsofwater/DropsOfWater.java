package com.example.jiashuai.myapplicationdropsofwater;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by JiaShuai on 2017/5/12.
 */

public class DropsOfWater extends View {
    private int frame = 3;//
    private Paint framePaint;//边框画笔
    private static final String TAG = "DropsOfWater";
    private int maxD = 50;
    private Point maxCentral, smallCentral;//两圆心
    private Paint mPaint;//画笔
    private int maxCircleRadius, smallCircleRadius;//俩圆半径
    private Point leftStartPoint, leftEndPoint, leftAssPoint;//左边曲线
    private Point rightStartPoint, rightEndPoint, rightAssPoint;//右边曲线
    private Path mBezierPath;//曲线路径
    private Paint mBezierPaint;//
    private int slidingDistance = 0;

    public void setSlidingDistance(int slidingDistance) {
        this.slidingDistance = slidingDistance;
        initPoint();
    }

    public float getSlidingDistance() {
        return slidingDistance;
    }

    public DropsOfWater(Context context) {
        this(context, null);
    }

    public DropsOfWater(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropsOfWater(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        maxCentral = new Point(100, 100);//
        smallCentral = new Point(100, 100);//
        //圆画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(Color.parseColor("#a1a1a1"));
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        framePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        framePaint.setDither(true);
        framePaint.setStrokeCap(Paint.Cap.ROUND);
        framePaint.setColor(Color.parseColor("#dcdcdc"));
        framePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        //贝塞尔曲线画笔
        mBezierPath = new Path();
        mBezierPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBezierPaint.setColor(Color.parseColor("#a1a1a1"));
        mBezierPaint.setStyle(Paint.Style.FILL);

        leftStartPoint = new Point();//大圆圆心.x-半径，大圆圆心.y
        leftEndPoint = new Point();//小圆圆心.x-小圆半径，小圆.y
        leftAssPoint = new Point();//结束点.x，下拉距离/2

        rightStartPoint = new Point();
        rightEndPoint = new Point();
        rightAssPoint = new Point();

        initPoint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drowFrame(canvas);
        drowDrops(canvas);

    }

    private void drowDrops(Canvas canvas) {
        //绘制圆
        canvas.drawCircle(maxCentral.x, maxCentral.y, maxCircleRadius, mPaint);
        canvas.drawCircle(smallCentral.x, smallCentral.y, smallCircleRadius, mPaint);
        mBezierPath.reset();
        //绘制曲线
        mBezierPath.moveTo(rightStartPoint.x, rightStartPoint.y);
        mBezierPath.quadTo(rightAssPoint.x, rightAssPoint.y, rightEndPoint.x, rightEndPoint.y);

        mBezierPath.lineTo(leftEndPoint.x, leftEndPoint.y);
        mBezierPath.quadTo(leftAssPoint.x, leftAssPoint.y, leftStartPoint.x, leftStartPoint.y);
        canvas.drawPath(mBezierPath, mBezierPaint);
    }

    //绘制边框
    private void drowFrame(Canvas canvas) {
        //绘制圆
        canvas.drawCircle(maxCentral.x, maxCentral.y, maxCircleRadius + frame, framePaint);
        canvas.drawCircle(smallCentral.x, smallCentral.y, smallCircleRadius + frame, framePaint);
        mBezierPath.reset();
        //绘制曲线
        mBezierPath.moveTo(rightStartPoint.x + frame, rightStartPoint.y);
        mBezierPath.quadTo(rightAssPoint.x + frame, rightAssPoint.y, rightEndPoint.x + frame, rightEndPoint.y);

        mBezierPath.lineTo(leftEndPoint.x - frame, leftEndPoint.y);
        mBezierPath.quadTo(leftAssPoint.x - frame, leftAssPoint.y, leftStartPoint.x - frame, leftStartPoint.y);
        canvas.drawPath(mBezierPath, framePaint);

    }

    //计算坐标
    private void initPoint() {
        slidingDistance = Math.min(slidingDistance, maxD * 2 - 15);
        smallCentral.y = maxCentral.y + slidingDistance;
        maxCircleRadius = maxD - slidingDistance / 3;//大圆的半径
        smallCircleRadius = maxD - slidingDistance / 2;//小圆半径
        ///////两条贝塞尔曲线各三点
        leftStartPoint.set(maxCentral.x - maxCircleRadius, maxCentral.y);//大圆圆心.x-半径，大圆圆心.y
        leftEndPoint.set(smallCentral.x - smallCircleRadius, smallCentral.y);//小圆圆心.x-小圆半径，小圆.y
        leftAssPoint.set(leftEndPoint.x, (leftStartPoint.y + leftEndPoint.y) / 2);//结束点.x，下拉距离/2

        rightStartPoint.set(maxCentral.x + maxCircleRadius, maxCentral.y);
        rightEndPoint.set(smallCentral.x + smallCircleRadius, smallCentral.y);
        rightAssPoint.set(rightEndPoint.x, (rightStartPoint.y + rightEndPoint.y) / 2);
        invalidate();
    }


    protected int dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    protected int sp2px(float sp) {
        final float scale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scale + 0.5f);
    }

    /**
     * 启动变幻测试
     */
    public void start() {
        ObjectAnimator animator = ObjectAnimator.ofInt(this, "slidingDistance", 0, 100);
        animator.setDuration(500);
        animator.start();

    }

    public void end() {
        ObjectAnimator animator = ObjectAnimator.ofInt(this, "slidingDistance", 100, 0);
        animator.setDuration(500);
        animator.start();

    }

    public void end(int dis) {
        ObjectAnimator animator = ObjectAnimator.ofInt(this, "slidingDistance", dis, 0);
        animator.setDuration(100);
        animator.start();

    }


    private int downPoint;
    private int movePoint;
    private int upPoint;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downPoint = (int) event.getY();
                Log.e(TAG, "downPoint   " + downPoint);
                break;
            case MotionEvent.ACTION_MOVE:
                movePoint = (int) event.getY();
                slidingDistance = (int) ((movePoint - downPoint) * 0.3);
                slidingDistance = Math.max(slidingDistance, 0);
                Log.e(TAG, "movePoint   " + movePoint);
                Log.e(TAG, "slidingDistance   " + slidingDistance);
                initPoint();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                upPoint = (int) event.getY();
                Log.e(TAG, "upPoint   " + upPoint);
                end(slidingDistance);
                break;
        }
        return true;
    }
}
