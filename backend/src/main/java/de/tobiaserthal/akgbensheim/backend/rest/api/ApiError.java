package de.tobiaserthal.akgbensheim.backend.rest.api;

import java.io.IOException;
import java.net.HttpURLConnection;

import de.tobiaserthal.akgbensheim.backend.rest.model.base.Response;


public class ApiError extends Exception {
    private final int code;
    private final String message;

    public static ApiError from(Response<?> response) {
        return new ApiError(response.getCode(), response.getMessage());
    }

    public static ApiError from(HttpURLConnection connection) throws IOException {
        return new ApiError(connection.getResponseCode(), connection.getResponseMessage());
    }

    public static void check(Response<?> response) throws ApiError {
        if(response == null || response.getCode() != 200)
            throw ApiError.from(response);
    }

    public static void check(HttpURLConnection connection) throws ApiError, IOException {
        if(connection == null || connection.getResponseCode() != 200)
            throw ApiError.from(connection);
    }

    private ApiError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
