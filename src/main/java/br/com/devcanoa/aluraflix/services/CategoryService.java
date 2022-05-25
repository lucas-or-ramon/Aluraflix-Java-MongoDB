package br.com.devcanoa.aluraflix.services;

import br.com.devcanoa.aluraflix.builder.QueryBuilder;
import br.com.devcanoa.aluraflix.controller.request.CategoryRequest;
import br.com.devcanoa.aluraflix.controller.response.CategoryResponse;
import br.com.devcanoa.aluraflix.exception.video.CategoryNotFoundException;
import br.com.devcanoa.aluraflix.models.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CategoryService {

    private final MongoTemplate mongoTemplate;
    private final NextSequenceService nextSequenceService;

    @Autowired
    public CategoryService(MongoTemplate mongoTemplate, NextSequenceService nextSequenceService) {
        this.mongoTemplate = mongoTemplate;
        this.nextSequenceService = nextSequenceService;
    }

    public List<CategoryResponse> findCategories(int pageNumber, String username) {
        Pageable pageable = PageRequest.of(pageNumber, Category.PAGE_LIMIT);
        var query = QueryBuilder.builder().withUsername(username).with(pageable).get();

        var categoryPage = getCategoryPage(pageable, query);
        if (categoryPage.isEmpty()) {
            throw new CategoryNotFoundException("Categories Not Found");
        }

        return CategoryResponse.fromList(categoryPage.toList());
    }

    private Page<Category> getCategoryPage(Pageable pageable, Query query) {
        var categories = mongoTemplate.find(query, Category.class);
        var count = mongoTemplate.count(query.skip(-1).limit(-1), Category.class);
        return new PageImpl<>(categories, pageable, count);
    }

    public CategoryResponse findCategoryById(Integer id, String username) {
        var query = QueryBuilder.builder().withId(id).withUsername(username).get();
        var category = Optional.ofNullable(mongoTemplate.findOne(query, Category.class));

        return CategoryResponse.from(category.orElseThrow(() -> new CategoryNotFoundException("Category not found")));
    }

    public CategoryResponse insertOrUpdateCategory(Integer id, String username, CategoryRequest categoryRequest) {
        var categoryId = Objects.isNull(id) ? nextSequenceService.getNextSequence(Category.SEQUENCE_NAME) : id;
        var category = Category.from(categoryId, username, categoryRequest);

        return CategoryResponse.from(mongoTemplate.save(category));
    }

    public void deleteCategory(int id, String username) {
        var query = QueryBuilder.builder().withId(id).withUsername(username).get();
        mongoTemplate.remove(query, Category.class);
    }

    public boolean existsById(int id, String username) {
        var query = QueryBuilder.builder().withId(id).withUsername(username).get();
        return mongoTemplate.exists(query, Category.class);
    }
}
