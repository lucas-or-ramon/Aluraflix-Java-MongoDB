package br.com.alura.aluraflix.repository;

import br.com.alura.aluraflix.AluraflixApplication;
import br.com.alura.aluraflix.domain.Video;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class VideoRepositoryTest {

//    @Configuration
//    static class ContextConfiguration {
//        @Bean
//        public VideoRepository videoRepository() {
//            VideoRepository videoRepository;
//            return videoRepository;
//        }
//
//    }

    @Autowired
    VideoRepository videoRepository;

    @Test
    public void testSaveAndFindVideo() {
        Video video = new Video(1L, "Titulo", "Descicao", "www.youtube.com");
        Video video2 = new Video(2L, "Titulo", "Descicao", "www.youtube.com");
        //videoRepository.deleteAll();
        videoRepository.save(video);
        videoRepository.save(video2);


        Optional<Video> optionalVideo = videoRepository.findById(1L);

        //Assertions.assertEquals(video, videoRepository.findById(1L).orElse(null));
    }
}