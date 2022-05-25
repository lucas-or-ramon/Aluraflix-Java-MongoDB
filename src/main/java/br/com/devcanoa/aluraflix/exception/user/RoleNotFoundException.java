package br.com.devcanoa.aluraflix.exception.user;

public class RoleNotFoundException extends UserNotFoundException {

    public RoleNotFoundException(String message) {
        super(message);
    }
}
