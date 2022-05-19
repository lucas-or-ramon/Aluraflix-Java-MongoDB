package br.com.alura.aluraflix.services;

import br.com.alura.aluraflix.controllers.request.LoginRequest;
import br.com.alura.aluraflix.controllers.request.SignupRequest;
import br.com.alura.aluraflix.controllers.response.JwtResponse;
import br.com.alura.aluraflix.controllers.response.UserResponse;
import br.com.alura.aluraflix.exception.user.UserAlreadyExistsException;
import br.com.alura.aluraflix.exception.user.UserNotFoundException;
import br.com.alura.aluraflix.models.ERole;
import br.com.alura.aluraflix.models.Role;
import br.com.alura.aluraflix.models.User;
import br.com.alura.aluraflix.repository.UserRepository;
import br.com.alura.aluraflix.security.jwt.JwtUtils;
import br.com.alura.aluraflix.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final JwtUtils jwtUtils;
    private final RoleService roleService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserService(JwtUtils jwtUtils, RoleService roleService, UserRepository userRepository,
                       PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.jwtUtils = jwtUtils;
        this.roleService = roleService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public JwtResponse authenticateUser(@Valid LoginRequest loginRequest) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        var jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        var roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles.get(0));
    }

    public UserResponse saveUser(@Valid SignupRequest signupRequest, ERole eRole) {
        validateEmailAndUsername(signupRequest);

        String password = passwordEncoder.encode(signupRequest.getPassword());
        var user = new User(signupRequest.getUsername(), signupRequest.getEmail(), password);

        Set<Role> roles = new HashSet<>();
        roles.add(roleService.getRole(eRole));

        user.setRoles(roles);
        userRepository.save(user);

        return UserResponse.from(user.getRoles().toString(), "Successfully!", user.getUsername());
    }

    private void validateEmailAndUsername(SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new UserAlreadyExistsException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new UserAlreadyExistsException("Error: Email is already taken!");
        }
    }

    public List<UserResponse> getUsers(int page) {
        Page<User> userPage = userRepository.findAll(PageRequest.of(page, User.PAGE_LIMIT));

        if (userPage.isEmpty()) {
            throw new UserNotFoundException("Users not found");
        }
        return UserResponse.fromList(userPage.toList());
    }

    public UserResponse getUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            return UserResponse.from(userOptional.get());
        }
        throw new UserNotFoundException("User not found");
    }

    public String deleteUser(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            userRepository.delete(userOptional.get());
            return "Deleted user";
        }
        throw new UserNotFoundException("User not found");
    }
}
