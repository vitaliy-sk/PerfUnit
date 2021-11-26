package dev.techh.exception;

public abstract class PerfUnitException extends RuntimeException {

    public PerfUnitException() {
    }

    public PerfUnitException(String message) {
        super(message);
    }

    public PerfUnitException(String message, Throwable cause) {
        super(message, cause);
    }

    public PerfUnitException(Throwable cause) {
        super(cause);
    }

    public PerfUnitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
