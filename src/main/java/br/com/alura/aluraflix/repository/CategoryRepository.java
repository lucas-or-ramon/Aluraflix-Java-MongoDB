package br.com.alura.aluraflix.repository;

import br.com.alura.aluraflix.models.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CategoryRepository {
    Page<Category> findCategories(Pageable pageable, String username);

    Optional<Category> findCategoryById(Integer id, String username);

    boolean insertOrUpdateCategory(Category category);

    boolean deleteCategory(Integer id, String username);

    boolean existsById(Integer id, String username);
}
