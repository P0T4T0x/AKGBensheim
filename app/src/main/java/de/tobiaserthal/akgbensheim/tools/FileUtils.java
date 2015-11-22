package de.tobiaserthal.akgbensheim.tools;

import android.content.Context;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.tobiaserthal.akgbensheim.data.Log;
import de.tobiaserthal.akgbensheim.data.provider.event.EventSelection;
import de.tobiaserthal.akgbensheim.data.provider.homework.HomeworkSelection;
import de.tobiaserthal.akgbensheim.data.provider.news.NewsSelection;
import de.tobiaserthal.akgbensheim.data.provider.substitution.SubstitutionSelection;
import de.tobiaserthal.akgbensheim.data.provider.teacher.TeacherSelection;

/**
 * A simple file utility class with static methods
 */
public class FileUtils {
    public static String readStreamToString(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        StringBuilder text = new StringBuilder();

        String line;
        while ((line = bufferedReader.readLine()) != null)
            text.append(line).append('\n');

        bufferedReader.close();
        inputStreamReader.close();

        return text.toString();
    }

    @SuppressWarnings("unused")
    public static String readRawTextFile(Context context, int resId) {
        String returnString;
        try {
            returnString = readStreamToString(context.getResources().openRawResource(resId));
        } catch (IOException e) {
            returnString = "";
            Log.e("FileUtils", e, "IOException occurred while trying to read resource with id: %d!", resId);
        }
        return returnString;
    }

    public static String readAssetsTextFile(Context context, String path) {
        String returnString;
        try {
            returnString = readStreamToString(context.getAssets().open(path));
        } catch (IOException e) {
            returnString = "";
            Log.e("FileUtils", e, "IOException occurred while trying to read asset with path: %s!", path);
        }
        return returnString;
    }

    public static String removeProtocol(String url) {
        if(url == null) {
            return "";
        }

        return url.replaceFirst("^(?:http(?:s)?://)?(?:www(?:[0-9]+)?\\.)", "");
    }

    public static boolean clearDirectory(File dir) {
        if(dir == null) {
            return false;
        }

        if(dir.isDirectory()) {
            String[] list = dir.list();
            for(int i = 0; i < list.length; i++) {
                boolean success = clearDirectory(new File(dir, list[i]));
                if(success) {
                    Log.d("FileUtils", "Successfully deleted file: %s", list[i]);
                } else {
                    return false;
                }
            }
        }

        return dir.delete();
    }

    public static void clearData(Context context) {
        // clear data directory
        clearDirectory(context.getExternalCacheDir());

        // clear database
        new EventSelection().delete(context.getContentResolver());
        new HomeworkSelection().delete(context.getContentResolver());
        new NewsSelection().delete(context.getContentResolver());
        new SubstitutionSelection().delete(context.getContentResolver());
        new TeacherSelection().delete(context.getContentResolver());
    }

    public static void clearCache(Context context) {
        clearDirectory(context.getCacheDir());
    }
}
