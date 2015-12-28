package de.tobiaserthal.akgbensheim.backend.rest.model.base;

public interface Response<T> {
    Integer getCode();
    String getMessage();

    T getData();
}
