package tech.itpark.proggerhub.exception;

public class SecretWordNotMatchedException extends RuntimeException {
    public SecretWordNotMatchedException() {
        super();
    }

    public SecretWordNotMatchedException(String message) {
        super(message);
    }

    public SecretWordNotMatchedException(String message, Throwable cause) {
        super(message, cause);
    }

    public SecretWordNotMatchedException(Throwable cause) {
        super(cause);
    }

    protected SecretWordNotMatchedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
