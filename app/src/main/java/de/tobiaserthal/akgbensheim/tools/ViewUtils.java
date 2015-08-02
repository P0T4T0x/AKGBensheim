package de.tobiaserthal.akgbensheim.tools;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.TextView;

public class ViewUtils {
    public static int getPixelSize(Context ctx, int attr) {
        TypedValue typedValue = new TypedValue();
        int[] sizeAttrs = new int[] {attr};
        int indexOfSizeAttr = 0;

        TypedArray typedArray = ctx.obtainStyledAttributes(typedValue.data, sizeAttrs);

        int pixelSize = typedArray.getDimensionPixelSize(indexOfSizeAttr, -1);
        typedArray.recycle();

        return pixelSize;
    }

    public static int getColor(Context ctx, int attr) {
        TypedValue typedValue = new TypedValue();
        int[] colorAttrs = new int[] {attr};
        int indexOfColorAttr = 0;

        TypedArray typedArray = ctx.obtainStyledAttributes(typedValue.data, colorAttrs);
        int color = typedArray.getColor(indexOfColorAttr, Color.TRANSPARENT);
        typedArray.recycle();

        return color;
    }

    public static int[] getColorArray(Context ctx, int resId) {
        final TypedArray array = ctx.getResources().obtainTypedArray(resId);
        final int[] colors = new int[array.length()];

        for(int i = 0; i < array.length(); i++) {
            colors[i] = array.getColor(i, 0);
        }

        array.recycle();
        return colors;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void animateRevealShow(View viewRoot, int duration) {
        int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
        int finalRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0, finalRadius);
        viewRoot.setVisibility(View.VISIBLE);
        anim.setDuration(duration);
        anim.start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void animateRevealHide(final View viewRoot, int duration) {
        int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
        int initialRadius = viewRoot.getWidth();

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, initialRadius, 0);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                viewRoot.setVisibility(View.INVISIBLE);
            }
        });
        anim.setDuration(duration);
        anim.start();
    }
}
