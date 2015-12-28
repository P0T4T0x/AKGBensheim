package de.tobiaserthal.akgbensheim.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.util.TypedValue;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;


import de.tobiaserthal.akgbensheim.R;

/**
 * A simple class containing some static methods to help you with context operations
 * (especially with typed attribute values)
 */
public class ContextHelper {
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

    public static void startEmailIntent(Activity activity) {
        final Intent intent = ShareCompat.IntentBuilder.from(activity)
                .setType("message/rfc822")
                .setEmailTo(new String[]{activity.getString(R.string.detail_body_contactEmail)})
                .setChooserTitle(R.string.share_action_chooser_email)
                .createChooserIntent();

        activity.startActivity(intent);
    }

    public static void startMapViewIntent(Activity activity) {
        Uri geoUri = Uri.parse(String.format(
                "geo:%f,%f?q=%s",
                49.689533,
                8.618027,
                activity.getString(R.string.app_name)
        ));

        Intent intent = new Intent(Intent.ACTION_VIEW, geoUri);
        intent.setPackage("com.google.android.apps.maps");
        if(intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);

        }
    }

    public static void startDialIntent(Activity activity) {
        String phone = String.format("tel:%s", activity.getString(R.string.detail_body_contactPhone));
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(phone));

        activity.startActivity(intent);
    }

    public static void startBrowserIntent(Activity activity, String url) {
        CustomTabsIntent intent = new CustomTabsIntent.Builder()
                .setToolbarColor(ContextCompat.getColor(activity, R.color.primary))
                .setStartAnimations(activity, R.anim.slide_up, R.anim.fade_out)
                .setExitAnimations(activity, R.anim.fade_in, R.anim.slide_down)
                .enableUrlBarHiding()
                .setShowTitle(true)
                .build();

        intent.launchUrl(activity, Uri.parse(url));
    }
}
