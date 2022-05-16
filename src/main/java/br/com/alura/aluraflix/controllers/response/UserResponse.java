package br.com.alura.aluraflix.controllers.response;

import br.com.alura.aluraflix.models.Role;
import br.com.alura.aluraflix.models.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserResponse {

    @JsonProperty("role")
    private String role;

    @JsonProperty("message")
    private String message;

    @JsonProperty("username")
    private String username;

    private UserResponse(User user) {
        this.username = user.getUsername();
        user.getRoles().stream().map(Role::getName).findFirst().ifPresent(
                rol -> {
                    this.role = rol.name();
                });
    }

    public UserResponse(String role, String message, String username) {
        this.role = role;
        this.message = message;
        this.username = username;
    }

    public static UserResponse from(User user) {
        return new UserResponse(user);
    }

    public static List<UserResponse> fromList(List<User> users) {
        return users.stream().map(UserResponse::from).collect(Collectors.toList());
    }

    public static UserResponse from(String role, String message, String username) {
        return new UserResponse(role, message, username);
    }
}
