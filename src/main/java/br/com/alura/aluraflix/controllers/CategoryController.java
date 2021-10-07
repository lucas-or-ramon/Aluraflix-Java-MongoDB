package br.com.alura.aluraflix.controllers;

import br.com.alura.aluraflix.controllers.request.CategoryRequest;
import br.com.alura.aluraflix.controllers.response.CategoryResponse;
import br.com.alura.aluraflix.controllers.response.MessageResponse;
import br.com.alura.aluraflix.controllers.response.VideoResponse;
import br.com.alura.aluraflix.models.Category;
import br.com.alura.aluraflix.models.Video;
import br.com.alura.aluraflix.repository.CategoryRepository;
import br.com.alura.aluraflix.repository.VideoRepository;
import br.com.alura.aluraflix.services.NextSequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/api/categories")
@PreAuthorize("hasRole('USER') or hasRole('MODERATOR')")
public class CategoryController {

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    NextSequenceService nextSequenceService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCategories(@RequestParam int page) {

        Pageable pageable = PageRequest.of(page, Category.PAGE_LIMIT);

        Page<Category> categoryPage = categoryRepository.findCategories(pageable);

        if (categoryPage.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Categories not found"));
        }
        return ResponseEntity.ok(CategoryResponse.fromList(categoryPage.toList()));
    }

    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getCategoryById(@PathVariable Integer id) {
        Optional<Category> optionalCategory = categoryRepository.findCategoryById(id);

        if (optionalCategory.isPresent()) {
            return ResponseEntity.ok(CategoryResponse.from(optionalCategory.get()));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Category not found"));
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> insertCategory(@Valid @RequestBody final CategoryRequest categoryRequest) {
        Category category = Category.from(categoryRequest);
        category.setId(nextSequenceService.getNextSequence(Category.SEQUENCE_NAME));
        if (categoryRepository.insertOrUpdateCategory(category)) {
            return ResponseEntity.ok(CategoryResponse.from(category));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Category not inserted"));
    }

    @PostMapping(
            value = "/many",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<ResponseEntity<?>> insertManyCategories(@Valid @RequestBody final List<CategoryRequest> categoryRequests) {
        return categoryRequests.stream().map(this::insertCategory).collect(Collectors.toList());
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> updateCategory(@PathVariable Integer id, @Valid @RequestBody final CategoryRequest categoryRequest) {
        if (categoryRepository.existsById(id)) {
            Category category = Category.from(categoryRequest);
            category.setId(id);

            if (categoryRepository.insertOrUpdateCategory(category)) {
                return ResponseEntity.ok(CategoryResponse.from(category));
            }
            return ResponseEntity.badRequest().body(new MessageResponse("Category not updated"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Category not found"));
        }
    }

    @DeleteMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> deleteCategory(@PathVariable Integer id) {
        if (categoryRepository.existsById(id)) {
            if (categoryRepository.deleteCategory(id)) {
                return ResponseEntity.ok(new MessageResponse("Deleted category"));
            }
            return ResponseEntity.internalServerError().build();
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Category not found"));
        }
    }

    @GetMapping(
            value = "/{categoryId}/videos",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getVideosByCategory(@PathVariable Integer categoryId, @RequestParam int page) {

        if (categoryRepository.existsById(categoryId)) {
            Pageable pageable = PageRequest.of(page, Video.PAGE_LIMIT);

            Page<Video> videoPage = videoRepository.findVideosByCategory(pageable, categoryId);

            if (videoPage.isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Videos not found"));
            }
            return ResponseEntity.ok(VideoResponse.fromList(videoPage.toList()));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Category not found"));
        }
    }
}
