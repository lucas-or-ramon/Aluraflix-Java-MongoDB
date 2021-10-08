package br.com.alura.aluraflix.repository;

import br.com.alura.aluraflix.models.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    Page<Category> findCategories(Pageable pageable, String username);
    Optional<Category> findCategoryById(Integer id, String username);
    Boolean insertOrUpdateCategory(Category category);
    Boolean deleteCategory(Integer id, String username);
    Boolean existsById(Integer id, String username);
}
