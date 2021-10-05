package br.com.alura.aluraflix.controllers.response;

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
    private Set<Role> roles;

    private UserResponse(User user) {
        this.username = user.getUsername();
        this.roles = user.getRoles();
    }

    public static UserResponse from(User user) {
        return new UserResponse(user);
    }

    public static List<UserResponse> fromList(List<User> users) {
        return users.stream().map(UserResponse::from).collect(Collectors.toList());
    }
}
