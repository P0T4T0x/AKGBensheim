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

public class BackdropImageView extends ImageView {
    private static final int MIN_SCRIM_ALPHA = 0x00;
    private static final int MAX_SCRIM_ALPHA = 0xFF;
    private static final int SCRIM_ALPHA_DIFF = MAX_SCRIM_ALPHA - MIN_SCRIM_ALPHA;

    private float mScrimDarkness;
    private int mScrimColor = Color.BLACK;
    private int mScrollOffset;
    private int mImageOffset;

    private final Paint mScrimPaint;

    public BackdropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScrimPaint = new Paint();
    }

    private void setScrollOffset(int offset) {
        if (offset != mScrollOffset) {
            mScrollOffset = offset;
            mImageOffset = -offset / 2;
            offsetTopAndBottom(offset - getTop());

            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (mScrollOffset != 0) {
            offsetTopAndBottom(mScrollOffset - getTop());
        }
    }

    public void setScrimColor(int scrimColor) {
        if (mScrimColor != scrimColor) {
            mScrimColor = scrimColor;

            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setProgress(int offset, float scrim) {
        mScrimDarkness = ScrollUtils.getFloat(scrim, 0, 1);
        setScrollOffset(offset);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        // Update the scrim paint
        mScrimPaint.setColor(ColorUtils.setAlphaComponent(mScrimColor,
                MIN_SCRIM_ALPHA + (int) (SCRIM_ALPHA_DIFF * mScrimDarkness)));

        if (mImageOffset != 0) {
            canvas.save();
            canvas.translate(0f, mImageOffset);
            canvas.clipRect(0f, 0f, canvas.getWidth(), canvas.getHeight() + mImageOffset + 1);
            super.onDraw(canvas);
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mScrimPaint);
            canvas.restore();
        } else {
            super.onDraw(canvas);
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mScrimPaint);
        }
    }
}
