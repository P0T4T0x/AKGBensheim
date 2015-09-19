package de.tobiaserthal.akgbensheim.contact;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.IntentCompat;

import de.tobiaserthal.akgbensheim.R;

/**
 * Created by tobiaserthal on 18.09.15.
 */
public class ContactUtils {
    public static void startEmailIntent(Activity activity) {
        final Intent intent = ShareCompat.IntentBuilder.from(activity)
                .setType("message/rfc822")
                .setEmailTo(new String[]{activity.getString(R.string.email)})
                .setChooserTitle(R.string.action_share_chooser_email)
                .createChooserIntent();

        activity.startActivity(intent);
    }

    public static void startMapViewIntent(Activity activity) {
        final String query = String.format("geo:%f,%f?q=%s(%s)", 49.689533, 8.618027, "", "AKG Bensheim");
        final Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(query));

        activity.startActivity(intent);
    }

    public static void startDialIntent(Activity activity) {
        final String phone = String.format("tel:%s", "+496251");
        final Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(phone));
    }
}
