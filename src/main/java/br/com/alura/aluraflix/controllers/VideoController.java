package br.com.alura.aluraflix.controllers;

import br.com.alura.aluraflix.controllers.request.VideoRequest;
import br.com.alura.aluraflix.controllers.response.MessageResponse;
import br.com.alura.aluraflix.controllers.response.VideoResponse;
import br.com.alura.aluraflix.models.Video;
import br.com.alura.aluraflix.services.CategoryService;
import br.com.alura.aluraflix.services.VideoService;
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

import static br.com.alura.aluraflix.controllers.Properties.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/videos")
@PreAuthorize("hasRole('USER') or hasRole('MODERATOR')")
public class VideoController {

    @Autowired
    VideoService videoService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    NextSequenceService nextSequenceService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getVideos(@RequestParam(required = false) String search, @RequestParam int page) {

        Pageable pageable = PageRequest.of(page, PAGE_LIMIT);

        Page<Video> videoPage = videoService.findVideos(pageable, search);

        if (videoPage.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Not Found Videos"));
        }
        return ResponseEntity.ok(VideoResponse.fromList(videoPage.toList()));
    }

    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getVideoById(@PathVariable Integer id) {
        Optional<Video> optionalVideo = videoService.findVideoById(id);

        if (optionalVideo.isPresent()) {
            return ResponseEntity.ok(VideoResponse.from(optionalVideo.get()));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Not Found Video"));
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> insertVideo(@Valid @RequestBody final VideoRequest videoRequest) {

        if (videoRequest.getCategoryId() == null) {
            videoRequest.setCategoryId(FREE_CATEGORY);
        } else if (!categoryService.existsById(videoRequest.getCategoryId())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Not Found Category"));
        }

        Video video = Video.from(videoRequest);
        video.setId(nextSequenceService.getNextSequence(Video.SEQUENCE_NAME));

        if (videoService.insertOrUpdateVideo(video)) {
            return ResponseEntity.ok(VideoResponse.from(video));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Not Inserted Video"));
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateVideo(@PathVariable Integer id, @Valid @RequestBody final VideoRequest videoRequest) {
        if (videoService.existsById(id)) {

            if (videoRequest.getCategoryId() == null) {
                videoRequest.setCategoryId(FREE_CATEGORY);
            } else if (!categoryService.existsById(videoRequest.getCategoryId())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Not Found Category"));
            }

            Video video = Video.from(videoRequest);
            video.setId(id);

            if (videoService.insertOrUpdateVideo(video)) {
                return ResponseEntity.ok(VideoResponse.from(video));
            }
            return ResponseEntity.badRequest().body(new MessageResponse("Not Updated Video"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Not Found Video"));
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteVideo(@PathVariable Integer id) {
        if (videoService.existsById(id)) {
            if (videoService.deleteVideo(id)) {
                return ResponseEntity.ok(new MessageResponse("Deleted Video"));
            }
            return ResponseEntity.internalServerError().build();
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Not Found Video"));
        }
    }
}
