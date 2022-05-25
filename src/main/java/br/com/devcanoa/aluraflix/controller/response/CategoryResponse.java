package br.com.devcanoa.aluraflix.controller.response;

import br.com.devcanoa.aluraflix.models.Category;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@JsonInclude(Include.NON_NULL)
public class CategoryResponse {

    private final Integer id;
    private final String title;
    private final String color;

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getTitle(), category.getColor());
    }

    public static List<CategoryResponse> fromList(List<Category> categories) {
        return categories.stream().map(CategoryResponse::from).collect(Collectors.toList());
    }
}
