package de.tobiaserthal.akgbensheim.data.rest.model.base;

public interface Response<T> {
    Integer getCode();
    String getMessage();

    T getData();
}
