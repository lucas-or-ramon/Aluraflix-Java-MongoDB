package br.com.alura.aluraflix.controllers;

import br.com.alura.aluraflix.controllers.request.VideoRequest;
import br.com.alura.aluraflix.controllers.response.MessageResponse;
import br.com.alura.aluraflix.controllers.response.VideoResponse;
import br.com.alura.aluraflix.models.Category;
import br.com.alura.aluraflix.models.Video;
import br.com.alura.aluraflix.repository.CategoryRepository;
import br.com.alura.aluraflix.repository.VideoRepository;
import br.com.alura.aluraflix.security.jwt.JwtUtils;
import br.com.alura.aluraflix.services.NextSequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/videos")
@PreAuthorize("hasRole('USER') or hasRole('MODERATOR')")
public class VideoController {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    NextSequenceService nextSequenceService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getVideos(@RequestHeader("Authorization") String headerAuth,
                                       @RequestParam(required = false) String search,
                                       @RequestParam int page) {

        String username = jwtUtils.getUserNameFromJwtToken(headerAuth.substring(7));

        Pageable pageable = PageRequest.of(page, Video.PAGE_LIMIT);
        Page<Video> videoPage = videoRepository.findVideos(pageable, search, username);

        if (videoPage.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Videos not found"));
        }
        return ResponseEntity.ok(VideoResponse.fromList(videoPage.toList()));
    }

    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getVideoById(@RequestHeader("Authorization") String headerAuth, @PathVariable Integer id) {

        String username = jwtUtils.getUserNameFromJwtToken(headerAuth.substring(7));
        Optional<Video> optionalVideo = videoRepository.findVideoById(id, username);

        if (optionalVideo.isPresent()) {
            return ResponseEntity.ok(VideoResponse.from(optionalVideo.get()));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Video not found"));
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> insertVideo(@RequestHeader("Authorization") String headerAuth,
                                         @Valid @RequestBody final VideoRequest videoRequest) {
        String username = jwtUtils.getUserNameFromJwtToken(headerAuth.substring(7));

        if (videoRequest.getCategoryId() == null) {
            videoRequest.setCategoryId(Category.FREE_CATEGORY);
        } else if (!categoryRepository.existsById(videoRequest.getCategoryId(), username)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Category not found"));
        }

        Video video = Video.from(videoRequest);
        video.setId(nextSequenceService.getNextSequence(Video.SEQUENCE_NAME));
        video.setUser(username);

        if (videoRepository.insertOrUpdateVideo(video)) {
            return ResponseEntity.ok(VideoResponse.from(video));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Video not inserted"));
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> updateVideo(@RequestHeader("Authorization") String headerAuth,
                                         @PathVariable Integer id, @Valid @RequestBody final VideoRequest videoRequest) {
        String username = jwtUtils.getUserNameFromJwtToken(headerAuth.substring(7));
        if (videoRepository.existsById(id, username)) {

            if (videoRequest.getCategoryId() == null) {
                videoRequest.setCategoryId(Category.FREE_CATEGORY);
            } else if (!categoryRepository.existsById(videoRequest.getCategoryId(), username)) {
                return ResponseEntity.badRequest().body(new MessageResponse("Category not found"));
            }

            Video video = Video.from(videoRequest);
            video.setId(id);
            video.setUser(username);

            if (videoRepository.insertOrUpdateVideo(video)) {
                return ResponseEntity.ok(VideoResponse.from(video));
            }
            return ResponseEntity.badRequest().body(new MessageResponse("Video not updated"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Video not found"));
        }
    }

    @DeleteMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> deleteVideo(@RequestHeader("Authorization") String headerAuth, @PathVariable Integer id) {
        String username = jwtUtils.getUserNameFromJwtToken(headerAuth.substring(7));

        if (videoRepository.existsById(id, username)) {
            if (videoRepository.deleteVideo(id, username)) {
                return ResponseEntity.ok(new MessageResponse("Deleted video"));
            }
            return ResponseEntity.internalServerError().build();
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Video not found"));
        }
    }
}
