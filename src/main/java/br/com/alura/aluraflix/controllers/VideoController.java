package br.com.alura.aluraflix.controllers;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.alura.aluraflix.controllers.request.VideoRequest;
import br.com.alura.aluraflix.controllers.response.MessageResponse;
import br.com.alura.aluraflix.controllers.response.VideoResponse;
import br.com.alura.aluraflix.models.Video;
import br.com.alura.aluraflix.repository.CategoryRepository;
import br.com.alura.aluraflix.repository.VideoRepository;
import br.com.alura.aluraflix.security.jwt.JwtUtils;
import br.com.alura.aluraflix.services.NextSequenceService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/videos")
@PreAuthorize("hasRole('USER')")
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
            @RequestParam(required = false) String search, @RequestParam int page) {

        Pageable pageable = PageRequest.of(page, Video.PAGE_LIMIT);
        Page<Video> videoPage = videoRepository.findVideos(pageable, search, getUsername(headerAuth));

        return videoPage.isEmpty() ? videoNotFound() : ResponseEntity.ok(VideoResponse.fromList(videoPage.toList()));
    }

    private String getUsername(String headerAuth) {
        return jwtUtils.getUserNameFromJwtToken(headerAuth.substring(7));
    }

    private ResponseEntity<MessageResponse> videoNotFound() {
        return ResponseEntity.badRequest().body(new MessageResponse("Video not found"));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getVideoById(@RequestHeader("Authorization") String headerAuth, @PathVariable Integer id) {

        Optional<Video> optionalVideo = videoRepository.findVideoById(id, getUsername(headerAuth));

        return optionalVideo.isPresent() ? okFrom(optionalVideo.get()) : videoNotFound();
    }

    private ResponseEntity<?> okFrom(Video video) {
        return ResponseEntity.ok(VideoResponse.from(video));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> insertVideo(@RequestHeader("Authorization") String headerAuth,
            @Valid @RequestBody final VideoRequest videoRequest) {

        String username = getUsername(headerAuth);
        if (!videoRequest.setFreeCategory() && !categoryRepository.existsById(videoRequest.getCategoryId(), username)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Category not found"));
        }

        int id = nextSequenceService.getNextSequence(Video.SEQUENCE_NAME);
        Video video = Video.from(id, username, videoRequest);

        return videoRepository.insertOrUpdateVideo(video) ? okFrom(video)
                : ResponseEntity.badRequest().body(new MessageResponse("Video not inserted"));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateVideo(@RequestHeader("Authorization") String headerAuth, @PathVariable Integer id,
            @Valid @RequestBody final VideoRequest videoRequest) {

        String username = getUsername(headerAuth);
        if (videoRepository.existsById(id, username)) {

            if (!videoRequest.setFreeCategory()
                    && !categoryRepository.existsById(videoRequest.getCategoryId(), username)) {
                return ResponseEntity.badRequest().body(new MessageResponse("Category not found"));
            }

            Video video = Video.from(id, username, videoRequest);

            return videoRepository.insertOrUpdateVideo(video) ? okFrom(video)
                    : ResponseEntity.badRequest().body(new MessageResponse("Video not updated"));
        }
        return videoNotFound();
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteVideo(@RequestHeader("Authorization") String headerAuth, @PathVariable Integer id) {
        
        String username = getUsername(headerAuth);
        if (videoRepository.existsById(id, username)) {
            return videoRepository.deleteVideo(id, username) ? ResponseEntity.ok(new MessageResponse("Deleted video"))
                    : ResponseEntity.internalServerError().build();
        }
        return videoNotFound();
    }
}
