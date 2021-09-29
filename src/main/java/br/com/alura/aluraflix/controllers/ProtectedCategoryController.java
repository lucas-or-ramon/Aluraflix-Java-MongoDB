package br.com.alura.aluraflix.controllers;

import br.com.alura.aluraflix.controllers.request.CategoryRequest;
import br.com.alura.aluraflix.controllers.response.CategoryResponse;
import br.com.alura.aluraflix.controllers.response.MessageResponse;
import br.com.alura.aluraflix.controllers.response.VideoResponse;
import br.com.alura.aluraflix.models.Category;
import br.com.alura.aluraflix.models.Video;
import br.com.alura.aluraflix.services.CategoryService;
import br.com.alura.aluraflix.services.NextSequenceService;
import br.com.alura.aluraflix.services.VideoService;
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

import static br.com.alura.aluraflix.controllers.Properties.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/protected/categorias")
@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
public class ProtectedCategoryController {

    @Autowired
    VideoService videoService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    NextSequenceService nextSequenceService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCategories(@RequestParam int page) {

        Pageable pageable = PageRequest.of(page, PAGE_LIMIT);

        Page<Category> categoryPage = categoryService.findCategories(pageable);

        if (categoryPage.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Not Found Categories"));
        }
        return ResponseEntity.ok(CategoryResponse.fromList(categoryPage.toList()));
    }

    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getCategoryById(@PathVariable Integer id) {
        Optional<Category> optionalCategory = categoryService.findCategoryById(id);

        if (optionalCategory.isPresent()) {
            return ResponseEntity.ok(CategoryResponse.from(optionalCategory.get()));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Not Found Category"));
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> insertCategory(@Valid @RequestBody final CategoryRequest categoryRequest) {
        Category category = Category.from(categoryRequest);
        category.setId(nextSequenceService.getNextSequence(Video.SEQUENCE_NAME));
        if (categoryService.insertOrUpdateCategory(category)) {
            return ResponseEntity.ok(CategoryResponse.from(category));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Not Inserted Category"));
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> updateCategory(@PathVariable Integer id, @Valid @RequestBody final CategoryRequest categoryRequest) {
        if (categoryService.existsById(id)) {
            Category category = Category.from(categoryRequest);
            category.setId(id);

            if (categoryService.insertOrUpdateCategory(category)) {
                return ResponseEntity.ok(CategoryResponse.from(category));
            }
            return ResponseEntity.badRequest().body(new MessageResponse("Not Updated Category"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Not Found Category"));
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Integer id) {
        if (categoryService.existsById(id)) {
            if (categoryService.deleteCategory(id)) {
                return ResponseEntity.ok(new MessageResponse("Deleted Category"));
            }
            return ResponseEntity.internalServerError().build();
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Not Found Category"));
        }
    }

    @GetMapping(
            value = "/{categoryId}/videos",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getVideosByCategory(@PathVariable Integer categoryId, @RequestParam int page) {

        Pageable pageable = PageRequest.of(page, PAGE_LIMIT);

        Page<Video> videoPage = videoService.findVideosByCategory(pageable, categoryId);

        if (videoPage.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Not Found Videos"));
        }
        return ResponseEntity.ok(VideoResponse.fromList(videoPage.toList()));
    }
}
