package br.com.alura.aluraflix.models;

import br.com.alura.aluraflix.controllers.request.CategoryRequest;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "categories")
public class Category {

    public static final String SEQUENCE_NAME = "categorySequence";
    public static final Integer FREE_CATEGORY = 1;
    public static final Integer PAGE_LIMIT = 10;
    @Id
    private Integer id;
    private String title;
    private String color;
    private String user;

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
