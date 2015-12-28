package de.tobiaserthal.akgbensheim.backend.sync;

import android.app.IntentService;
import android.content.Intent;

import com.epapyrus.plugpdf.core.PDFDocument;

import java.io.File;

import de.tobiaserthal.akgbensheim.backend.rest.api.akgserver.AKGServer;
import de.tobiaserthal.akgbensheim.backend.rest.model.foodplan.FoodPlanKeys;


public class FoodPlanService extends IntentService {

    public static final String ACTION_REPORT = "de.tobiaserthal.akgbensheim.service.foodplan.ACTION_REPORT";
    public static final int CODE_SUCCESS = 0x0;
    public static final int CODE_FAILURE = 0x1;

    public FoodPlanService() {
        this(FoodPlanService.class.getName());
    }

    public FoodPlanService(String name) {
        super(name);
    }

    public void onCreate(){
        super.onCreate();
        File cacheDir = new File(FoodPlanKeys.getDefaultCacheDir(this));
        if(!cacheDir.exists()){
            cacheDir.mkdirs();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int first = intent.getIntExtra("first", -1);
        int second = intent.getIntExtra("second", -1);
        String path = FoodPlanKeys.getDefaultCachePath(this).getAbsolutePath();

        if(first < 0 || second < 0) {
            notifyResults(CODE_FAILURE);
            stopSelf();
            return;
        }

        PDFDocument doc1 = null;
        PDFDocument doc2 = null;
        PDFDocument doc3 = null;
        try {
            doc1 = AKGServer.getFoodPlan(this, first);
            doc2 = AKGServer.getFoodPlan(this, second);

            doc3 = new PDFDocument();
            doc3.transplantPage(doc1, 0);
            doc3.transplantPage(doc2, 0);

            doc3.saveAsFile(path);
            notifyResults(CODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            notifyResults(CODE_FAILURE);
        } finally {
            if(doc1 != null)
                doc1.release();

            if(doc2 != null)
                doc2.release();

            if(doc3 != null)
                doc3.release();
        }
    }

    private void notifyResults(int code) {
        Intent intent = new Intent(ACTION_REPORT);
        intent.putExtra("code", code);
        sendBroadcast(intent);
    }
}
