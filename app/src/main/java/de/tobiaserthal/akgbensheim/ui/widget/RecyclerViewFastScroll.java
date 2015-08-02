package de.tobiaserthal.akgbensheim.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import de.tobiaserthal.akgbensheim.R;

public class RecyclerViewFastScroll extends FrameLayout {
    private static final int BUBBLE_ANIMATION_DURATION = 100;
    private static final int TRACK_SNAP_RANGE = 5;

    private int height;
    private View handle;
    private TextView bubble;
    private RecyclerView recyclerView;
    private boolean bubbleVisible = false;
    private boolean dragging = false;

    private final RecyclerView.OnScrollListener listener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if(dragging)
                return;

            int offset = recyclerView.computeVerticalScrollOffset();
            int extend = recyclerView.computeVerticalScrollExtent();
            int range = recyclerView.computeVerticalScrollRange();

            float fraction = ((float) offset) / (((float) range) - ((float) extend));
            offsetComponents(fraction * height);
        }
    };

    public RecyclerViewFastScroll(Context context) {
        super(context);
    }

    public RecyclerViewFastScroll(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public RecyclerViewFastScroll(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        setClipChildren(false);

        LayoutInflater.from(context)
                .inflate(R.layout.recyclerview_fastscroll, this, true);

        bubble = (TextView) findViewById(R.id.fastscroll_bubble);
        handle = findViewById(R.id.fastscroll_handle);

        hideBubble(false);
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        recyclerView.addOnScrollListener(listener);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        height = h;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(!isOnHandle(event))
                    return false;

                if(!bubbleVisible) {
                    showBubble();
                    bubbleVisible = true;
                }

                handle.setSelected(true);
                dragging = true;
                return true;

            case MotionEvent.ACTION_MOVE:
                if(!bubbleVisible) {
                    showBubble();
                    bubbleVisible = true;
                }

                handle.setSelected(true);

                offsetComponents(event.getY());
                offsetRecyclerView(event.getY());
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                if(bubbleVisible) {
                    hideBubble(true);
                    bubbleVisible = false;
                }

                handle.setSelected(false);
                dragging = false;
                return true;

            default:
                return super.onTouchEvent(event);
        }
    }

    private boolean isOnHandle(MotionEvent event) {
       return ViewHelper.getX(handle) < event.getX();
    }

    private void offsetComponents(float y) {
        int bubbleHeight = bubble.getHeight();
        int handleHeight = handle.getHeight();

        float handleY = ScrollUtils.getFloat(y - handleHeight / 2, 0, height - handleHeight);
        float bubbleY = ScrollUtils.getFloat(y - bubbleHeight, 0, height - bubbleHeight - handleHeight / 2);

        ViewHelper.setY(handle, handleY);
        ViewHelper.setY(bubble, bubbleY);
    }

    private void offsetRecyclerView(float y) {
        if (recyclerView != null) {
            int itemCount = recyclerView.getAdapter().getItemCount();

            float proportion;
            if (ViewHelper.getY(handle) == 0)
                proportion = 0f;
            else if (ViewHelper.getY(handle) + handle.getHeight() >= height - TRACK_SNAP_RANGE)
                proportion = 1f;
            else
                proportion = y / (float) height;

            int targetPos = (int) ScrollUtils.getFloat(proportion * itemCount, 0, itemCount - 1);
            recyclerView.getLayoutManager().scrollToPosition(targetPos);

            SectionIndexer indexer = (SectionIndexer) recyclerView.getAdapter();
            Object[] sections = indexer.getSections();
            int section = indexer.getSectionForPosition(targetPos);

            bubble.setText(sections[section].toString());
        }
    }

    public void showBubble() {
        ViewHelper.setScaleX(bubble, 0);
        ViewHelper.setScaleY(bubble, 0);

        ViewHelper.setPivotX(bubble, bubble.getWidth());
        ViewHelper.setPivotY(bubble, bubble.getHeight());

        ViewPropertyAnimator.animate(bubble).cancel();
        ViewPropertyAnimator.animate(bubble)
                .scaleX(1)
                .scaleY(1)
                .alpha(1)
                .setDuration(BUBBLE_ANIMATION_DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        bubble.setVisibility(View.VISIBLE);
                    }
                })
                .start();
    }

    public void hideBubble(boolean animated) {
        if(!animated) {
            bubble.setVisibility(GONE);
            ViewHelper.setScaleX(bubble, 0);
            ViewHelper.setScaleY(bubble, 0);
        }

        ViewHelper.setPivotX(bubble, bubble.getWidth());
        ViewHelper.setPivotY(bubble, bubble.getHeight());

        //new AnimatorSet.Builder().with(Animator.)

        ViewPropertyAnimator.animate(bubble).cancel();
        ViewPropertyAnimator.animate(bubble)
                .scaleX(0)
                .scaleY(0)
                .alpha(0)
                .setDuration(BUBBLE_ANIMATION_DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        bubble.setVisibility(View.GONE);
                    }
                })
                .start();
    }
}

