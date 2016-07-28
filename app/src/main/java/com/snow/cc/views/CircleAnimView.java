package com.snow.cc.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.snow.cc.R;


public class CircleAnimView extends View {
    class OpenAnimation extends Animation {
        float sweepAngle;

        public OpenAnimation(int sweepAngle, long duration) {
            super();
            this.sweepAngle = ((float) sweepAngle);
            this.setDuration(duration);
            this.setInterpolator(new AccelerateDecelerateInterpolator());
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            CircleAnimView.this.currentAngle = this.sweepAngle * interpolatedTime;
            CircleAnimView.this.invalidate();
            Log.e(TAG, "OpenAnimation :" + interpolatedTime + "  " + currentAngle);
            if (1f - interpolatedTime < FLOAT_SMALL) {//animation is end!
                inOpeningAnimation = false;
                if (showProcessingAnimation && updateProgress != null) {
                    showProcessingAnimation(updateProgress);
                }
            }
        }
    }

    class ProcessingAnimation extends Animation {
        float sweepAngle;
        UpdateProgress updateProgress;


        private ProcessingAnimation(long duration, UpdateProgress updateProgress) {
            super();
            this.updateProgress = updateProgress;
            this.updateProgressAngle();
            this.setDuration(duration);
            this.setRepeatMode(1);
            this.setRepeatCount(-1);
        }

        protected void applyTransformation(float interpolatedTime, Transformation t) {
            CircleAnimView.this.currentAngle = this.sweepAngle * interpolatedTime;
            CircleAnimView.this.invalidate();
            if (1f - interpolatedTime < FLOAT_SMALL) {//animation is end!
                this.updateProgressAngle();
            }
        }

        private void updateProgressAngle() {
            this.sweepAngle = this.updateProgress.updateAngle();
            CircleAnimView.this.endAngle = this.sweepAngle;
        }
    }

    public interface UpdateProgress {
        float updateAngle();
    }

    private static final boolean DEBUG = false;
    public static final int DURATION = 2000;
    public static final int DURATION_CHARGING = 4500;
    public static final float FLOAT_SMALL = 0.001f;
    public static final double RATE_CIRCLE = 0.9;
    public static final double RATE_RADIUS_ENDPOINT = 0.07;
    public static final double RATE_RADIUS_PAINT_WIDTH = 0.03;
    private static final String TAG = CircleAnimView.class.getSimpleName();
    public static final int ZERO_ANGLE = -90;
    private float bottom;
    private float centerX;
    private float centerY;
    float currentAngle = 0f;
    float endAngle;
    private Paint endPointPaint;
    private int endPointRadius;
    private float endX;
    private float endY;
    private boolean inOpeningAnimation;
    private float left;
    private int paintWidth;
    private ProcessingAnimation processingAnimation;
    private int progressColor;
    private final Paint progressPaint;
    private float radius;
    private float right;
    private boolean showProcessingAnimation;
    private float top;
    private UpdateProgress updateProgress;


    public CircleAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.progressPaint = new Paint(1);
        this.endPointPaint = new Paint(1);
        this.progressColor = this.getResources().getColor(R.color.progressColor);
        this.showProcessingAnimation = false;
        this.inOpeningAnimation = false;
        this.initializePaint();
    }

    private void computePosition() {
        int w = ((int) ((((double) this.getWidth())) * RATE_CIRCLE));//圆环所在区域宽度
        int h = ((int) ((((double) this.getHeight())) * RATE_CIRCLE));//圆环所在区域高度
        int diameter = w > h ? h : w;//调整圆环的直径
        this.centerX = ((float) (this.getWidth() / 2));
        this.centerY = ((float) (this.getHeight() / 2));
        this.radius = ((float) (diameter / 2));//环形进度的半径
        this.endPointRadius = ((int) ((((double) this.radius)) * RATE_RADIUS_ENDPOINT));
        this.paintWidth = ((int) ((((double) this.radius)) * RATE_RADIUS_PAINT_WIDTH));
        this.left = this.centerX - this.radius;
        this.right = this.centerX + this.radius;
        this.top = this.centerY - this.radius;
        this.bottom = this.centerY + this.radius;
    }

    private void drawProgressBarArc(Canvas canvas) {
        canvas.drawArc(new RectF(this.left, this.top, this.right, this.bottom), ZERO_ANGLE, this.currentAngle,
                false, this.progressPaint);
    }

    @TargetApi(value = 11)
    private void drawProgressBarPoint(Canvas canvas) {
        float angle = inOpeningAnimation ? currentAngle : endAngle;
        this.endX = (float) Math.sin(Math.toRadians(angle)) * radius + centerX;
        this.endY = (float) Math.sin(Math.toRadians(angle) - 1.570796) * radius + centerY;
        canvas.drawCircle(endX, endY, ((float) endPointRadius), endPointPaint);
    }

    @TargetApi(value = 11)
    private void initializePaint() {
        this.progressPaint.setColor(this.progressColor);
        this.progressPaint.setStyle(Paint.Style.STROKE);
        this.progressPaint.setStrokeWidth(((float) this.paintWidth));
        this.progressPaint.setStrokeCap(Paint.Cap.ROUND);
        this.endPointPaint.setColor(this.getResources().getColor(R.color.progressColor));
        this.endPointPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.computePosition();//计算位置
        this.initializePaint();//初始化画笔
        this.drawProgressBarArc(canvas);//
        this.drawProgressBarPoint(canvas);
    }

    public void setProcessingAnimation(boolean setAnimation, UpdateProgress updateProgress) {
        if (!setAnimation || updateProgress != null) {
            this.showProcessingAnimation = setAnimation;
            this.updateProgress = updateProgress;
        }
    }

    public void showProcessingAnimation(UpdateProgress updateProgress) {
        if (this.processingAnimation == null) {
            this.processingAnimation = new ProcessingAnimation(DURATION_CHARGING, updateProgress);
            this.startAnimation(this.processingAnimation);
        }
    }

    public void startAnimation(int angle, Animation.AnimationListener listener) {
        this.currentAngle = ((float) angle);
        this.endAngle = ((float) angle);
        this.inOpeningAnimation = true;
        OpenAnimation animation = new OpenAnimation(angle, DURATION);
        animation.setAnimationListener(listener);
        this.startAnimation(animation);
    }

    public synchronized void stopProcessingAnimation() {
        if (this.processingAnimation != null) {
            this.clearAnimation();
            this.processingAnimation.cancel();
            this.processingAnimation = null;
        }
    }
}

