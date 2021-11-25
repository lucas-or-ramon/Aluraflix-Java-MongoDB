package br.com.alura.aluraflix.controllers;

import br.com.alura.aluraflix.controllers.request.SignupRequest;
import br.com.alura.aluraflix.controllers.response.MessageResponse;
import br.com.alura.aluraflix.controllers.response.UserResponse;
import br.com.alura.aluraflix.models.ERole;
import br.com.alura.aluraflix.models.Role;
import br.com.alura.aluraflix.models.User;
import br.com.alura.aluraflix.repository.RoleRepository;
import br.com.alura.aluraflix.repository.UserRepository;

import org.eclipse.jetty.websocket.api.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping(value = "/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> registerUserAdmin(@Valid @RequestBody SignupRequest signupRequest) {

        ResponseEntity<MessageResponse> response = isUsernameAndEmailValid(signupRequest);
        if (response.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            return response;
        }

        String password = passwordEncoder.encode(signupRequest.getPassword());
        User user = new User(signupRequest.getUsername(), signupRequest.getEmail(), password);

        Set<Role> roles = new HashSet<>();
        roles.add(getAdminRole());

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    private ResponseEntity<MessageResponse> isUsernameAndEmailValid(SignupRequest signupRequest) {
        if (Boolean.TRUE.equals(userRepository.existsByUsername(signupRequest.getUsername()))) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (Boolean.TRUE.equals(userRepository.existsByEmail(signupRequest.getEmail()))) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already taken!"));
        }

        return ResponseEntity.ok().body(new MessageResponse(""));
    }

    private Role getAdminRole() {
        return roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
    }

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUsers(@RequestParam int page) {

        Page<User> userPage = userRepository.findAll(PageRequest.of(page, User.PAGE_LIMIT));

        if (userPage.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Users not found"));
        }
        return ResponseEntity.ok(UserResponse.fromList(userPage.toList()));
    }

    @GetMapping(value = "/users/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            return ResponseEntity.ok(UserResponse.from(userOptional.get()));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
    }

    @DeleteMapping(value = "/users/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            userRepository.delete(userOptional.get());
            return ResponseEntity.ok(new MessageResponse("Deleted user"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
        }
    }
}
