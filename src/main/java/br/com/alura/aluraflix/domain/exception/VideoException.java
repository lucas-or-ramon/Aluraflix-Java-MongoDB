package br.com.alura.aluraflix.domain.exception;

public class VideoException extends RuntimeException{

    private final String msg;

    public VideoException(String message, Throwable cause) {
        super(message, cause);
        this.msg = message;
    }

    public VideoException(String message) {
        super(message);
        this.msg = message;
    }

    public String getMsg() {
        return msg;
    }
}
