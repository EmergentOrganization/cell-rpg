package io.github.emergentorganization.cellrpg.tools.postprocessing;


class ShaderException extends Exception {
    public ShaderException(String message) {
        super(message);
    }

    public ShaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShaderException(Throwable cause) {
        super(cause);
    }
}
