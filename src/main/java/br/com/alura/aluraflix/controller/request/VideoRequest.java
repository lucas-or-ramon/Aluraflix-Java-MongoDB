package br.com.alura.aluraflix.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class VideoRequest {

    @NotNull
    @Size(max = 20, message = "Campo com no máximo 20 caracters")
    @JsonProperty("title")
    private String title;

    @NotNull
    @Size(max = 100)
    @JsonProperty("description")
    private String description;

    @NotNull
    @URL
    @JsonProperty("link")
    private String link;

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
