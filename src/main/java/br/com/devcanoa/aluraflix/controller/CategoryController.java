package br.com.devcanoa.aluraflix.controller;

import br.com.devcanoa.aluraflix.controller.request.CategoryRequest;
import br.com.devcanoa.aluraflix.controller.response.CategoryResponse;
import br.com.devcanoa.aluraflix.security.jwt.JwtUtils;
import br.com.devcanoa.aluraflix.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(value = "/api/categories")
@PreAuthorize("hasRole('USER')")
public class CategoryController {

    private final JwtUtils jwtUtils;
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(JwtUtils jwtUtils, CategoryService categoryService) {
        this.jwtUtils = jwtUtils;
        this.categoryService = categoryService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CategoryResponse>> getCategories(@RequestParam int pageNumber, @RequestHeader("Authorization") String headerAuth) {

        return ResponseEntity.ok(categoryService.findCategories(pageNumber, getUsername(headerAuth)));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable int id, @RequestHeader("Authorization") String headerAuth) {

        return ResponseEntity.ok(categoryService.findCategoryById(id, getUsername(headerAuth)));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoryResponse> insertCategory(@RequestHeader("Authorization") String headerAuth,
                                                           @Valid @RequestBody final CategoryRequest categoryRequest) {

       return ResponseEntity.ok(categoryService.insertOrUpdateCategory(null, getUsername(headerAuth), categoryRequest));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable int id,
                                            @RequestHeader("Authorization") String headerAuth,
                                            @Valid @RequestBody CategoryRequest categoryRequest) {

        return ResponseEntity.ok(categoryService.insertOrUpdateCategory(id, getUsername(headerAuth), categoryRequest));
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> deleteCategory(@PathVariable int id,
                                                     @RequestHeader("Authorization") String headerAuth) {

        categoryService.deleteCategory(id, getUsername(headerAuth));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private String getUsername(String headerAuth) {
        return jwtUtils.getUserNameFromJwtToken(headerAuth.substring(7));
    }
}
