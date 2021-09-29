package br.com.alura.aluraflix.repository;

import br.com.alura.aluraflix.models.Category;
import br.com.alura.aluraflix.models.Video;
import br.com.alura.aluraflix.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CategoryRepository implements CategoryService {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public Page<Category> findCategories(Pageable pageable) {
        try {
            Query query = new Query().with(pageable);
            List<Category> categoryPage = mongoTemplate.findAll(Category.class);
            long count = mongoTemplate.count(query.skip(-1).limit(-1), Category.class);
            return new PageImpl<>(categoryPage, pageable, count);
        } catch (Exception e) {
            return Page.empty();
        }
    }

    @Override
    public Optional<Category> findCategoryById(Integer id) {
        try {
            return Optional.ofNullable(mongoTemplate.findOne(getQueryById(id), Category.class));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Boolean insertOrUpdateCategory(final Category category) {
        try {
            mongoTemplate.save(category);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean deleteCategory(Integer id) {
        try {
            mongoTemplate.remove(getQueryById(id), Category.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean existsById(Integer id) {
        try {
            return mongoTemplate.exists(getQueryById(id), Category.class);
        } catch (Exception e) {
            return false;
        }
    }

    public Query getQueryById(Integer id) {
        return Query.query(Criteria.where("id").is(id));
    }
}
