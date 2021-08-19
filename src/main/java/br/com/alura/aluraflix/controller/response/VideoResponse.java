package br.com.alura.aluraflix.controller.response;

import br.com.alura.aluraflix.domain.Video;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VideoResponse {

    @JsonProperty("titulo")
    private String titulo;

    @JsonProperty("descricao")
    private String descricao;

    @JsonProperty("link")
    private String link;

    public VideoResponse(final Video video) {
        this.titulo = video.getTitle();
        this.descricao = video.getDescription();
        this.link = video.getLink();
    }

    public static VideoResponse from(final Video video){
        return new VideoResponse(video);
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getLink() {
        return link;
    }
}
