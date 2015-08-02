package de.tobiaserthal.akgbensheim.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.preferences.Keys;


public class ColorUtil {
    public static final String TAG = "ColorUtil";

    private static ColorUtil instance = null;

    private Context context;
    private int[] colors;

    private int index_subst;
    private int index_change;
    private int index_reserv;
    private int index_cancel;
    private int index_special;
    private int index_roomsSubst;
    private int index_shift;
    private int index_other;

    private ColorUtil(Context context) {
        this.context = context;
        this.colors = ViewUtils.getColorArray(context, R.array.subst_colors);

        refreshColorList();
    }

    public static ColorUtil getInstance(Context context) {
        if(instance == null)
            instance = new ColorUtil(context.getApplicationContext());

        return instance;
    }

    public int[] getColorArray() {
        return colors;
    }

    public void refreshColorList() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        index_subst = preferences.getInt(Keys.COLOR.SUBST_SUBST, 8);
        index_change = preferences.getInt(Keys.COLOR.SUBST_CHANGE, 5);
        index_reserv = preferences.getInt(Keys.COLOR.SUBST_RESERV, 4);
        index_cancel = preferences.getInt(Keys.COLOR.SUBST_CANCEL, 0);
        index_special = preferences.getInt(Keys.COLOR.SUBST_SPECIAL, 1);
        index_roomsSubst = preferences.getInt(Keys.COLOR.SUBST_ROOM_SUBST, 0);
        index_shift = preferences.getInt(Keys.COLOR.SUBST_SHIFT, 13);
        index_other = preferences.getInt(Keys.COLOR.SUBST_OTHER, 16);
    }

    public int getColorFromSubstType(String type) {
        if(type.equalsIgnoreCase("Vertretung"))
            return colors[index_subst];

        if(type.equalsIgnoreCase("Tausch"))
            return colors[index_change];

        if(type.equalsIgnoreCase("Vormerkung"))
            return colors[index_reserv];

        if(type.equalsIgnoreCase("Fällt aus!"))
            return colors[index_cancel];

        if(type.equalsIgnoreCase("Sondereins."))
            return colors[index_special];

        if(type.equalsIgnoreCase("Raum-Vtr."))
            return colors[index_roomsSubst];

        if(type.equalsIgnoreCase("Verlegung"))
            return colors[index_shift];

        return colors[index_other];
    }
}
