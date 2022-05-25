package br.com.devcanoa.aluraflix.exception.video;

public class VideoNotFoundException extends RuntimeException {

    public VideoNotFoundException(String message) {
        super(message);
    }
}
