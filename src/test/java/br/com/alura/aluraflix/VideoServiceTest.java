package br.com.alura.aluraflix;

//import static org.junit.jupiter.api.Assertions;

import br.com.alura.aluraflix.domain.Video;
import br.com.alura.aluraflix.repository.VideoRepository;
import br.com.alura.aluraflix.service.VideoService;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.junit.runner.RunWith;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class VideoServiceTest {

    @TestConfiguration
    static class VideoServiceTestConfiguration {
        @Bean
        public VideoService videoService() {
            return new VideoService();
        }
    }

    @MockBean
    private VideoRepository videoRepository;

    @Autowired
    private VideoService videoService;

//    @BeforeAll
//    public void setup() throws IOException {
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        List<VideoRequest> listVideos = objectMapper.readValue(new URL("file:src/test/resources/all_videos.json"), new TypeReference<List<VideoRequest>>() {
//        });
//
//        listVideos.forEach(videoRequest -> mongoTemplate.insert(Video.from(videoRequest)));
//    }

    @Test
    public void deve_retornar_todos_os_videos() {
        Video video = videoService.findVideoById(1L);

        Assertions.assertEquals(1L, video.getId());
    }

    @Before
    public void setup() {
        Video video = new Video(1L, "Titulo", "Descicao", "www.youtube.com");

        Mockito.when(videoRepository.findById(video.getId()).orElse(null))
                .thenReturn(video);
    }
}
