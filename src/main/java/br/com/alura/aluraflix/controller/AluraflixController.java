package br.com.alura.aluraflix.controller;

import br.com.alura.aluraflix.controller.request.VideoRequest;
import br.com.alura.aluraflix.controller.response.VideoResponse;
import br.com.alura.aluraflix.domain.Video;
import br.com.alura.aluraflix.domain.exception.VideoException;
import br.com.alura.aluraflix.service.VideoService;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/videos")
public class AluraflixController {

    @Autowired
    VideoService videoService;

    @GetMapping
    public ResponseEntity<List<VideoResponse>> allVideos() {
        List<Video> videos = videoService.findAllVideos();
        if (Objects.isNull(videos)) {
            return ResponseEntity.ok().body(new ArrayList<>());
        }

        List<VideoResponse> videosResponses = videos.stream().map(VideoResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok().body(videosResponses);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<VideoResponse> videoById(@PathVariable Long id) {
        Video video = videoService.findVideoById(id);
        if (video == null) {
            return ResponseEntity.notFound().build();
        }
        VideoResponse videoResponse = VideoResponse.from(video);
        return ResponseEntity.ok().body(videoResponse);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<VideoResponse> insertVideo(@Valid @RequestBody final VideoRequest videoRequest) {
        Video video = Video.from(videoRequest);

        Boolean resp = videoService.insertVideo(video);
        if (resp) {
            VideoResponse videoResponse = VideoResponse.from(video);
            return ResponseEntity.ok().body(videoResponse);
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VideoResponse> updateVideo(@PathVariable Long id, @Valid @RequestBody final VideoRequest videoRequest) {
        Video video = videoService.findVideoById(id);
        if (video == null){
            return ResponseEntity.badRequest().build();
        }

        Boolean resp = videoService.updateVideo(video, Video.from(videoRequest));
        if (resp) {
            return ResponseEntity.ok().body(VideoResponse.from(video));
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteVideo(@PathVariable Long id) {
        Video video = videoService.findVideoById(id);
        if (video == null) {
            return ResponseEntity.badRequest().body("Problema ao Deletar: Vídeo Não Encontrado");
        }
        Boolean resp = videoService.deleteVideo(id);
        if (resp) {
            return ResponseEntity.ok("Vídeo Deletado!");
        }
        return ResponseEntity.badRequest().body("Problema ao Deletar");
    }
}
