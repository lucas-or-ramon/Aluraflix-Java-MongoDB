package br.com.alura.aluraflix.controllers;

import br.com.alura.aluraflix.controllers.request.SignupRequest;
import br.com.alura.aluraflix.controllers.response.CategoryResponse;
import br.com.alura.aluraflix.controllers.response.MessageResponse;
import br.com.alura.aluraflix.controllers.response.UserResponse;
import br.com.alura.aluraflix.models.Category;
import br.com.alura.aluraflix.models.ERole;
import br.com.alura.aluraflix.models.Role;
import br.com.alura.aluraflix.models.User;
import br.com.alura.aluraflix.repository.RoleRepository;
import br.com.alura.aluraflix.repository.UserRepository;
import br.com.alura.aluraflix.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static br.com.alura.aluraflix.controllers.Properties.PAGE_LIMIT;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUserAdmin(@Valid @RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already taken!"));
        }

        User user = new User(signupRequest.getUsername(), signupRequest.getEmail(), encoder.encode(signupRequest.getPassword()));

        Set<String> strRoles = signupRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                if ("admin".equals(role)) {
                    Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(adminRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @GetMapping(
            value = "/users",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllUsers(@RequestParam int page) {

        Pageable pageable = PageRequest.of(page, PAGE_LIMIT);

        Page<User> userPage = userRepository.findAll(pageable);

        if (userPage.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Users Not Found"));
        }
        return ResponseEntity.ok(UserResponse.fromList(userPage.toList()));
    }

    @GetMapping(
            value = "/users/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getUserById(@PathVariable String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            return ResponseEntity.ok(UserResponse.from(userOptional.get()));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Not Found User"));
    }

    @DeleteMapping(value = "/users/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            userOptional.get().getRoles().forEach(role -> {
                if (role.getName() == ERole.ROLE_ADMIN)
                    throw new RuntimeException("Error: Users with ADMIN privileges cannot be deleted");
            });

            userRepository.delete(userOptional.get());
            return ResponseEntity.ok(new MessageResponse("Deleted User"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Not Found User"));
        }

    }
}
