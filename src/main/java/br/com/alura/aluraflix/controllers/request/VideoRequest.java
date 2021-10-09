package br.com.alura.aluraflix.controllers.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class VideoRequest {

    @NotBlank
    @Size(max = 80)
    @JsonProperty("title")
    private String title;

    @NotBlank
    @Size(max = 500)
    @JsonProperty("description")
    private String description;

    @NotBlank
    @URL
    @JsonProperty("link")
    private String link;

    @JsonProperty("categoryId")
    private Integer categoryId;

    public VideoRequest(String title, String description, String link, Integer categoryId) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.categoryId = categoryId;
    }
}
