package de.tobiaserthal.akgbensheim.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.github.ksoichiro.android.observablescrollview.TouchInterceptionFrameLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.backend.utils.Log;
import de.tobiaserthal.akgbensheim.utils.ContextHelper;
import de.tobiaserthal.akgbensheim.base.toolbar.ToolbarActivity;
import de.tobiaserthal.akgbensheim.utils.widget.BackdropImageView;

import static de.tobiaserthal.akgbensheim.R.anim.abc_fade_out;

public abstract class OverlayActivity<S extends View & Scrollable> extends ToolbarActivity
        implements TouchInterceptionFrameLayout.TouchInterceptionListener, ObservableScrollViewCallbacks {

    private static final String TAG = "OverlayActivity";

    private S scrollable;
    private ScrollState state;

    private FrameLayout containerView;
    private BackdropImageView backdropImageView;
    private TouchInterceptionFrameLayout frameLayout;

    private VelocityTracker velocityTracker;
    private float scrollYOnDownMotion;
    private float initialY;
    private float movedDistanceY;

    private int fromStatusBarColor;
    private int toStatusBarColor;
    private boolean fromSavedInstanceState;

    private AnimatorSet animatorSet = new AnimatorSet();

    private static final long ANIMATION_DURATION = 250;
    private static final Interpolator ENTER_INTERPOLATOR = new LinearOutSlowInInterpolator();
    private static final Interpolator EXIT_INTERPOLATOR = new DecelerateInterpolator();
    private static final ArgbEvaluator ARGB_EVALUATOR = new ArgbEvaluator();

    public static <T extends OverlayActivity<?>> Intent createOverlayActivity(Activity activity, Class<T> clazz) {
        Intent startIntent = new Intent(activity, clazz);
        View root = activity.findViewById(android.R.id.content);

        Rect clipRect = new Rect();
        activity.getWindow().getDecorView()
                .getWindowVisibleDisplayFrame(clipRect);

        Bitmap bitmap = Bitmap.createBitmap(
                root.getWidth(),
                root.getHeight(),
                Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(bitmap);
        canvas.drawRGB(0xEE, 0xEE, 0xEE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.translate(0, -clipRect.top / 2);
            canvas.clipRect(clipRect);
        }
        root.draw(canvas);

        try {
            File file = new File(activity.getCacheDir(), "background.jpg");
            FileOutputStream stream = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            stream.flush();
            stream.close();

            bitmap.recycle();
            startIntent.putExtra("bgBitmap", file.getPath());

            Log.d(TAG, "Rendered background image.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return startIntent;
    }

    private final Runnable runEnterAnim = new Runnable() {
        @Override
        public void run() {
            if(fromSavedInstanceState) {
                frameLayout.setVisibility(View.VISIBLE);

                frameLayout.setTranslationY(0);
                setStatusBarColor(toStatusBarColor);
            } else {
                animatorSet.cancel();
                animatorSet.removeAllListeners();
                frameLayout.setTranslationY(getScreenHeight());

                ValueAnimator animator1 = ValueAnimator.ofFloat(getCurrentTranslation(), 0);
                animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        frameLayout.setTranslationY((Float) animation.getAnimatedValue());
                    }
                });

                ValueAnimator animator2 = ValueAnimator.ofFloat(0, 1);
                animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        setScrimTo((Float) animation.getAnimatedValue());
                    }
                });

                ValueAnimator animator3 = ValueAnimator.ofObject(ARGB_EVALUATOR, fromStatusBarColor, toStatusBarColor);
                animator3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        setStatusBarColor((Integer) animation.getAnimatedValue());
                    }
                });

                animatorSet.playTogether(animator1, animator2, animator3);
                animatorSet.setDuration(ANIMATION_DURATION);
                animatorSet.setInterpolator(ENTER_INTERPOLATOR);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        onStartEnterAnimation();
                        frameLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        onEndEnterAnimation();
                    }
                });

                animatorSet.start();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);

        Bitmap screenshot = BitmapFactory.decodeFile(getIntent().getStringExtra("bgBitmap"));
        if (screenshot == null) {
            throw new IllegalArgumentException("You have to provide a valid bitmap!");
        }

        containerView = new FrameLayout(this);
        containerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        backdropImageView = new BackdropImageView(this);
        backdropImageView.setId(android.R.id.background);

        backdropImageView.setFactor(0.25f);
        backdropImageView.setScrimColor(Color.BLACK);
        backdropImageView.setImageBitmap(screenshot);
        backdropImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        containerView.addView(backdropImageView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        frameLayout = new TouchInterceptionFrameLayout(this);
        frameLayout.setScrollInterceptionListener(this);

        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            frameLayoutParams.gravity = Gravity.TOP;
        }

        containerView.addView(frameLayout, frameLayoutParams);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fromStatusBarColor = ContextHelper.getColor(this, R.attr.colorPrimaryDark);
            toStatusBarColor = Color.BLACK;
        }

        fromSavedInstanceState = savedInstanceState != null;
        Log.d(TAG, "Is from saved instance state: %b", fromSavedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int pos = savedInstanceState.getInt("scroll_vertical", - getToolbar().getHeight()) + getToolbar().getHeight();
        ScrollUtils.addOnGlobalLayoutListener(scrollable, new Runnable() {
            @Override
            public void run() {
                scrollable.scrollVerticallyTo(pos);
                onScrollChanged(pos, false, false);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(scrollable != null) {
            outState.putInt("scroll_vertical", scrollable.getCurrentScrollY() - getToolbar().getHeight());
        }
    }

    @Override
    public void finish() {
        snapToBottom();
    }

    @Override
    public void setContentView(int resId) {
        if(frameLayout == null)
            throw new IllegalStateException("onCreate() must be called before this!");

        frameLayout.removeAllViews();
        View view = getLayoutInflater().inflate(resId, frameLayout, true);
        view.setVisibility(View.INVISIBLE);

        super.setContentView(containerView);
        ScrollUtils.addOnGlobalLayoutListener(frameLayout, runEnterAnim);
    }

    @Override
    public void setContentView(View view) {
        if(frameLayout == null)
            throw new IllegalStateException("onCreate() must be called before this!");

        attachView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if(frameLayout == null)
            throw new IllegalStateException("onCreate() must be called before this!");

        view.setLayoutParams(params);
        attachView(view);
    }

    private void attachView(View contentView) {
        contentView.setVisibility(View.INVISIBLE);

        frameLayout.removeAllViews();
        frameLayout.addView(contentView);

        super.setContentView(containerView);
        ScrollUtils.addOnGlobalLayoutListener(frameLayout, runEnterAnim);
    }

    public void setScrollable(S scrollable) {
        scrollable.setTouchInterceptionViewGroup(frameLayout);
        scrollable.setScrollViewCallbacks(this);
        this.scrollable = scrollable;
    }

    public void setStatusBarTint(int color) {
        this.fromStatusBarColor = color;
    }

    public void setRequestedStatusBarTint(int color) {
        this.toStatusBarColor = color;
    }

    public S getScrollable() {
        return scrollable;
    }

    public void onStartEnterAnimation() {}
    public void onEndEnterAnimation() {}
    public void onStartExitAnimation() {}
    public void onEndExitAnimation() {}

    public int getCurrentScrollY() {
        return scrollable != null ?
                scrollable.getCurrentScrollY() : 0;
    }

    public int getScreenHeight() {
        return frameLayout != null ?
                frameLayout.getHeight() : 0;
    }

    public float getCurrentTranslation() {
        return frameLayout != null ?
                frameLayout.getTranslationY() : 0;
    }

    @Override
    public boolean shouldInterceptTouchEvent(MotionEvent motionEvent, boolean moving, float diffX, float diffY) {
        return frameLayout.getY() > 0
                || (moving && (getCurrentScrollY() - diffY) < 0);
    }

    @Override
    public void onDownMotionEvent(MotionEvent motionEvent) {
        scrollYOnDownMotion = getCurrentScrollY();
        initialY = getCurrentTranslation();

        if(velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        } else {
            velocityTracker.clear();
        }

        velocityTracker.addMovement(motionEvent);
    }

    @Override
    public void onMoveMotionEvent(MotionEvent motionEvent, float diffX, float diffY) {
        float translationY = getCurrentTranslation() - scrollYOnDownMotion + diffY;
        float fraction = getFraction(translationY);

        moveTo(translationY);
        setScrimTo(fraction);
        setStatusBarTo(fraction);

        movedDistanceY = getCurrentTranslation() - initialY;
        velocityTracker.addMovement(motionEvent);
        if(diffY < 0) {
            state = ScrollState.UP;
        } else if(diffY > 0) {
            state = ScrollState.DOWN;
        } else {
            state = ScrollState.STOP;
        }
    }

    @Override
    public void onUpOrCancelMotionEvent(MotionEvent motionEvent) {
        float velocity;
        final int mx = 25;
        if(motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
            velocityTracker.recycle();
            state = ScrollState.STOP;
            velocity = 0;
        } else {
            if(velocityTracker != null) {
                velocityTracker.addMovement(motionEvent);
                velocityTracker.computeCurrentVelocity(50, mx);
                velocity = Math.abs(velocityTracker.getYVelocity());
            } else {
                velocity = 0;
            }
        }

        if(state == ScrollState.UP) {
            snapToTop();
        } else {
            int maxBoundary = getScreenHeight() / 2;
            int boundary = (int) (maxBoundary * (1.2 - (velocity / mx)));
            Log.i("OverlayActivity", "Setting dismiss boundary: %dpx of max: %d", boundary, maxBoundary);

            if(movedDistanceY < boundary) {
                snapToTop();
            } else {
                snapToBottom();
            }
        }
    }

    @Override
    public void onScrollChanged(int scrollY, boolean var2, boolean var3) {}

    @Override
    public void onDownMotionEvent(){}

    @Override
    public void onUpOrCancelMotionEvent(ScrollState var1){}

    private void moveTo(float translationY) {
        float fixed = ScrollUtils.getFloat(translationY, 0, getScreenHeight());
        frameLayout.setTranslationY(fixed);
    }

    private void setScrimTo(float fraction) {
        backdropImageView.setProgress((int) (fraction * (-getScreenHeight())), fraction);
    }

    private void setStatusBarTo(float fraction) {
        int color = (Integer) ARGB_EVALUATOR.evaluate(fraction, fromStatusBarColor, toStatusBarColor);
        setStatusBarColor(color);
    }

    private void snapToTop() {
        animatorSet.cancel();
        animatorSet.removeAllListeners();

        ValueAnimator animator1 = ValueAnimator.ofFloat(getCurrentTranslation(), 0);
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                frameLayout.setTranslationY((Float) animation.getAnimatedValue());
            }
        });

        ValueAnimator animator2 = ValueAnimator.ofFloat(getFraction(), 1);
        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setScrimTo((Float) animation.getAnimatedValue());
            }
        });

        ValueAnimator animator3 = ValueAnimator.ofObject(ARGB_EVALUATOR, getStatusBarColor(), toStatusBarColor);
        animator3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setStatusBarColor((Integer) animation.getAnimatedValue());
            }
        });

        animatorSet.playTogether(animator1, animator2, animator3);
        animatorSet.setDuration(ANIMATION_DURATION);
        animatorSet.setInterpolator(ENTER_INTERPOLATOR);
        animatorSet.start();
    }

    private void setStatusBarColor(int color) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }

    private int getStatusBarColor() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getWindow().getStatusBarColor();
        } else {
            return Color.BLACK;
        }
    }

    private float getFraction() {
        return getFraction(getCurrentTranslation());
    }

    private float getFraction(float translationY) {
        return ScrollUtils.getFloat(1 - (translationY / getScreenHeight()), 0, 1);
    }

    private void snapToBottom() {
        animatorSet.cancel();
        animatorSet.removeAllListeners();

        ValueAnimator animator1 = ValueAnimator.ofFloat(getCurrentTranslation(), getScreenHeight());
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                frameLayout.setTranslationY((Float) animation.getAnimatedValue());
            }
        });

        ValueAnimator animator2 = ValueAnimator.ofFloat(getFraction(), 0);
        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setScrimTo((Float) animation.getAnimatedValue());
            }
        });

        ValueAnimator animator3 = ValueAnimator.ofObject(ARGB_EVALUATOR, getStatusBarColor(), fromStatusBarColor);
        animator3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setStatusBarColor((Integer) animation.getAnimatedValue());
            }
        });

        animatorSet.playTogether(animator1, animator2, animator3);
        animatorSet.setDuration(ANIMATION_DURATION);
        animatorSet.setInterpolator(EXIT_INTERPOLATOR);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                OverlayActivity.super.finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                onEndExitAnimation();

                OverlayActivity.super.finish();
                overridePendingTransition(0, abc_fade_out);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                onStartExitAnimation();
            }
        });

        animatorSet.start();
    }
}
