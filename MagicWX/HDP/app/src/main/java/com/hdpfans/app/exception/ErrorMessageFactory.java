package com.hdpfans.app.exception;

import com.google.gson.JsonSyntaxException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.HttpException;

public class ErrorMessageFactory {

    private ErrorMessageFactory() {
        // empty
    }

    public static String create(Exception exception) {
        exception.printStackTrace();
        String message = null;

        if (exception instanceof JsonSyntaxException) {

        } else if (exception instanceof HttpException) {

        } else if (exception instanceof UnknownHostException
                || exception instanceof ConnectException
                || exception instanceof SocketTimeoutException) {

        } else {

        }
        return message;
    }
}
