package br.com.devcanoa.aluraflix.models;

import br.com.devcanoa.aluraflix.controller.request.CategoryRequest;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "categories")
public class Category {

    public static final Integer PAGE_LIMIT = 10;
    public static final Integer FREE_CATEGORY = 1;
    public static final String SEQUENCE_NAME = "categorySequence";

    @Id
    private final Integer id;
    private final String color;
    private final String title;
    private final String username;

    public static Category from(Integer id, String username, CategoryRequest categoryRequest) {
        return new Category(id, categoryRequest.getColor(), categoryRequest.getTitle(), username);
    }
}
