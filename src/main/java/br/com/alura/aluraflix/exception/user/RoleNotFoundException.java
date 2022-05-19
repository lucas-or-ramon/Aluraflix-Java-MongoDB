package br.com.alura.aluraflix.exception.user;

import br.com.alura.aluraflix.exception.user.UserNotFoundException;

public class RoleNotFoundException extends UserNotFoundException {

    public RoleNotFoundException(String message) {
        super(message);
    }
}
