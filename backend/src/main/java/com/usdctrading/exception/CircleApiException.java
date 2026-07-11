package com.usdctrading.exception;

public class CircleApiException extends RuntimeException {
    private String errorCode;
    private String circleErrorMessage;

    public CircleApiException(String message) {
        super(message);
    }

    public CircleApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public CircleApiException(String message, String errorCode, String circleErrorMessage) {
        super(message);
        this.errorCode = errorCode;
        this.circleErrorMessage = circleErrorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getCircleErrorMessage() {
        return circleErrorMessage;
    }
}
