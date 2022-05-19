package br.com.alura.aluraflix.exception.video;

public class VideoAlreadyExistsException extends RuntimeException {

    public VideoAlreadyExistsException(String message) {
        super(message);
    }
}
