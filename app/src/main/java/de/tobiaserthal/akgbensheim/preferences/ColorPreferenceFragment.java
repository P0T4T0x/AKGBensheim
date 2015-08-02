package de.tobiaserthal.akgbensheim.preferences;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.data.preferences.PreferenceProvider;
import de.tobiaserthal.akgbensheim.tools.ColorUtil;
import de.tobiaserthal.akgbensheim.ui.ColorChooser;

public class ColorPreferenceFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_color_preferences, parent, false);

        setupRow(root, R.id.rowSubst, R.string.pref_title_subst_color_subst, Keys.COLOR.SUBST_SUBST);
        setupRow(root, R.id.rowChange, R.string.pref_title_subst_color_change, Keys.COLOR.SUBST_CHANGE);
        setupRow(root, R.id.rowReserv, R.string.pref_title_subst_color_reserv, Keys.COLOR.SUBST_RESERV);
        setupRow(root, R.id.rowCancel, R.string.pref_title_subst_color_cancel, Keys.COLOR.SUBST_CANCEL);
        setupRow(root, R.id.rowSpecial, R.string.pref_title_subst_color_special, Keys.COLOR.SUBST_SPECIAL);
        setupRow(root, R.id.rowRoomSubst, R.string.pref_title_subst_color_roomSubst, Keys.COLOR.SUBST_ROOM_SUBST);
        setupRow(root, R.id.rowShift, R.string.pref_title_subst_color_shift, Keys.COLOR.SUBST_SHIFT);
        setupRow(root, R.id.rowOther, R.string.pref_title_subst_color_other, Keys.COLOR.SUBST_OTHER);

        return root;
    }

    private void setupRow(View parent, int rowId, int titleRes, String prefKey) {
        View row = parent.findViewById(rowId);
        row.setTag(prefKey);
        row.setClickable(true);
        row.setOnClickListener(this);

        setRowText(row, titleRes);
        setRowIcon(row, prefKey);
    }

    public void setRowText(View row, int textId) {
        ((TextView) row.findViewById(android.R.id.text1)).setText(textId);
    }

    public void setRowIcon(View row, String key) {
        int[] colors = ColorUtil.getInstance(getActivity()).getColorArray();
        int index = PreferenceProvider.getInstance(getActivity()).getInteger(key);
        setRowIcon(row, colors[index]);
    }

    public void setRowIcon(View row, int color) {
        GradientDrawable background = (GradientDrawable) row.findViewById(android.R.id.icon).getBackground();
        background.setColor(color);
    }

    @Override
    public void onClick(final View v) {
        final String key = (String) v.getTag();

        int selected = PreferenceProvider.getInstance(getActivity()).getInteger(key);
        ColorChooser chooser = ColorChooser.newInstance(R.array.subst_colors, selected);
        chooser.setCallback(new ColorChooser.Callback() {
            @Override
            public void onColorSelection(int index, int color, int darker) {
                setRowIcon(v, color);
                PreferenceProvider.getInstance(getActivity()).putInteger(key, index);
            }
        });

        chooser.show(getChildFragmentManager(), "COLOR_CHOOSER");
    }

}
