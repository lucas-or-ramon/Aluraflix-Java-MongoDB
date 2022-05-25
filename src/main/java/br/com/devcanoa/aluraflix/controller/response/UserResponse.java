package br.com.devcanoa.aluraflix.controller.response;

import br.com.devcanoa.aluraflix.models.ERole;
import br.com.devcanoa.aluraflix.models.Role;
import br.com.devcanoa.aluraflix.models.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@JsonInclude(Include.NON_NULL)
public class UserResponse {

    private final String role;
    private final String username;

    public static UserResponse from(User user) {
        var role =  user.getRoles().stream().map(Role::getName).map(ERole::name).findFirst().orElse(null);
        return new UserResponse(role, user.getUsername());
    }

    public static List<UserResponse> fromList(List<User> users) {
        return users.stream().map(UserResponse::from).collect(Collectors.toList());
    }
}
