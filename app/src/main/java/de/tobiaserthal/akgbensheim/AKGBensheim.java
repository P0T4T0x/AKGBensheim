package de.tobiaserthal.akgbensheim;

import android.app.Application;
import android.preference.PreferenceManager;

import com.epapyrus.plugpdf.core.PlugPDF;
import com.epapyrus.plugpdf.core.PlugPDFException;
import de.tobiaserthal.akgbensheim.data.sync.SyncUtils;

public class AKGBensheim extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        SyncUtils.createSyncAccount(this);
        try {
            PlugPDF.init(this, BuildConfig.PLUGPDF_API_KEY);
            if(BuildConfig.DEBUG) {
                PlugPDF.setUpdateCheckEnabled(false);
                PlugPDF.enableUncaughtExceptionHandler();
            } else {
                PlugPDF.setUpdateCheckEnabled(false);
            }

        } catch (PlugPDFException.InvalidLicense invalidLicense) {
            invalidLicense.printStackTrace();
        }
    }
}
