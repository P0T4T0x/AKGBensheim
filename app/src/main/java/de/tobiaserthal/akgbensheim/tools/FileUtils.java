package de.tobiaserthal.akgbensheim.tools;

import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.tobiaserthal.akgbensheim.data.Log;

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
}
