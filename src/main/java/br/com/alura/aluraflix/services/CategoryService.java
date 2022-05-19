package br.com.alura.aluraflix.services;

import br.com.alura.aluraflix.builder.QueryBuilder;
import br.com.alura.aluraflix.controllers.response.CategoryResponse;
import br.com.alura.aluraflix.exception.video.CategoryNotFoundException;
import br.com.alura.aluraflix.models.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public CategoryService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<CategoryResponse> findCategories(int pageNumber, String username) {
        Pageable pageable = PageRequest.of(pageNumber, Category.PAGE_LIMIT);

        var query = QueryBuilder.builder().withUsername(username).with(pageable).get();

        List<Category> categories = mongoTemplate.find(query, Category.class);
        long count = mongoTemplate.count(query.skip(-1).limit(-1), Category.class);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, count);

        if (categoryPage.isEmpty()) {
            throw new CategoryNotFoundException("Categories Not Found");
        }

        return CategoryResponse.fromList(categoryPage.toList());
    }

    public CategoryResponse findCategoryById(Integer id, String username) {
        var query = QueryBuilder.builder().withId(id).withUsername(username).get();
        var category = Optional.ofNullable(mongoTemplate.findOne(query, Category.class));

        return CategoryResponse.from(category.orElseThrow(() -> new CategoryNotFoundException("Category not found")));
    }

    public boolean insertOrUpdateCategory(final Category category) {
        try {
            mongoTemplate.save(category);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteCategory(Integer id, String username) {
        try {
            mongoTemplate.remove(getQueryById(id, username), Category.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean existsById(Integer id, String username) {
        try {
            return mongoTemplate.exists(getQueryById(id, username), Category.class);
        } catch (Exception e) {
            return false;
        }
    }

    public Query getQueryById(Integer id, String username) {
        Query query = getQueryWithUserCriteria(username);
        return query.addCriteria(Criteria.where("id").is(id));
    }

    public Query getQueryWithUserCriteria(String username) {
        return Query.query(Criteria.where("user").is(username));
    }
}
