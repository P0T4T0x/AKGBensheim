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
import de.tobiaserthal.akgbensheim.ui.ColorChooser;

public class ColorPreferenceFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_color_preferences, parent, false);

        setupRow(root, R.id.rowSubst, R.string.pref_title_subst_color_subst);
        setupRow(root, R.id.rowChange, R.string.pref_title_subst_color_change);
        setupRow(root, R.id.rowReserv, R.string.pref_title_subst_color_reserv);
        setupRow(root, R.id.rowCancel, R.string.pref_title_subst_color_cancel);
        setupRow(root, R.id.rowSpecial, R.string.pref_title_subst_color_special);
        setupRow(root, R.id.rowRoomSubst, R.string.pref_title_subst_color_roomSubst);
        setupRow(root, R.id.rowShift, R.string.pref_title_subst_color_shift);
        setupRow(root, R.id.rowOther, R.string.pref_title_subst_color_other);

        return root;
    }

    private void setupRow(View parent, int rowId, int titleRes) {
        View row = parent.findViewById(rowId);
        row.setClickable(true);
        row.setOnClickListener(this);
        setRowText(row, titleRes);

        int color;
        switch (rowId) {
            case R.id.rowSubst:
                color = PreferenceProvider.getInstance().getColorSubst();
                break;
            case R.id.rowChange:
                color = PreferenceProvider.getInstance().getColorChange();
                break;
            case R.id.rowReserv:
                color = PreferenceProvider.getInstance().getColorReserv();
                break;
            case R.id.rowCancel:
                color = PreferenceProvider.getInstance().getColorCancel();
                break;
            case R.id.rowSpecial:
                color = PreferenceProvider.getInstance().getColorSpecial();
                break;
            case R.id.rowRoomSubst:
                color = PreferenceProvider.getInstance().getColorRoomSubst();
                break;
            case R.id.rowShift:
                color = PreferenceProvider.getInstance().getColorShift();
                break;
            default:
                color = PreferenceProvider.getInstance().getColorOther();
                break;


        }

        setRowIcon(row, color);
    }

    public void setRowText(View row, int textId) {
        ((TextView) row.findViewById(android.R.id.text1)).setText(textId);
    }

    public void setRowIcon(View row, int color) {
        GradientDrawable background = (GradientDrawable) row.findViewById(android.R.id.icon).getBackground();
        background.setColor(color);

        row.setTag(color);
    }

    @Override
    public void onClick(final View v) {
        int color = (Integer) v.getTag();
        ColorChooser chooser = ColorChooser.newInstance(R.array.subst_colors, color);
        chooser.setCallback(new ColorChooser.Callback() {
            @Override
            public void onColorSelection(int index, int color, int darker) {
                setRowIcon(v, color);
                switch (v.getId()) {
                    case R.id.rowSubst:
                        PreferenceProvider.getInstance().setColorSubst(color);
                        break;
                    case R.id.rowChange:
                        PreferenceProvider.getInstance().setColorChange(color);
                        break;
                    case R.id.rowReserv:
                        PreferenceProvider.getInstance().setColorReserv(color);
                        break;
                    case R.id.rowCancel:
                        PreferenceProvider.getInstance().setColorCancel(color);
                        break;
                    case R.id.rowSpecial:
                        PreferenceProvider.getInstance().setColorSpecial(color);
                        break;
                    case R.id.rowRoomSubst:
                        PreferenceProvider.getInstance().setColorRoomSubst(color);
                        break;
                    case R.id.rowShift:
                        PreferenceProvider.getInstance().setColorShift(color);
                        break;
                    default:
                        PreferenceProvider.getInstance().setColorOther(color);
                        break;
                }
            }
        });

        chooser.show(getChildFragmentManager(), "COLOR_CHOOSER");
    }

}
