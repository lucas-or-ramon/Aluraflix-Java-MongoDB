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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/api/categories")
@PreAuthorize("hasRole('USER') or hasRole('MODERATOR')")
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

        String username = jwtUtils.getUserNameFromJwtToken(headerAuth.substring(7));

        Pageable pageable = PageRequest.of(page, Category.PAGE_LIMIT);
        Page<Category> categoryPage = categoryRepository.findCategories(pageable, username);

        if (categoryPage.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Categories not found"));
        }
        return ResponseEntity.ok(CategoryResponse.fromList(categoryPage.toList()));
    }

    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getCategoryById(@RequestHeader("Authorization") String headerAuth,
                                             @PathVariable Integer id) {
        String username = jwtUtils.getUserNameFromJwtToken(headerAuth.substring(7));
        Optional<Category> optionalCategory = categoryRepository.findCategoryById(id, username);

        if (optionalCategory.isPresent()) {
            return ResponseEntity.ok(CategoryResponse.from(optionalCategory.get()));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Category not found"));
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> insertCategory(@RequestHeader("Authorization") String headerAuth,
                                            @Valid @RequestBody final CategoryRequest categoryRequest) {
        String username = jwtUtils.getUserNameFromJwtToken(headerAuth.substring(7));

        Category category = Category.from(categoryRequest);
        category.setId(nextSequenceService.getNextSequence(Category.SEQUENCE_NAME));
        category.setUser(username);

        if (categoryRepository.insertOrUpdateCategory(category)) {
            return ResponseEntity.ok(CategoryResponse.from(category));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Category not inserted"));
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> updateCategory(@RequestHeader("Authorization") String headerAuth,
                                            @PathVariable Integer id,
                                            @Valid @RequestBody final CategoryRequest categoryRequest) {

        String username = jwtUtils.getUserNameFromJwtToken(headerAuth.substring(7));

        if (categoryRepository.existsById(id, username)) {
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
    public ResponseEntity<?> deleteCategory(@RequestHeader("Authorization") String headerAuth,
                                            @PathVariable Integer id) {

        String username = jwtUtils.getUserNameFromJwtToken(headerAuth.substring(7));

        if (categoryRepository.existsById(id, username)) {
            if (categoryRepository.deleteCategory(id, username)) {
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
    public ResponseEntity<?> getVideosByCategory(@RequestHeader("Authorization") String headerAuth,
                                                 @PathVariable Integer categoryId, @RequestParam int page) {

        String username = jwtUtils.getUserNameFromJwtToken(headerAuth.substring(7));

        if (categoryRepository.existsById(categoryId, username)) {
            Pageable pageable = PageRequest.of(page, Video.PAGE_LIMIT);

            Page<Video> videoPage = videoRepository.findVideosByCategory(pageable, categoryId, username);

            if (videoPage.isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Videos not found"));
            }
            return ResponseEntity.ok(VideoResponse.fromList(videoPage.toList()));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Category not found"));
        }
    }
}
