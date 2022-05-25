package br.com.devcanoa.aluraflix.controller;

import java.util.List;

import javax.validation.Valid;

import br.com.devcanoa.aluraflix.services.VideoService;
import br.com.devcanoa.aluraflix.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import br.com.devcanoa.aluraflix.controller.request.VideoRequest;
import br.com.devcanoa.aluraflix.controller.response.VideoResponse;

@RestController
@PreAuthorize("hasRole('USER')")
@RequestMapping("/api/videos")
@CrossOrigin(origins = "*", maxAge = 3600)
public class VideoController {

    private final JwtUtils jwtUtils;
    private final VideoService videoService;

    @Autowired
    public VideoController(JwtUtils jwtUtils, VideoService videoService) {
        this.jwtUtils = jwtUtils;
        this.videoService = videoService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<VideoResponse>> getVideos(@RequestParam int pageNumber,
                                                         @RequestParam(required = false) String search,
                                                         @RequestHeader("Authorization") String headerAuth) {

        return ResponseEntity.ok(videoService.findVideos(pageNumber, search, getUsername(headerAuth)));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VideoResponse> getVideoById(@PathVariable int id, @RequestHeader("Authorization") String headerAuth) {

        return ResponseEntity.ok(videoService.findVideoById(id, getUsername(headerAuth)));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VideoResponse> insertVideo(@RequestHeader("Authorization") String headerAuth,
                                                     @Valid @RequestBody final VideoRequest videoRequest) {

        return ResponseEntity.ok(videoService.insertOrUpdateVideo(null, getUsername(headerAuth), videoRequest));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VideoResponse> updateVideo(@PathVariable int id,
                                                     @RequestHeader("Authorization") String headerAuth,
                                                     @Valid @RequestBody final VideoRequest videoRequest) {

        return ResponseEntity.ok(videoService.insertOrUpdateVideo(id, getUsername(headerAuth), videoRequest));
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteVideo(@PathVariable Integer id, @RequestHeader("Authorization") String headerAuth) {

        videoService.deleteVideo(id, getUsername(headerAuth));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<VideoResponse>> getVideosByCategory(@RequestParam int pageNumber,
                                                                   @PathVariable int categoryId,
                                                                   @RequestHeader("Authorization") String headerAuth) {

        return ResponseEntity.ok(videoService.findVideosByCategory(pageNumber, categoryId, getUsername(headerAuth)));
    }

    private String getUsername(String headerAuth) {
        return jwtUtils.getUserNameFromJwtToken(headerAuth.substring(7));
    }
}
