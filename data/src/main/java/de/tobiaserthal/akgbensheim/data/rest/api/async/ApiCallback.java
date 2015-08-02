package de.tobiaserthal.akgbensheim.data.rest.api.async;

import java.util.List;

import de.tobiaserthal.akgbensheim.data.rest.api.ApiError;
import de.tobiaserthal.akgbensheim.data.rest.model.base.BaseResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public abstract class ApiCallback<T> implements Callback<BaseResponse<T>> {
    @Override
    public final void success(BaseResponse<T> data, Response response) {
        switch (data.getCode()) {
            case 200:
                onSuccess(data.getData(), response);
                break;
            default:
                onFailure(ApiError.from(data));
                break;
        }
    }

    @Override
    public void failure(RetrofitError error) {
        onFailure(error);
    }

    public abstract void onSuccess(List<T> data, Response response);
    public abstract void onFailure(Exception e);
}
