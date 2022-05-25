package br.com.devcanoa.aluraflix.controller;

import br.com.devcanoa.aluraflix.controller.request.SignupRequest;
import br.com.devcanoa.aluraflix.controller.response.UserResponse;
import br.com.devcanoa.aluraflix.models.ERole;
import br.com.devcanoa.aluraflix.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping(value = "/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> registerUserAdmin(@Valid @RequestBody SignupRequest signupRequest) {

        return ResponseEntity.ok(userService.saveUser(signupRequest, ERole.ROLE_ADMIN));
    }

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserResponse>> getUsers(@RequestParam int page) {

        return ResponseEntity.ok(userService.getUsers(page));
    }

    @GetMapping(value = "/users/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {

        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @DeleteMapping(value = "/users/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        return new ResponseEntity<>(userService.deleteUser(username), HttpStatus.NO_CONTENT);
    }
}
