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
public class VideoController {

    @Autowired
    VideoService videoService;


    @GetMapping
    public @ResponseBody List<VideoResponse> allVideos() {
        List<Video> videos = videoService.findAllVideos();
        if (Objects.isNull(videos)) {
            return new ArrayList<>();
        }
        return videos.stream().map(VideoResponse::from).collect(Collectors.toList());
    }

    @GetMapping(value = "/{id}")
    public @ResponseBody VideoResponse videoById(@PathVariable Long id) {
        Video video = videoService.findVideoById(id);
        if (video == null) {
            return new VideoResponse();
        }
        return VideoResponse.from(video);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody VideoResponse insertVideo(@Valid @RequestBody final VideoRequest videoRequest) {
        Video video = Video.from(videoRequest);

        Boolean resp = videoService.insertVideo(video);
        if (resp) {
            return VideoResponse.from(video);
        }
        return new VideoResponse();
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody VideoResponse updateVideo(@PathVariable Long id, @Valid @RequestBody final VideoRequest videoRequest) {
        Video video = videoService.findVideoById(id);
        if (video == null){
            return new VideoResponse();
        }

        Boolean resp = videoService.updateVideo(video, Video.from(videoRequest));
        if (resp) {
            return VideoResponse.from(video);
        }
        return new VideoResponse();
    }

    @DeleteMapping(value = "/{id}")
    public @ResponseBody String deleteVideo(@PathVariable Long id) {
        Video video = videoService.findVideoById(id);
        if (video == null) {
            return "Problema ao Deletar: Vídeo Não Encontrado";
        }
        Boolean resp = videoService.deleteVideo(id);
        if (resp) {
            return "Vídeo Deletado!";
        }
        return "Problema ao Deletar";
    }
}
