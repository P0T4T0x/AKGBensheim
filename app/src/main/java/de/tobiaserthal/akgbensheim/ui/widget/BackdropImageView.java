package de.tobiaserthal.akgbensheim.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.github.ksoichiro.android.observablescrollview.ScrollUtils;

import de.tobiaserthal.akgbensheim.data.Log;

public class BackdropImageView extends ImageView {
    private static final int MIN_SCRIM_ALPHA = 0x00;
    private static final int MAX_SCRIM_ALPHA = 0xFF;
    private static final int SCRIM_ALPHA_DIFF = MAX_SCRIM_ALPHA - MIN_SCRIM_ALPHA;

    private float mScrimDarkness;
    private float factor;
    private int mScrimColor = Color.BLACK;
    private int mImageOffset;

    private final Paint mScrimPaint;

    public BackdropImageView(Context context) {
        super(context);
        mScrimPaint = new Paint();
        factor = 2;
    }

    public BackdropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScrimPaint = new Paint();
        factor = 2;
    }

    public void setFactor(float factor) {
        this.factor = factor;
    }

    public void setScrimColor(int scrimColor) {
        if (mScrimColor != scrimColor) {
            mScrimColor = scrimColor;

            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setProgress(int offset, float scrim) {
        mScrimDarkness = ScrollUtils.getFloat(scrim, 0, 1);
        mImageOffset = (int) (offset * factor);

        ViewCompat.postInvalidateOnAnimation(this);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        // Update the scrim paint
        mScrimPaint.setColor(ColorUtils.setAlphaComponent(mScrimColor,
                MIN_SCRIM_ALPHA + (int) (SCRIM_ALPHA_DIFF * mScrimDarkness)));

        if (mImageOffset != 0) {
            canvas.save();
            canvas.translate(0f, mImageOffset);
            canvas.clipRect(0, 0, canvas.getWidth(), canvas.getHeight() + mImageOffset + 1);

            super.onDraw(canvas);

            canvas.restore();
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mScrimPaint);
        } else {
            super.onDraw(canvas);
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mScrimPaint);
        }
    }
}
