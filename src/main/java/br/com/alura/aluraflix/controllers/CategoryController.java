package br.com.alura.aluraflix.controllers;

import br.com.alura.aluraflix.controllers.request.CategoryRequest;
import br.com.alura.aluraflix.controllers.response.CategoryResponse;
import br.com.alura.aluraflix.controllers.response.MessageResponse;
import br.com.alura.aluraflix.controllers.response.VideoResponse;
import br.com.alura.aluraflix.models.Category;
import br.com.alura.aluraflix.models.Video;
import br.com.alura.aluraflix.repository.CategoryRepository;
import br.com.alura.aluraflix.repository.VideoRepository;
import br.com.alura.aluraflix.security.jwt.JwtUtils;
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
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('USER') or hasRole('MODERATOR')")
@RequestMapping(value = "/api/categories")
public class CategoryController {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    NextSequenceService nextSequenceService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCategories(@RequestHeader("Authorization") String headerAuth, @RequestParam int page) {

        Pageable pageable = PageRequest.of(page, Category.PAGE_LIMIT);
        Page<Category> categoryPage = categoryRepository.findCategories(pageable, getUsername(headerAuth));

        return categoryPage.isEmpty() ? categoryNotFound()
                : ResponseEntity.ok(CategoryResponse.fromList(categoryPage.toList()));
    }

    private String getUsername(String headerAuth) {
        return jwtUtils.getUserNameFromJwtToken(headerAuth.substring(7));
    }

    private ResponseEntity<MessageResponse> categoryNotFound() {
        return ResponseEntity.badRequest().body(new MessageResponse("Categoriy not found"));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCategoryById(@RequestHeader("Authorization") String headerAuth,
            @PathVariable Integer id) {

        Optional<Category> optionalCategory = categoryRepository.findCategoryById(id, getUsername(headerAuth));

        return optionalCategory.isPresent() ? okFrom(optionalCategory.get()) : categoryNotFound();
    }

    private ResponseEntity<?> okFrom(Category category) {
        return ResponseEntity.ok(CategoryResponse.from(category));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> insertCategory(@RequestHeader("Authorization") String headerAuth,
            @Valid @RequestBody final CategoryRequest categoryRequest) {

        int id = nextSequenceService.getNextSequence(Category.SEQUENCE_NAME);
        Category category = Category.from(id, getUsername(headerAuth), categoryRequest);

        boolean updated = categoryRepository.insertOrUpdateCategory(category);

        return updated ? okFrom(category)
                : ResponseEntity.badRequest().body(new MessageResponse("Category not inserted"));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateCategory(@RequestHeader("Authorization") String headerAuth, @PathVariable Integer id,
            @Valid @RequestBody final CategoryRequest categoryRequest) {

        String username = getUsername(headerAuth);
        if (categoryRepository.existsById(id, username)) {
            Category category = Category.from(id, username, categoryRequest);

            boolean updated = categoryRepository.insertOrUpdateCategory(category);

            return updated ? okFrom(category)
                    : ResponseEntity.badRequest().body(new MessageResponse("Category not updated"));
        }
        return categoryNotFound();
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteCategory(@RequestHeader("Authorization") String headerAuth,
            @PathVariable Integer id) {

        String username = getUsername(headerAuth);
        if (categoryRepository.existsById(id, username)) {
            boolean deleted = categoryRepository.deleteCategory(id, username);

            return deleted ? ResponseEntity.ok(new MessageResponse("Deleted category"))
                    : ResponseEntity.internalServerError().build();
        }
        return categoryNotFound();
    }

    @GetMapping(value = "/{categoryId}/videos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getVideosByCategory(@RequestHeader("Authorization") String headerAuth,
            @PathVariable Integer categoryId, @RequestParam int page) {

        String username = getUsername(headerAuth);
        if (categoryRepository.existsById(categoryId, username)) {

            Pageable pageable = PageRequest.of(page, Video.PAGE_LIMIT);
            Page<Video> videoPage = videoRepository.findVideosByCategory(pageable, categoryId, username);

            return videoPage.isEmpty() ? ResponseEntity.badRequest().body(new MessageResponse("Videos not found"))
                    : ResponseEntity.ok(VideoResponse.fromList(videoPage.toList()));
        }
        return categoryNotFound();
    }
}
