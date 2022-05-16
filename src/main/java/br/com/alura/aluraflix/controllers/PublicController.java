package br.com.alura.aluraflix.controllers;

import br.com.alura.aluraflix.controllers.request.LoginRequest;
import br.com.alura.aluraflix.controllers.request.SignupRequest;
import br.com.alura.aluraflix.controllers.response.MessageResponse;
import br.com.alura.aluraflix.controllers.response.UserResponse;
import br.com.alura.aluraflix.controllers.response.VideoResponse;
import br.com.alura.aluraflix.models.Category;
import br.com.alura.aluraflix.models.ERole;
import br.com.alura.aluraflix.models.User;
import br.com.alura.aluraflix.models.Video;
import br.com.alura.aluraflix.repository.VideoRepository;
import br.com.alura.aluraflix.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/start")
public class PublicController {

    private final VideoRepository videoRepository;
    private final UserService userService;

    @Autowired
    public PublicController(VideoRepository videoRepository, UserService userService) {
        this.videoRepository = videoRepository;
        this.userService = userService;
    }

    @PostMapping(value = "/signin", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        return ResponseEntity.ok(userService.authenticateUser(loginRequest));
    }

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody SignupRequest signupRequest) {

        return ResponseEntity.ok(userService.saveUser(signupRequest, ERole.ROLE_USER));
    }

    @GetMapping(value = "/free", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getFreeVideos(@RequestParam int page) {

        Pageable pageable = PageRequest.of(page, User.PAGE_LIMIT);
        Page<Video> videoPage = videoRepository.findFreeVideos(pageable, Category.FREE_CATEGORY);

        return videoPage.isEmpty() ? ResponseEntity.badRequest().body(new MessageResponse("Videos not found"))
                : ResponseEntity.ok(VideoResponse.fromList(videoPage.toList()));
    }
}
