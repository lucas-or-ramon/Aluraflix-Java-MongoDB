package br.com.alura.aluraflix.models;

import br.com.alura.aluraflix.controllers.request.VideoRequest;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import static br.com.alura.aluraflix.controllers.Properties.*;

@Data
@Document(collection = "alura_videos")
public class Video {

    public static final String SEQUENCE_NAME = "videoSequences";

    @Id
    private Integer id;
    private Integer categoryId;
    private String title;
    private String description;
    private String link;

    public Video() {}

    public Video(Integer id, Integer categoryId, String title, String description, String link) {
        this.id = id;
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.link = link;
    }

    private Video(final VideoRequest videoRequest) {
        this.title = videoRequest.getTitle();
        this.description = videoRequest.getDescription();
        this.link = videoRequest.getLink();
        this.categoryId = videoRequest.getCategoryId();
    }

    public static Video from(final VideoRequest videoRequest) {
        return new Video(videoRequest);
    }
}
