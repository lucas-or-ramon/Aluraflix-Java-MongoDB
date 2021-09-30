package br.com.alura.aluraflix;


import br.com.alura.aluraflix.controllers.ProtectedVideoController;
import br.com.alura.aluraflix.controllers.PublicVideoController;
import br.com.alura.aluraflix.controllers.request.VideoRequest;
import br.com.alura.aluraflix.models.Video;
import br.com.alura.aluraflix.controllers.Properties;
import br.com.alura.aluraflix.services.NextSequenceService;
import br.com.alura.aluraflix.services.VideoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
public class VideoTest extends Setup{

    @Autowired
    VideoService videoService;

    @Autowired
    NextSequenceService nextSequenceService;

    @Test
    public void deve_retornar_paginas_1_2_e_a_3_vazia_de_todos_os_videos_devido_paginacao() {

        Pageable pageable = PageRequest.of(0, Properties.PAGE_LIMIT);
        Page<Video> videoPage1 = videoService.findVideos(pageable, null);

        pageable = PageRequest.of(1, Properties.PAGE_LIMIT);
        Page<Video> videoPage2 = videoService.findVideos(pageable, null);

        pageable = PageRequest.of(2, Properties.PAGE_LIMIT);
        Page<Video> videoPage3 = videoService.findVideos(pageable, null);

        Assertions.assertEquals(5, videoPage1.get().count());
        Assertions.assertEquals(2, videoPage2.get().count());
        Assertions.assertTrue(videoPage3.isEmpty());
    }

    @Test
    public void deve_retornar_pagina_1_com_video_5_e_pagina_2_sem_videos_devido_pesquisa() {
        Pageable pageable = PageRequest.of(0, Properties.PAGE_LIMIT);
        Page<Video> videoPage1 = videoService.findVideos(pageable, "5");

        pageable = PageRequest.of(1, Properties.PAGE_LIMIT);
        Page<Video> videoPage2 = videoService.findVideos(pageable, "5");

        Assertions.assertEquals(1, videoPage1.get().count());
        Assertions.assertTrue(videoPage2.isEmpty());
    }

    @Test
    public void deve_retornar_video_encontrado_por_id_e_nao_encontrado_por_id() {
        Video video = videoService.findVideoById(1).orElse(new Video());
        Optional<Video> optionalVideo = videoService.findVideoById(8);

        Assertions.assertEquals("Título do Video 1", video.getTitle());
        Assertions.assertTrue(optionalVideo.isEmpty());
    }

    @Test
    public void deve_retornar_true_devido_inserir_video_com_sucesso() {
        Video newVideo = Video.from(new VideoRequest("Título do Video 20", "Descrição do Vídeo 20", "video20.com", 1));
        newVideo.setId(20);

        boolean result = videoService.insertOrUpdateVideo(newVideo);

        Assertions.assertTrue(result);
    }

    @Test
    public void deve_retornar_true_devido_atualizar_video_com_sucesso() {
        Video newVideo = Video.from(new VideoRequest("Título do Video 20 Atualizado", "Descrição do Vídeo 20", "video20.com", 1));
        newVideo.setId(20);

        boolean result = videoService.insertOrUpdateVideo(newVideo);

        Assertions.assertTrue(result);
    }

    @Test
    public void deve_retornar_true_devido_deletar_video_com_sucesso() {
        boolean result = videoService.deleteVideo(20);
        Assertions.assertTrue(result);
    }

    @Test
    public void deve_retornar_true_devido_existir_video_com_id_especifico() {
        boolean result = videoService.existsById(1);
        Assertions.assertTrue(result);
    }


}
