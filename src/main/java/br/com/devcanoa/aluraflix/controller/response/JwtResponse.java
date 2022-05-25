package br.com.devcanoa.aluraflix.controller.response;

import br.com.devcanoa.aluraflix.security.services.UserDetailsImpl;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class JwtResponse {

    private static final String TYPE = "Bearer";

    private final String token;
    private final String id;
    private final String username;
    private final String email;
    private final String role;

    public static JwtResponse from(String jwt, UserDetailsImpl userDetails, String role) {
        return new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), role);
    }
}
