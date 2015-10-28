package com.emergentorganization.cellrpg.tools.postprocessing;

/**
 * Created by BrianErikson on 9/3/15.
 */
public class ShaderException extends Exception {
    public ShaderException() {
        super();
    }

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
