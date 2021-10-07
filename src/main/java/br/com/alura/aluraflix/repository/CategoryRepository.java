package br.com.alura.aluraflix.repository;

import br.com.alura.aluraflix.models.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    Page<Category> findCategories(Pageable pageable);
    Optional<Category> findCategoryById(Integer id);
    Boolean insertOrUpdateCategory(Category category);
    Boolean deleteCategory(Integer id);
    Boolean existsById(Integer id);
}
