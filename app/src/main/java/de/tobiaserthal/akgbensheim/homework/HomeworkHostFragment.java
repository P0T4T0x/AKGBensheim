package de.tobiaserthal.akgbensheim.homework;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.backend.model.ModelUtils;
import de.tobiaserthal.akgbensheim.base.tabs.TabbedHostFragment;

public class HomeworkHostFragment extends TabbedHostFragment {

    private FloatingActionButton actionButton;

    private static final int ANIM_DURATION = 150;
    private static final Interpolator IN_INTERPOLATOR = new LinearOutSlowInInterpolator();
    private static final Interpolator OUT_INTERPOLATOR = new FastOutLinearInInterpolator();

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container,
                                    Bundle savedInstanceState) {

        FrameLayout root = (FrameLayout) super.onCreateContentView(
                inflater, container, savedInstanceState);

        actionButton = (FloatingActionButton) inflater.inflate(R.layout.action_button, root, false);
        actionButton.setImageResource(R.drawable.ic_plus);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModelUtils.newDummyHomework(getActivity());
            }
        });

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM | Gravity.END
        );

        int marginHorizontal = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        int marginVertical = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
        
        layoutParams.bottomMargin = marginVertical;
        layoutParams.topMargin = marginVertical;

        MarginLayoutParamsCompat.setMarginStart(layoutParams, marginHorizontal);
        MarginLayoutParamsCompat.setMarginEnd(layoutParams, marginHorizontal);

        root.addView(actionButton, layoutParams);
        return root;
    }

    @Override
    public void onPageSelected(int position) {
        if(needsButton(position)) {
            showFab(true);
        } else {
            hideFab(true);
        }
    }

    private boolean needsButton(int position) {
        return ((HomeworkFragment) getFragmentAt(position)).needsButton();
    }

    private void showFab(boolean animated) {
        actionButton.animate().cancel();

        if(!animated) {
            actionButton.setTranslationY(0);
            actionButton.setVisibility(View.VISIBLE);
            return;
        }

        actionButton.animate()
                .translationY(0)
                .setInterpolator(IN_INTERPOLATOR)
                .setDuration(ANIM_DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        actionButton.setVisibility(View.VISIBLE);
                    }
                })
                .start();
    }

    private void hideFab(boolean animated) {
        actionButton.animate().cancel();

        if(!animated) {
            actionButton.setTranslationY(getContentView().getHeight() - actionButton.getTop());
            actionButton.setVisibility(View.GONE);
            return;
        }

        actionButton.animate()
                .translationY(getContentView().getHeight() - actionButton.getTop())
                .setInterpolator(OUT_INTERPOLATOR)
                .setDuration(ANIM_DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        actionButton.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    public static class Builder extends TabbedHostFragment.Builder {
        private Builder() {
            super(HomeworkFragment.class);
        }

        public static Builder withDefault() {
            return new Builder();
        }

        @Override
        public HomeworkHostFragment build() {
            return build(HomeworkHostFragment.class);
        }
    }
}
