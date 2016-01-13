package com.emergentorganization.cellrpg.tools.postprocessing;


public class ShaderException extends Exception {
    public ShaderException(String message) {
        super(message);
    }

    public ShaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShaderException(Throwable cause) {
        super(cause);
    }

    protected ShaderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
