package de.tobiaserthal.akgbensheim.tools;

import com.nineoldandroids.animation.Animator;

import de.tobiaserthal.akgbensheim.data.Log;

public class AnimatorAdapter implements Animator.AnimatorListener {
    public static final String TAG = "Animator";

    @Override
    public void onAnimationStart(Animator animation) {
        Log.i(TAG, "Start animation");
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        Log.i(TAG, "Ended animation");
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        Log.w(TAG, "Canceled animation!");
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        Log.d(TAG, "Repeat animation!");
    }
}
