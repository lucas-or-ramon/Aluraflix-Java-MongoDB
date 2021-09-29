package br.com.alura.aluraflix.controllers;

import br.com.alura.aluraflix.controllers.response.VideoResponse;
import br.com.alura.aluraflix.models.Video;
import br.com.alura.aluraflix.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static br.com.alura.aluraflix.controllers.Properties.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/public/free")
public class PublicVideoController {

    @Autowired
    VideoService videoService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getFreeVideos(@RequestParam int page) {

        Pageable pageable = PageRequest.of(page, PAGE_LIMIT);

        Page<Video> videoPage = videoService.findVideosByCategory(pageable, FREE_CATEGORY);

        if (videoPage.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(VideoResponse.fromList(videoPage.toList()));
    }
}
