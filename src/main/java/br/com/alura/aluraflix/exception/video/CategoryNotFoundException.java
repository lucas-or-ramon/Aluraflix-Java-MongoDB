package br.com.alura.aluraflix.exception.video;

public class CategoryNotFoundException extends VideoNotFoundException {

    public CategoryNotFoundException(String message) {
        super(message);
    }
}
