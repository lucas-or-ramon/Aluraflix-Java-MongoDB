package br.com.alura.aluraflix.controllers.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class JwtResponse {

    @JsonProperty("token")
    private String token;

    @JsonProperty("type")
    private String type = "Bearer";

    @JsonProperty("id")
    private String id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("role")
    private String role;

    public JwtResponse(String token, String id, String username, String email, String role) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }
}
