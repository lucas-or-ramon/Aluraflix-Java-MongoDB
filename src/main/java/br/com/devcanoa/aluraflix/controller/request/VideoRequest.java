package br.com.devcanoa.aluraflix.controller.request;

import br.com.devcanoa.aluraflix.models.Category;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class VideoRequest {

    @NotBlank
    @Size(max = 80)
    private String title;

    @NotBlank
    @Size(max = 500)
    private String description;

    @NotBlank
    @URL
    private String link;

    private Integer categoryId;

    public Integer getCategoryId() {
        if (Objects.isNull(categoryId)) {
            return Category.FREE_CATEGORY;
        }
        return categoryId;
    }
}
