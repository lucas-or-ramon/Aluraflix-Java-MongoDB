package br.com.alura.aluraflix.models;

import br.com.alura.aluraflix.controllers.request.CategoryRequest;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "alura_categories")
public class Category {

    public static final String SEQUENCE_NAME = "categorySequence";

    @Id
    private Integer id;
    private String title;
    private String color;

    public Category() {
    }

    public Category(Integer id, String title, String color) {
        this.id = id;
        this.title = title;
        this.color = color;
    }

    private Category(CategoryRequest categoryRequest) {
        this.title = categoryRequest.getTitle();
        this.color = categoryRequest.getColor();
    }

    public static Category from(final CategoryRequest categoryRequest) {
        return new Category(categoryRequest);
    }
}
