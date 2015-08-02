package de.tobiaserthal.akgbensheim.data.provider;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.epapyrus.plugpdf.core.PDFDocument;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import de.tobiaserthal.akgbensheim.data.rest.api.ApiError;
import de.tobiaserthal.akgbensheim.data.rest.api.akgserver.AKGServer;

public class FoodPlanLoader extends AsyncTaskLoader<byte[]> {

    private File file;
    private byte[] data;

    public FoodPlanLoader(Context context, File file) {
        super(context);
        this.file = file;
    }

    @Override
    public byte[] loadInBackground() {
        InputStream inputStream;
        ByteArrayOutputStream outputStream;
        try {
            inputStream = new FileInputStream(file);
            outputStream = new ByteArrayOutputStream();

            int read;
            byte[] buffer = new byte[inputStream.available()];
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }

            inputStream.close();
            outputStream.flush();

            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deliverResult(byte[] data) {
        if (isReset()) {
            // The Loader has been reset; ignore the result and invalidate the data.
            releaseResources(data);
            return;
        }

        // Hold a reference to the old data so it doesn't get garbage collected.
        // We must protect it until the new data has been delivered.
        byte[] oldData = this.data;
        this.data = data;

        if (isStarted()) {
            // If the Loader is in a started state, deliver the results to the
            // client. The superclass method does this for us.
            super.deliverResult(data);
        }

        // Invalidate the old data as we don't need it any more.
        if (oldData != null && oldData != data) {
            releaseResources(oldData);
        }
    }

    @Override
    protected void onStartLoading() {
        if (data != null) {
            // Deliver any previously loaded data immediately.
            deliverResult(data);
        }

        if (takeContentChanged() || data == null) {
            // When the observer detects a change, it should call onContentChanged()
            // on the Loader, which will cause the next call to takeContentChanged()
            // to return true. If this is ever the case (or if the current data is
            // null), we force a new load.
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        // The Loader is in a stopped state, so we should attempt to cancel the
        // current load (if there is one).
        cancelLoad();

        // Note that we leave the observer as is. Loaders in a stopped state
        // should still monitor the data source for changes so that the Loader
        // will know to force a new load if it is ever started again.
    }

    @Override
    protected void onReset() {
        // Ensure the loader has been stopped.
        onStopLoading();

        // At this point we can release the resources associated with 'mData'.
        if (data != null) {
            releaseResources(data);
            data = null;
        }
    }

    @Override
    public void onCanceled(byte[] data) {
        // Attempt to cancel the current asynchronous load.
        super.onCanceled(data);

        // The load has been canceled, so we should release the resources
        // associated with 'data'.
        releaseResources(data);
    }

    private void releaseResources(byte[] data) {
        Arrays.fill(data, (byte) 0);
    }
}
