package br.com.devcanoa.aluraflix.controller.response;

import br.com.devcanoa.aluraflix.models.Video;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@JsonInclude(Include.NON_NULL)
public class VideoResponse {

    private final Integer id;
    private final String link;
    private final String title;
    private final Integer categoryId;
    private final String description;

    public static VideoResponse from(final Video video) {
        return new VideoResponse(video.getId(), video.getLink(), video.getTitle(), video.getCategoryId(), video.getDescription());
    }

    public static List<VideoResponse> fromList(final List<Video> videoList) {
        return videoList.stream().map(VideoResponse::from).collect(Collectors.toList());
    }
}
