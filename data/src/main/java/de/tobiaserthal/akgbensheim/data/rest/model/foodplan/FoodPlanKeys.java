package de.tobiaserthal.akgbensheim.data.rest.model.foodplan;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import java.io.File;

public class FoodPlanKeys {
    public static final String DOMAIN = "http://www.akg-bensheim.de/files/%02dWoche.pdf";
    public static String getDomain(int week) {return String.format(DOMAIN, week);}

    public static String getDefaultCacheDir(Context context) {
        return context.getExternalCacheDir()
                + File.separator + "documents";
    }

    public static File getDefaultCachePath(Context context) {
        return new File(getDefaultCacheDir(context), "food.pdf");
    }

    public static Uri getDefaultCacheUri(Context context) {
        return FileProvider.getUriForFile(context, "de.tobiaserthal.akgbensheim.file.provider", getDefaultCachePath(context));
    }
}
