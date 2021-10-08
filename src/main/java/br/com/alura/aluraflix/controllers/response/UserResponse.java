package br.com.alura.aluraflix.controllers.response;

import br.com.alura.aluraflix.models.ERole;
import br.com.alura.aluraflix.models.Role;
import br.com.alura.aluraflix.models.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class UserResponse {

    @JsonProperty("username")
    private String username;

    @JsonProperty("roles")
    private Set<ERole> roles;

    private UserResponse(User user) {
        this.username = user.getUsername();
        this.roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
    }

    public static UserResponse from(User user) {
        return new UserResponse(user);
    }

    public static List<UserResponse> fromList(List<User> users) {
        return users.stream().map(UserResponse::from).collect(Collectors.toList());
    }
}
