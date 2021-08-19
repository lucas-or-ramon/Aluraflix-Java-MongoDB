package br.com.alura.aluraflix.domain;

import br.com.alura.aluraflix.controller.request.VideoRequest;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "alura_videos")
public class Video {

    public static final String SEQUENCE_NAME = "customSequences";

    @Id
    private Long id;
    private String title;
    private String description;
    private String link;

    public Video() {
    }

    public Video(String id, String title, String description, String link) {
        this.title = title;
        this.description = description;
        this.link = link;
    }

    private Video(VideoRequest videoRequest) {
        this.title = videoRequest.getTitle();
        this.description = videoRequest.getDescription();
        this.link = videoRequest.getLink();
    }

    public static Video from(final VideoRequest videoRequest) {
        return new Video(videoRequest);
    }

    public Long getId() {
        return id;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
