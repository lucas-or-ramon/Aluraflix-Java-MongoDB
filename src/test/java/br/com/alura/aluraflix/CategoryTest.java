package br.com.alura.aluraflix;

import br.com.alura.aluraflix.controllers.Properties;
import br.com.alura.aluraflix.controllers.request.CategoryRequest;
import br.com.alura.aluraflix.models.Category;
import br.com.alura.aluraflix.models.Video;
import br.com.alura.aluraflix.services.CategoryService;
import br.com.alura.aluraflix.services.VideoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
public class CategoryTest extends Setup {

    @Autowired
    CategoryService categoryService;

    @Autowired
    VideoService videoService;

    @Test
    public void deve_retornar_paginas_1_2_de_todas_as_categorias_devido_paginacao() {

        Pageable pageable = PageRequest.of(0, Properties.PAGE_LIMIT);
        Page<Category> categoryPage1 = categoryService.findCategories(pageable);

        pageable = PageRequest.of(1, Properties.PAGE_LIMIT);
        Page<Category> categoryPage2 = categoryService.findCategories(pageable);

        Assertions.assertEquals(5, categoryPage1.get().count());
        Assertions.assertEquals(1, categoryPage2.get().count());
    }

    @Test
    public void deve_retornar_categoria_encontrada_por_id_e_nao_encontrada_por_id() {
        Category category = categoryService.findCategoryById(1).orElse(new Category());
        Optional<Category> categoryOptional = categoryService.findCategoryById(100);

        Assertions.assertEquals("LIVRE", category.getTitle());
        Assertions.assertTrue(categoryOptional.isEmpty());
    }

    @Test
    public void deve_retornar_true_devido_inserir_categoria_com_sucesso() {
        Category category = Category.from(new CategoryRequest("PYTHON", "Amarela"));
        category.setId(20);

        boolean result = categoryService.insertOrUpdateCategory(category);

        Assertions.assertTrue(result);
    }

    @Test
    public void deve_retornar_true_devido_atualizar_categoria_com_sucesso() {
        Category category = Category.from(new CategoryRequest("PYTHON", "Vermelha"));
        category.setId(20);

        boolean result = categoryService.insertOrUpdateCategory(category);

        Assertions.assertTrue(result);
    }

    @Test
    public void deve_retornar_true_devido_deletar_categoria_com_sucesso() {
        boolean result = categoryService.deleteCategory(20);
        Assertions.assertTrue(result);
    }

    @Test
    public void deve_retornar_true_devido_existir_categoria() {
        boolean result = categoryService.existsById(1);
        Assertions.assertTrue(result);
    }

    @Test
    public void deve_retornar_pagina_1_com_videos_e_pagina_2_sem_videos_devido_pesquisa_por_id() {
        Pageable pageable = PageRequest.of(0, Properties.PAGE_LIMIT);
        Page<Video> videoPage1 = videoService.findVideosByCategory(pageable, 2);

        pageable = PageRequest.of(1, Properties.PAGE_LIMIT);
        Page<Video> videoPage2 = videoService.findVideosByCategory(pageable, 2);

        Assertions.assertEquals(2, videoPage1.get().count());
        Assertions.assertTrue(videoPage2.isEmpty());
    }
}
