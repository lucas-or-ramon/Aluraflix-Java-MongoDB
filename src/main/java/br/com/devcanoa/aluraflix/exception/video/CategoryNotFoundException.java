package br.com.devcanoa.aluraflix.exception.video;

public class CategoryNotFoundException extends VideoNotFoundException {

    public CategoryNotFoundException(String message) {
        super(message);
    }
}
