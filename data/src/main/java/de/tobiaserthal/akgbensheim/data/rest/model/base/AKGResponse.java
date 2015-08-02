package de.tobiaserthal.akgbensheim.data.rest.model.base;

public class AKGResponse<T> implements Response<T> {

    private Integer code;
    private String message;
    private T data;

    public AKGResponse(Integer code, String message, T data) {
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
    public T getData() {
        return this.data;
    }
}
