package br.com.alura.aluraflix.controllers;

import br.com.alura.aluraflix.controllers.request.CategoryRequest;
import br.com.alura.aluraflix.controllers.response.CategoryResponse;
import br.com.alura.aluraflix.controllers.response.MessageResponse;
import br.com.alura.aluraflix.models.Category;
import br.com.alura.aluraflix.security.jwt.JwtUtils;
import br.com.alura.aluraflix.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(value = "/api/categories")
@PreAuthorize("hasRole('USER') or hasRole('MODERATOR')")
public class CategoryController {

    private final JwtUtils jwtUtils;
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(JwtUtils jwtUtils, CategoryService categoryService) {
        this.jwtUtils = jwtUtils;
        this.categoryService = categoryService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCategories(@RequestParam int pageNumber, @RequestHeader("Authorization") String headerAuth) {

        return ResponseEntity.ok(categoryService.findCategories(pageNumber, getUsername(headerAuth)));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCategoryById(@PathVariable int id, @RequestHeader("Authorization") String headerAuth) {

        return ResponseEntity.ok(categoryService.findCategoryById(id, getUsername(headerAuth)));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> insertCategory(@RequestHeader("Authorization") String headerAuth,
            @Valid @RequestBody final CategoryRequest categoryRequest) {

        int id = nextSequenceService.getNextSequence(Category.SEQUENCE_NAME);
        Category category = Category.from(id, getUsername(headerAuth), categoryRequest);

        boolean updated = categoryService.insertOrUpdateCategory(category);

        return updated ? okFrom(category)
                : ResponseEntity.badRequest().body(new MessageResponse("Category not inserted"));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateCategory(@RequestHeader("Authorization") String headerAuth, @PathVariable Integer id,
            @Valid @RequestBody final CategoryRequest categoryRequest) {

        String username = getUsername(headerAuth);
        if (categoryService.existsById(id, username)) {
            Category category = Category.from(id, username, categoryRequest);

            boolean updated = categoryService.insertOrUpdateCategory(category);

            return updated ? okFrom(category)
                    : ResponseEntity.badRequest().body(new MessageResponse("Category not updated"));
        }
        return categoryNotFound();
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteCategory(@RequestHeader("Authorization") String headerAuth,
            @PathVariable Integer id) {

        String username = getUsername(headerAuth);
        if (categoryService.existsById(id, username)) {
            boolean deleted = categoryService.deleteCategory(id, username);

            return deleted ? ResponseEntity.ok(new MessageResponse("Deleted category"))
                    : ResponseEntity.internalServerError().build();
        }
        return categoryNotFound();
    }

    private String getUsername(String headerAuth) {
        return jwtUtils.getUserNameFromJwtToken(headerAuth.substring(7));
    }
}
