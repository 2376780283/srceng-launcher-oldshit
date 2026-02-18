package zzh.source.srceng.ui.widget.progress;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.graphics.SweepGradient;
import zzh.source.srceng.R;

public class ProgressBarRoundNormalofEP extends View {
  private int mViewWidth;
  private int mViewHeight;
  private final float mStartAngle =
      90; // Always start from top (default is: "3 o'clock on a watch.")
  private float mSweepAngle = 0; // How long to sweep from mStartAngle
  private float mMaxSweepAngle = 360; // Max degrees to sweep = full circle
  private int mStrokeWidth = 20; // Width of outline
  private int mAnimationDuration = 400; // Animation duration for progress change
  private int mMaxProgress = 145; // 设置进度的最大值 hl2 31个 cs起源 145个  csso 未知
  private boolean mDrawText = true; // Set to true if progress text should be drawn
  private boolean mRoundedCorners =
      true; // Set to true if rounded corners should be applied to outline ends
  private int mProgressColor = Color.BLACK; // Outline color
  private int mTextColor = Color.WHITE; // Progress text color
  private final Paint mPaint; // Allocate paint outside onDraw to avoid unnecessary object creation

  public ProgressBarRoundNormalofEP(Context context) {
    this(context, null);
  }

  public ProgressBarRoundNormalofEP(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ProgressBarRoundNormalofEP(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    initMeasurments();
    drawOutlineArc(canvas);

    if (mDrawText) {
      drawText(canvas);
    }
  }

  private void initMeasurments() {
    mViewWidth = getWidth();
    mViewHeight = getHeight();
  }

  private void drawOutlineArc(Canvas canvas) {
    final int diameter = Math.min(mViewWidth, mViewHeight); // 获取最小的宽高值作为直径
    final float pad = mStrokeWidth / 2.0f; // 计算边距
    final RectF outerOval = new RectF(pad, pad, diameter - pad, diameter - pad); // 生成外部的弧形矩形

    // 创建一个用于绘制背景路径的 Paint
    Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    backgroundPaint.setColor(Color.parseColor("#80D3D3D3")); // 半透明浅灰色 (#80 表示 50% 透明度)
    backgroundPaint.setStrokeWidth(mStrokeWidth); // 设置背景路径的宽度
    backgroundPaint.setAntiAlias(true); // 启用抗锯齿
    backgroundPaint.setStrokeCap(Paint.Cap.ROUND); // 圆角端点
    backgroundPaint.setStyle(Paint.Style.STROKE); // 设置为描边模式
    canvas.drawArc(outerOval, mStartAngle, mMaxSweepAngle, false, backgroundPaint); // 绘制完整的圆形路径

    // 创建一个用于绘制进度条的 Paint
    Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    SweepGradient gradient =
        new SweepGradient(
            mViewWidth / 2,
            mViewHeight / 2,
            new int[] {
              Color.parseColor("#639AE6"), Color.parseColor("#78C8E1"), Color.parseColor("#62A1FA")
            },
            new float[] {0f, 0.5f, 1f}); // 渐变分布
    progressPaint.setShader(gradient); // 应用渐变色到进度条
    progressPaint.setStrokeWidth(mStrokeWidth); // 设置进度条宽度
    progressPaint.setAntiAlias(true); // 启用抗锯齿
    progressPaint.setStrokeCap(mRoundedCorners ? Paint.Cap.ROUND : Paint.Cap.BUTT); // 圆角或直角端点
    progressPaint.setStyle(Paint.Style.STROKE); // 设置为描边模式
    canvas.drawArc(outerOval, mStartAngle, mSweepAngle, false, progressPaint); // 绘制弧形

    // 清除渐变效果以确保后续绘制不受影响
    progressPaint.setShader(null);
  }

  private void drawText(Canvas canvas) {
    mPaint.setTextSize(Math.min(mViewWidth, mViewHeight) / 5f);
    mPaint.setTextAlign(Paint.Align.CENTER);
    mPaint.setStrokeWidth(0);
    mPaint.setColor(mTextColor);
    mPaint.setStyle(Paint.Style.FILL); // 设置为填充模式

    // Center text
    int xPos = (canvas.getWidth() / 2);
    int yPos = (int) ((canvas.getHeight() / 2) - ((mPaint.descent() + mPaint.ascent()) / 2));

    canvas.drawText(
        calcProgressFromSweepAngle(mSweepAngle)
            + getContext().getString(R.string.srceng_launcher_Achievement),
        xPos,
        yPos,
        mPaint);
  }

  private float calcSweepAngleFromProgress(int progress) {
    return (mMaxSweepAngle / mMaxProgress) * progress;
  }

  private int calcProgressFromSweepAngle(float sweepAngle) {
    return (int) ((sweepAngle * mMaxProgress) / mMaxSweepAngle);
  }

  /**
   * Set progress of the circular progress bar.
   *
   * @param progress progress between 0 and 100.
   */
  public void setProgress(int progress) {
    ValueAnimator animator =
        ValueAnimator.ofFloat(mSweepAngle, calcSweepAngleFromProgress(progress));
    animator.setInterpolator(new DecelerateInterpolator());
    animator.setDuration(mAnimationDuration);
    animator.addUpdateListener(
        new ValueAnimator.AnimatorUpdateListener() {
          @Override
          public void onAnimationUpdate(ValueAnimator valueAnimator) {
            mSweepAngle = (float) valueAnimator.getAnimatedValue();
            invalidate();
          }
        });
    animator.start();
  }

  public void setProgressColor(int color) {
    mProgressColor = color;
    invalidate();
  }

  public void setProgressWidth(int width) {
    mStrokeWidth = width;
    invalidate();
  }

  public void setTextColor(int color) {
    mTextColor = color;
    invalidate();
  }

  public void showProgressText(boolean show) {
    mDrawText = show;
    invalidate();
  }

  /**
   * Toggle this if you don't want rounded corners on progress bar. Default is true.
   *
   * @param roundedCorners true if you want rounded corners of false otherwise.
   */
  public void useRoundedCorners(boolean roundedCorners) {
    mRoundedCorners = roundedCorners;
    invalidate();
  }
}
