package com.zzh.widget.blurview;

import static com.zzh.widget.blurview.PreDrawBlurController.TRANSPARENT;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import zzh.source.csso.R;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import android.os.Build;

/*
*  值得注意的是，这个无法渲染在线程以外的布局
*  渲染圆角需要手动控制 
*  Date 2024-07-20
*/
public class BlurView extends FrameLayout {
    private static final String TAG = BlurView.class.getSimpleName();
    BlurController blurController = new NoOpController();
    @ColorInt
    private int overlayColor;
    private float cornerRadius;
    private Path clipPath;
    private RectF rectF;

    public BlurView(Context context) {
        super(context);
        init(null, 0);
    }

    public BlurView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BlurView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BlurView, defStyleAttr, 0);
        overlayColor = a.getColor(R.styleable.BlurView_blurOverlayColor, TRANSPARENT);
        cornerRadius = a.getDimension(R.styleable.BlurView_cornerRadius, 0);
        a.recycle();

        clipPath = new Path();
        rectF = new RectF();
    }

    @Override
    public void draw(Canvas canvas) {
        boolean shouldDraw = blurController.draw(canvas);
        if (shouldDraw) {
            int save = canvas.save();
            clipPath.reset();
            rectF.set(0, 0, getWidth(), getHeight());
            clipPath.addRoundRect(rectF, cornerRadius, cornerRadius, Path.Direction.CW);
            canvas.clipPath(clipPath);
            super.draw(canvas);
            canvas.restoreToCount(save);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        blurController.updateBlurViewSize();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        blurController.setBlurAutoUpdate(false);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isHardwareAccelerated()) {
            Log.e(TAG, "BlurView can't be used in not hardware-accelerated window!");
        } else {
            blurController.setBlurAutoUpdate(true);
        }
    }

    public BlurViewFacade setupWith(@NonNull ViewGroup rootView, BlurAlgorithm algorithm) {
        this.blurController.destroy();
        BlurController blurController = new PreDrawBlurController(this, rootView, overlayColor, algorithm);
        this.blurController = blurController;

        return blurController;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public BlurViewFacade setupWith(@NonNull ViewGroup rootView) {
        return setupWith(rootView, getBlurAlgorithm());
    }

    public BlurViewFacade setBlurRadius(float radius) {
        return blurController.setBlurRadius(radius);
    }

    public BlurViewFacade setOverlayColor(@ColorInt int overlayColor) {
        this.overlayColor = overlayColor;
        return blurController.setOverlayColor(overlayColor);
    }

    public BlurViewFacade setBlurAutoUpdate(boolean enabled) {
        return blurController.setBlurAutoUpdate(enabled);
    }

    public BlurViewFacade setBlurEnabled(boolean enabled) {
        return blurController.setBlurEnabled(enabled);
    }

    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private BlurAlgorithm getBlurAlgorithm() {
        BlurAlgorithm algorithm;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            algorithm = new RenderEffectBlur();
        } else {
            algorithm = new RenderScriptBlur(getContext());
        }
        return algorithm;
    }
}
/*
<declare-styleable name="BlurView">
    <attr name="blurOverlayColor" format="color" />
    <attr name="cornerRadius" format="dimension" />
</declare-styleable>
*/