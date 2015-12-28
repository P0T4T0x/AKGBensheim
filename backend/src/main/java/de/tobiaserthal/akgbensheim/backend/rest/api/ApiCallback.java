package de.tobiaserthal.akgbensheim.backend.rest.api;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import de.tobiaserthal.akgbensheim.backend.model.base.BaseModel;
import de.tobiaserthal.akgbensheim.backend.rest.model.base.BaseResponse;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public abstract class ApiCallback<T extends BaseModel> implements Callback<BaseResponse<T>> {
    @SuppressWarnings("unchecked")
    public final void onResponse(Response<BaseResponse<T>> response, Retrofit retrofit) {
        if(response.isSuccess()) {
            BaseResponse data = response.body();
            if(data.getCode() == HttpURLConnection.HTTP_OK) {
                onSuccess((List<T>) data.getData(), response);
            } else {
                onFailure(ApiError.from(data));
            }

        } else {
            onFailure(new IOException("Response failure on http backend layer!"));
        }
    }

    public abstract void onSuccess(List<T> data, Response<BaseResponse<T>> response);
    public abstract void onFailure(Exception e);
}
