package br.com.alura.aluraflix.controllers;

import br.com.alura.aluraflix.controllers.request.VideoRequest;
import br.com.alura.aluraflix.controllers.response.MessageResponse;
import br.com.alura.aluraflix.controllers.response.VideoResponse;
import br.com.alura.aluraflix.models.Category;
import br.com.alura.aluraflix.models.Video;
import br.com.alura.aluraflix.repository.CategoryRepository;
import br.com.alura.aluraflix.repository.VideoRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/videos")
@PreAuthorize("hasRole('USER') or hasRole('MODERATOR')")
public class VideoController {

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    NextSequenceService nextSequenceService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getVideos(@RequestParam(required = false) String search, @RequestParam int page) {

        Pageable pageable = PageRequest.of(page, Video.PAGE_LIMIT);

        Page<Video> videoPage = videoRepository.findVideos(pageable, search);

        if (videoPage.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Videos not found"));
        }
        return ResponseEntity.ok(VideoResponse.fromList(videoPage.toList()));
    }

    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getVideoById(@PathVariable Integer id) {
        Optional<Video> optionalVideo = videoRepository.findVideoById(id);

        if (optionalVideo.isPresent()) {
            return ResponseEntity.ok(VideoResponse.from(optionalVideo.get()));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Video not found"));
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> insertVideo(@Valid @RequestBody final VideoRequest videoRequest) {

        if (videoRequest.getCategoryId() == null) {
            videoRequest.setCategoryId(Category.FREE_CATEGORY);
        } else if (!categoryRepository.existsById(videoRequest.getCategoryId())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Category not found"));
        }

        Video video = Video.from(videoRequest);
        video.setId(nextSequenceService.getNextSequence(Video.SEQUENCE_NAME));

        if (videoRepository.insertOrUpdateVideo(video)) {
            return ResponseEntity.ok(VideoResponse.from(video));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Video not found"));
    }

    @PostMapping(
            value = "/many",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<ResponseEntity<?>> insertManyVideos(@Valid @RequestBody final List<VideoRequest> videoRequests) {
        return videoRequests.stream().map(this::insertVideo).collect(Collectors.toList());
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> updateVideo(@PathVariable Integer id, @Valid @RequestBody final VideoRequest videoRequest) {
        if (videoRepository.existsById(id)) {

            if (videoRequest.getCategoryId() == null) {
                videoRequest.setCategoryId(Category.FREE_CATEGORY);
            } else if (!categoryRepository.existsById(videoRequest.getCategoryId())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Category not found"));
            }

            Video video = Video.from(videoRequest);
            video.setId(id);

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
    public ResponseEntity<?> deleteVideo(@PathVariable Integer id) {
        if (videoRepository.existsById(id)) {
            if (videoRepository.deleteVideo(id)) {
                return ResponseEntity.ok(new MessageResponse("Deleted video"));
            }
            return ResponseEntity.internalServerError().build();
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Video not found"));
        }
    }
}
