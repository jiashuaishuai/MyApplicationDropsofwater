package com.example.jiashuai.myapplicationdropsofwater;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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
    private static final String TAG = "DropsOfWater";
    private int frame = 0;//边框宽度
    private Paint framePaint;//边框画笔
    private int shadow = 3;//阴影宽度
    private Paint shadowPaint;//阴影


    private int maximumCircleRadius = 80;//最大圆半径
    private int maxDistance = maximumCircleRadius * 2 - 15;//最大滑动距离


    private Paint mPaint;//两圆画笔
    private Paint mBezierPaint;//贝塞尔曲线画笔
    private int maxCircleRadius, smallCircleRadius;//俩圆半径
    private Point maxCentral, smallCentral;//两圆心
    private Point leftStartPoint, leftEndPoint, leftAssPoint;//左边曲线
    private Point rightStartPoint, rightEndPoint, rightAssPoint;//右边曲线
    private Path mBezierPath;//曲线路径
    private int slidingDistance = 0;//当前滑动距离

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


        //贝塞尔曲线画笔
        mBezierPath = new Path();
        mBezierPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBezierPaint.setDither(true);
        mBezierPaint.setStrokeCap(Paint.Cap.ROUND);
        mBezierPaint.setColor(Color.parseColor("#a1a1a1"));
        mBezierPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        //边框
        framePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        framePaint.setDither(true);
        framePaint.setStrokeCap(Paint.Cap.ROUND);
        framePaint.setColor(Color.parseColor("#666666"));
        framePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        //阴影
        shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadowPaint.setDither(true);
        shadowPaint.setStrokeCap(Paint.Cap.ROUND);
        shadowPaint.setColor(Color.parseColor("#888888"));
        shadowPaint.setStyle(Paint.Style.FILL_AND_STROKE);

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
        drowShadow(canvas);
        drowDrops(canvas);
        drowRefreshIcon(canvas);
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

    //绘制阴影
    private void drowShadow(Canvas canvas) {
        //绘制圆
        canvas.drawCircle(maxCentral.x + shadow, maxCentral.y, maxCircleRadius, shadowPaint);
        canvas.drawCircle(smallCentral.x + shadow, smallCentral.y, smallCircleRadius, shadowPaint);
        mBezierPath.reset();
        //绘制曲线
        mBezierPath.moveTo(rightStartPoint.x + shadow, rightStartPoint.y);
        mBezierPath.quadTo(rightAssPoint.x + shadow, rightAssPoint.y, rightEndPoint.x + shadow, rightEndPoint.y);

        mBezierPath.lineTo(leftEndPoint.x, leftEndPoint.y);
        mBezierPath.quadTo(leftAssPoint.x, leftAssPoint.y, leftStartPoint.x, leftStartPoint.y);
        canvas.drawPath(mBezierPath, shadowPaint);
    }

    //绘制刷新图片
    private void drowRefreshIcon(Canvas canvas) {
        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.default_ptr_rotate);
        float bw = bitmap.getWidth();//这里必须是float否则bw/bh为0
        float bh = bitmap.getHeight();
        bitmap = Bitmap.createScaledBitmap(bitmap, (int) (maxCircleRadius * (bw / bh)), (int) (maxCircleRadius * (bh / bw)), true);//按比例设置bitmap大小
        float deg = slidingDistance / (float) (maxDistance);//计算滑动比例
        Matrix matrix = new Matrix();
        matrix.setRotate(360 * deg, bw / 2, bh / 2);//设置旋转角度，以bitmap为中心旋转
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);//设置新的bitmap
        canvas.drawBitmap(bitmap, maxCentral.x - bitmap.getWidth() / 2, maxCentral.y - bitmap.getHeight() / 2, mPaint);//绘制
    }

    //计算坐标
    private void initPoint() {
        slidingDistance = Math.min(slidingDistance, maxDistance);
        smallCentral.y = maxCentral.y + slidingDistance;
        maxCircleRadius = maximumCircleRadius - slidingDistance / 4;//大圆的半径
        smallCircleRadius = (int) (maximumCircleRadius - slidingDistance / 2.2);//小圆半径
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
        ObjectAnimator animator = ObjectAnimator.ofInt(this, "slidingDistance", 0, maxDistance);
        animator.setDuration(500);
        animator.start();

    }

    public void end() {
        ObjectAnimator animator = ObjectAnimator.ofInt(this, "slidingDistance", maxDistance, 0);
        animator.setDuration(500);
        animator.start();

    }

    public void end(int dis) {
        ObjectAnimator animator = ObjectAnimator.ofInt(this, "slidingDistance", dis, 0);
        animator.setDuration(300);
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
