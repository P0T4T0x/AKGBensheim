package de.tobiaserthal.akgbensheim.backend.rest.model.base;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import de.tobiaserthal.akgbensheim.backend.model.base.BaseModel;

public class BaseResponse<T extends BaseModel> implements Response<List<T>>{

    @Expose @SerializedName(BaseKeys.KEY_CODE)      private Integer code;
    @Expose @SerializedName(BaseKeys.KEY_MESSAGE)   private String message;
    @Expose @SerializedName(BaseKeys.KEY_DATA)      private List<T> data;

    public BaseResponse(Integer code, String message, List<T> data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public List<T> getData() {
        return this.data;
    }
}
