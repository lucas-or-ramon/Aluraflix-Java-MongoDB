package br.com.alura.aluraflix.controller.response;

import br.com.alura.aluraflix.domain.Video;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VideoResponse {

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("link")
    private String link;

    public VideoResponse() {}

    private VideoResponse(final Video video) {
        this.title = video.getTitle();
        this.description = video.getDescription();
        this.link = video.getLink();
    }

    public static VideoResponse from(final Video video){
        return new VideoResponse(video);
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }
}
