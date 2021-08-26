package br.com.alura.aluraflix;

import br.com.alura.aluraflix.controller.VideoController;
import br.com.alura.aluraflix.domain.Video;
import br.com.alura.aluraflix.service.NextSequenceService;
import br.com.alura.aluraflix.service.VideoService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.net.URL;
import java.util.List;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WebMvcTest(VideoController.class)
public class VideoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VideoService videoService;

    @MockBean
    private NextSequenceService nextSequenceService;

    private static List<Video> listVideos;

    @BeforeAll
    static void setup() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        listVideos = objectMapper.readValue(new URL("file:src/test/resources/all_videos.json"), new TypeReference<List<Video>>() {});
    }

    @Test
    public void deve_retornar_todos_os_videos() throws Exception {
        Mockito.when(videoService.findAllVideos()).thenReturn(listVideos);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/videos"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(15)))
                .andExpect(MockMvcResultMatchers.jsonPath("[0].link").value("https://www.youtube.com/watch?v=6IuQUgeDPg0"))
                .andExpect(MockMvcResultMatchers.jsonPath("[14].link").value("https://www.youtube.com/watch?v=kryIBKPVZ7A"));
    }

    @Test
    public void deve_retornar_video_4() throws Exception {
        Mockito.when(videoService.findVideoById(4L)).thenReturn(listVideos.get(3));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/videos/4"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("O que é REST?"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Nesse video o instrutor Giovanni Tempobono explica o que é REST, da onde veio e para que serve!"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.link").value("https://www.youtube.com/watch?v=weQ8ssA6iBU"));
    }

    @Test
    public void deve_retornar_200_para_video_inserido() throws Exception {
        Mockito.when(videoService.insertVideo(listVideos.get(14))).thenReturn(true);
        Mockito.when(nextSequenceService.getNextSequence("test")).thenReturn(10L);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(listVideos.get(14));

        this.mockMvc.perform(MockMvcRequestBuilders.post("/videos").contentType(MediaType.APPLICATION_JSON_VALUE).content(json).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
                //.andExpect(MockMvcResultMatchers.jsonPath("$.title").value("O que é Deep Learning?"))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Afinal, o que é Deep Learning e onde se aplica? O Gui Silveira explica neste vídeo além de outros conceitos que rodeiam este tema como as redes neurais densas, estimadores, classificadores e mais."))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.link").value("https://www.youtube.com/watch?v=kryIBKPVZ7A"));
    }

//    @Test
//    public void greetingShouldReturnMessageFromService() throws Exception {
//        Mockito.when(service.greet()).thenReturn("Hello, Mock");
//
//        this.mockMvc.perform(MockMvcRequestBuilders.get("/videos"))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Hello, Mock")));
//    }

//    @LocalServerPort
//    private int port;

//    @Autowired
//    private VideoController videoController;

//    @Autowired
//    private TestRestTemplate testRestTemplate;

//    @Test
//    public void contextLoads() throws Exception {
//        Assertions.assertThat(videoController).isNotNull();
//    }

//    @Test
//    public void greetingShouldReturnDefaultMessage() throws Exception {
//        String hello = this.testRestTemplate.getForObject("http://localhost:" + port + "/", String.class);
//        Assertions.assertEquals("Hello, World", hello);
//    }

}
