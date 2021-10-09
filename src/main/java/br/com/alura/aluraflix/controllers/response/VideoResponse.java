package br.com.alura.aluraflix.controllers.response;

import br.com.alura.aluraflix.models.Video;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class VideoResponse {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("categoryId")
    private Integer categoryId;

    @JsonProperty("description")
    private String description;

    @JsonProperty("link")
    private String link;

    private VideoResponse(final Video video) {
        this.id = video.getId();
        this.title = video.getTitle();
        this.description = video.getDescription();
        this.link = video.getLink();
        this.categoryId = video.getCategoryId();
    }

    public static VideoResponse from(final Video video) {
        return new VideoResponse(video);
    }

    public static List<VideoResponse> fromList(final List<Video> videoList) {
        return videoList.stream().map(VideoResponse::from).collect(Collectors.toList());
    }
}
