package br.com.alura.aluraflix.controllers.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CategoryRequest {

    @NotBlank
    @Size(max = 20)
    @JsonProperty("title")
    private String title;

    @NotBlank
    @Size(max = 20)
    @JsonProperty("color")
    private String color;

    public CategoryRequest(String title, String color) {
        this.title = title;
        this.color = color;
    }
}
