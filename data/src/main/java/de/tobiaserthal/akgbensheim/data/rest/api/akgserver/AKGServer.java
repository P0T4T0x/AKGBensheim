package de.tobiaserthal.akgbensheim.data.rest.api.akgserver;

import android.content.Context;

import com.epapyrus.plugpdf.core.PDFDocument;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.tobiaserthal.akgbensheim.data.rest.api.ApiError;
import de.tobiaserthal.akgbensheim.data.rest.model.base.AKGResponse;
import de.tobiaserthal.akgbensheim.data.rest.model.foodplan.FoodPlanKeys;

public class AKGServer {
    public static PDFDocument getFoodPlan(Context context, int week) throws ApiError {
        AKGResponse<PDFDocument> response = getFoodPlanResponse(context, week);

        ApiError.check(response);
        return response.getData();
    }

    public static AKGResponse<PDFDocument> getFoodPlanResponse(Context context, int week) {
        HttpURLConnection connection = null;
        try {
            URL domain = new URL(FoodPlanKeys.getDomain(week));
            connection = (HttpURLConnection) domain.openConnection();

            connection.setDoInput(true);
            connection.setConnectTimeout(10000);
            connection.connect();

            if(connection.getResponseCode()
                    != HttpURLConnection.HTTP_OK)
                return null;

            if(!"application/pdf".equals(
                    connection.getContentType())) {
                return null;
            }

            byte[] result;
            if(connection.getContentLength() > 5242880) {
                result = readFromConnectionWithCache(connection, context, domain.getFile());
            } else {
                result = readFromConnection(connection);
            }

            return new AKGResponse<>(
                    connection.getResponseCode(),
                    connection.getResponseMessage(),
                    new PDFDocument(result, connection.getContentLength(), "")
            );

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
    }

    private static byte[] readFromConnection(HttpURLConnection connection) throws IOException {
        InputStream inputStream = connection.getInputStream();
        if(inputStream == null)
            return null;

        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bufferSize * 2);

        int read;
        while((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }

        inputStream.close();
        return outputStream.toByteArray();
    }

    private static byte[] readFromConnectionWithCache(HttpURLConnection connection, Context context, String filePath) throws IOException {
        InputStream inputStream = connection.getInputStream();
        if(inputStream == null)
            return null;

        File file = new File(context.getCacheDir(), filePath);
        if(file.exists()
                && file.getParentFile().exists()
                && file.getParentFile().isDirectory()) {

            if(file.length() == (long) connection.getContentLength()) {
                inputStream.close();
                return file.getAbsolutePath().getBytes();
            }

            if(!file.delete()) {
                inputStream.close();
                return null;
            }

        } else {
            if(!file.getParentFile().mkdirs()) {
                inputStream.close();
                return null;
            }
        }

        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        FileOutputStream outputStream = new FileOutputStream(file);

        int read;
        while((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }

        inputStream.close();
        return file.getAbsolutePath().getBytes();
    }

}
