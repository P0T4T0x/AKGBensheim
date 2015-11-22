package de.tobiaserthal.akgbensheim.ui;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.support.v7.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;

import de.tobiaserthal.akgbensheim.R;

public class ColorChooser extends DialogFragment implements View.OnClickListener {
    private Callback callback;
    private int[] colors;

    public static ColorChooser newInstance(@ArrayRes int colorArray) {
        return newInstance(colorArray, 0);
    }

    public static ColorChooser newInstance(@ArrayRes int colorArray, int selected) {
        ColorChooser chooser = new ColorChooser();

        Bundle args = new Bundle();
        args.putInt("arrId", colorArray);
        args.putInt("selected", selected);
        chooser.setArguments(args);

        return chooser;
    }

    public static ColorChooser newInstance(int[] colorArray) {
        return newInstance(colorArray, 0);
    }

    public static ColorChooser newInstance(int[] colorArray, int selected) {
        ColorChooser chooser = new ColorChooser();

        Bundle args = new Bundle();
        args.putIntArray("arr", colorArray);
        args.putInt("selected", selected);
        chooser.setArguments(args);

        return chooser;
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() != null) {
            Integer index = (Integer) v.getTag();
            callback.onColorSelection(index, colors[index], shiftColor(colors[index]));
            dismiss();
        }
    }

    public interface Callback {
        void onColorSelection(int index, int color, int darker);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final int preselect = getArguments().getInt("selected");
        final int itemSize = getResources().getDimensionPixelSize(R.dimen.color_chooser_item_size);
        final int itemMargin = getResources().getDimensionPixelSize(R.dimen.color_chooser_item_margin);
        final int columnCount = 4;

        colors = getArguments().getIntArray("arr");
        if(colors == null) {
            int resId = getArguments().getInt("arrId");
            TypedArray typedArray = getResources().obtainTypedArray(resId);

            colors = new int[typedArray.length()];
            for (int i = 0; i < typedArray.length(); i++) {
                colors[i] = typedArray.getColor(i, Color.BLACK);
            }

            typedArray.recycle();
        }

        final LinearLayout rootLayout = new LinearLayout(getActivity());
        rootLayout.setLayoutParams(new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
        );

        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        final GridLayout gridLayout = new GridLayout(getActivity());
        gridLayout.setId(android.R.id.list);

        int paddingHorizontal = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        int paddingVertical = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
        ViewCompat.setPaddingRelative(
                gridLayout,
                paddingHorizontal,
                paddingVertical,
                paddingHorizontal,
                paddingVertical
        );
        gridLayout.setClipToPadding(false);

        gridLayout.setColumnCount(columnCount);
        gridLayout.setOrientation(GridLayout.HORIZONTAL);

        for (int i = 0; i < colors.length; i++) {
            FrameLayout child = new FrameLayout(getActivity());

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = itemSize;
            params.height = itemSize;
            params.setGravity(Gravity.CENTER);
            params.setMargins(itemMargin, itemMargin, itemMargin, itemMargin);

            child.setTag(i);
            child.setOnClickListener(this);

            ImageView mark = new ImageView(getActivity());
            mark.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER
            ));

            mark.setImageResource(R.drawable.ic_checkbox_marked_circle);
            mark.setVisibility(colors[i] == preselect ?
                    View.VISIBLE : View.GONE);

            child.addView(mark);
            applySelectors(child, colors[i]);

            gridLayout.addView(child, params);
        }

        rootLayout.addView(gridLayout, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        return new MaterialDialog.Builder(getActivity())
                .title(R.string.color_chooser)
                .autoDismiss(false)
                .cancelable(true)
                .negativeText(android.R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dismiss();
                    }
                })
                .customView(rootLayout, false)
                .build();
    }

    private void applySelectors(View cell, int color) {
        Drawable selector = createSelector(color);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int[][] states = {
                    {-android.R.attr.state_pressed},
                    {android.R.attr.state_pressed}
            };

            int[] colors = {
                    shiftColor(color),
                    color
            };

            ColorStateList rippleColors = new ColorStateList(states, colors);
            setBackgroundCompat(cell, new RippleDrawable(rippleColors, selector, null));
        } else {
            setBackgroundCompat(cell, selector);
        }
    }

    private void setBackgroundCompat(View view, Drawable d) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(d);
        } else {
            //noinspection deprecation
            view.setBackgroundDrawable(d);
        }
    }

    private int shiftColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.9f; // value component
        return Color.HSVToColor(hsv);
    }

    private Drawable createSelector(int color) {
        ShapeDrawable coloredCircle = new ShapeDrawable(new OvalShape());
        coloredCircle.getPaint().setColor(color);
        ShapeDrawable darkerCircle = new ShapeDrawable(new OvalShape());
        darkerCircle.getPaint().setColor(shiftColor(color));

        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{-android.R.attr.state_pressed}, coloredCircle);
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, darkerCircle);
        return stateListDrawable;
    }
}
