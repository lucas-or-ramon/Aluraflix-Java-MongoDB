package br.com.alura.aluraflix.controllers.response;

import br.com.alura.aluraflix.models.Category;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class CategoryResponse {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("color")
    private String color;

    private CategoryResponse(final Category category) {
        this.id = category.getId();
        this.title = category.getTitle();
        this.color = category.getColor();
    }

    public static CategoryResponse from(final Category category){
        return new CategoryResponse(category);
    }

    public static List<CategoryResponse> fromList(List<Category> categories) {
        return categories.stream().map(CategoryResponse::from).collect(Collectors.toList());
    }
}
