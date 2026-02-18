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

public class ProgressBarRound extends View {
  private int mViewWidth;
  private int mViewHeight;
  private final float mStartAngle = 90; // 始终从顶部开始（默认是手表的“3点钟”位置）
  // 注意 顶部是-90
  private float mSweepAngle = 0; // 从 mStartAngle 开始扫过的角度
  private float mMaxSweepAngle = 360; // 最大扫过角度，即完整圆的角度
  private int mStrokeWidth = 5; // 进度条的宽度
  private int mAnimationDuration = 600; // 进度变化的动画时长
  private int mMaxProgress = 100; // 最大进度值
  private boolean mDrawText = true; // 设置为 true 表示绘制进度文本
  private boolean mRoundedCorners = true; // 设置为 true 表示进度条末端为圆角
  private int mProgressColor = Color.BLACK; // 进度条颜色
  private int mTextColor = Color.WHITE; // 进度文本颜色
  private final Paint mPaint; // 预先分配 Paint 对象，避免在 onDraw 中重复创建

  public ProgressBarRound(Context context) {
    this(context, null);
  }

  public ProgressBarRound(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ProgressBarRound(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    initMeasurments(); // 初始化视图的宽度和高度
    drawOutlineArc(canvas); // 绘制外部弧形进度条
    if (mDrawText) {
      drawText(canvas); // 如果需要，绘制进度文本
    }
  }

  private void initMeasurments() {
    mViewWidth = getWidth(); // 获取视图宽度
    mViewHeight = getHeight(); // 获取视图高度
  }

  /*
  private void drawOutlineArc(Canvas canvas) {
    final int diameter = Math.min(mViewWidth, mViewHeight); // 获取最小的宽高值作为直径
    final float pad = mStrokeWidth / 2.0f; // 计算边距
    final RectF outerOval = new RectF(pad, pad, diameter - pad, diameter - pad); // 生成外部的弧形矩形
    mPaint.setColor(mProgressColor); // 设置进度条颜色
    mPaint.setStrokeWidth(mStrokeWidth); // 设置进度条宽度
    mPaint.setAntiAlias(true); // 启用抗锯齿
    mPaint.setStrokeCap(mRoundedCorners ? Paint.Cap.ROUND : Paint.Cap.BUTT); // 根据设置选择圆角或直角端点
    mPaint.setStyle(Paint.Style.STROKE); // 设置为描边模式
    canvas.drawArc(outerOval, mStartAngle, mSweepAngle, false, mPaint); // 绘制弧形
  }*/
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
              Color.parseColor("#62A1FA"), Color.parseColor("#78C8E1"), Color.parseColor("#62A1FA")
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
    mPaint.setTextSize(Math.min(mViewWidth, mViewHeight) / 5f); // 设置文本大小
    mPaint.setTextAlign(Paint.Align.CENTER); // 设置文本居中对齐
    mPaint.setStrokeWidth(0);
    mPaint.setColor(mTextColor); // 设置文本颜色
    mPaint.setStyle(Paint.Style.FILL); // 设置为填充模式

    // 计算文本居中的位置
    int xPos = (canvas.getWidth() / 2);
    int yPos = (int) ((canvas.getHeight() / 2) - ((mPaint.descent() + mPaint.ascent()) / 2));

    // 格式化进度值，保留小数点后两位 "%.2f h" 
    String progressText = String.format("%.2f", calcProgressFromSweepAngle(mSweepAngle));

    // 绘制格式化后的进度文本
    canvas.drawText(progressText, xPos, yPos, mPaint);
  }

  private float calcSweepAngleFromProgress(float progress) {
    return (mMaxSweepAngle / mMaxProgress) * progress; // 根据进度计算扫过的角度
  }

  private float calcProgressFromSweepAngle(float sweepAngle) {
    return (sweepAngle * mMaxProgress) / mMaxSweepAngle; // 根据扫过的角度计算进度
  }

  /**
   * 设置圆形进度条的进度
   *
   * @param progress 进度值在 0 到 100 之间
   */
  /**
   * 设置圆形进度条的进度
   *
   * @param progress 进度值在 0 到 mMaxProgress 之间，可以有小数
   */
  public void setProgress(float progress) {
    ValueAnimator animator =
        ValueAnimator.ofFloat(mSweepAngle, calcSweepAngleFromProgress(progress)); // 创建进度动画
    animator.setInterpolator(new DecelerateInterpolator()); // 设置减速插值器
    animator.setDuration(mAnimationDuration); // 设置动画时长
    animator.addUpdateListener(
        new ValueAnimator.AnimatorUpdateListener() {
          @Override
          public void onAnimationUpdate(ValueAnimator valueAnimator) {
            mSweepAngle = (float) valueAnimator.getAnimatedValue(); // 更新扫过的角度
            invalidate(); // 重新绘制视图
          }
        });
    animator.start(); // 启动动画
  }

  public void setProgressColor(int color) {
    mProgressColor = color; // 设置进度条颜色
    invalidate();
  }

  public void setProgressWidth(int width) {
    mStrokeWidth = width; // 设置进度条宽度
    invalidate();
  }

  public void setTextColor(int color) {
    mTextColor = color; // 设置文本颜色
    invalidate();
  }

  public void showProgressText(boolean show) {
    mDrawText = show; // 设置是否显示进度文本
    invalidate();
  }

  /**
   * 如果不希望进度条末端为圆角，可以切换此选项。默认是圆角。
   *
   * @param roundedCorners true 表示使用圆角，false 表示不使用。
   */
  public void useRoundedCorners(boolean roundedCorners) {
    mRoundedCorners = roundedCorners; // 设置是否使用圆角
    invalidate();
  }
}
