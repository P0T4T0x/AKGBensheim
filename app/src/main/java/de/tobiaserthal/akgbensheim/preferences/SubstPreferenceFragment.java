package de.tobiaserthal.akgbensheim.preferences;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.adapter.SubstPreferenceAdapter;
import de.tobiaserthal.akgbensheim.data.preferences.PreferenceKey;
import de.tobiaserthal.akgbensheim.data.preferences.PreferenceProvider;
import de.tobiaserthal.akgbensheim.ui.RecyclerFragment;
import de.tobiaserthal.akgbensheim.ui.widget.DividerItemDecoration;

public class SubstPreferenceFragment extends RecyclerFragment<SubstPreferenceAdapter> {

    private int maxTranslationY;
    private FloatingActionButton actionButton;

    private static final int ANIM_DURATION = 150;
    private static final Interpolator IN_INTERPOLATOR = new LinearOutSlowInInterpolator();
    private static final Interpolator OUT_INTERPOLATOR = new FastOutLinearInInterpolator();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Create the adapter
        String[] subjects = PreferenceProvider.getInstance(getActivity()).getSubstSubjects();
        setAdapter(new SubstPreferenceAdapter(subjects));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        FrameLayout frameLayout = (FrameLayout) super.onCreateView(
                inflater, parent, savedInstanceState);

        if(frameLayout == null) {
            return null;
        }

        actionButton = (FloatingActionButton) inflater.inflate(R.layout.action_button, frameLayout, false);
        actionButton.setImageResource(R.drawable.ic_plus);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertItem();
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

        frameLayout.addView(actionButton, layoutParams);
        return frameLayout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setLayoutManager(new LinearLayoutManager(getActivity()));
        getRecyclerView().addItemDecoration(new DividerItemDecoration(getActivity(), null));

        final int swipe = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, swipe) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                getAdapter().removeItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(getRecyclerView());
    }

    @Override
    public void onDownMotionEvent() {
        maxTranslationY = getView() !=  null ?
                getView().getHeight() - actionButton.getTop() : 0;
    }

    @Override
    public void onScrolled(int dX, int dY) {
        float currentTranslationY = ViewHelper.getTranslationY(actionButton);

        float translationY = ScrollUtils.getFloat(currentTranslationY + dY, 0, maxTranslationY);
        ViewHelper.setTranslationY(actionButton, translationY);
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        switch (scrollState) {
            case UP:
                hideFab();
                break;
            case DOWN:
                showFab();
                break;
        }
    }

    private void showFab() {
        ViewPropertyAnimator.animate(actionButton).cancel();
        ViewPropertyAnimator.animate(actionButton)
                .translationY(0)
                .setInterpolator(IN_INTERPOLATOR)
                .setDuration(ANIM_DURATION)
                .start();
    }

    private void hideFab() {
        ViewPropertyAnimator.animate(actionButton).cancel();
        ViewPropertyAnimator.animate(actionButton)
                .translationY(maxTranslationY)
                .setInterpolator(OUT_INTERPOLATOR)
                .setDuration(ANIM_DURATION)
                .start();
    }

    @Override
    public void onPause() {
        super.onPause();
        save();
    }

    private void insertItem() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.insert_subject)
                .content(R.string.insert_subject_content)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(R.string.insert_subject_hint, 0, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                        getAdapter().addItem(charSequence.toString());
                    }
                }).show();

    }

    private void save() {
        PreferenceProvider.getInstance(getActivity())
                .setSubstSubjects(getAdapter().toString());
    }

}
