package br.com.alura.aluraflix;

import br.com.alura.aluraflix.models.Category;
import br.com.alura.aluraflix.models.CustomSequences;
import br.com.alura.aluraflix.models.Video;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Setup {

    @Autowired
    MongoTemplate mongoTemplate;

    @BeforeAll
    public void setup() {
        mongoTemplate.insert(new Video(1, 1, "Título do Video 1", "Descrição do Vídeo 1", "video1.com"));
        mongoTemplate.insert(new Video(2, 1, "Título do Video 2", "Descrição do Vídeo 2", "video2.com"));
        mongoTemplate.insert(new Video(3, 1, "Título do Video 3", "Descrição do Vídeo 3", "video3.com"));
        mongoTemplate.insert(new Video(4, 2, "Título do Video 4", "Descrição do Vídeo 4", "video4.com"));
        mongoTemplate.insert(new Video(5, 2, "Título do Video 5", "Descrição do Vídeo 5", "video5.com"));
        mongoTemplate.insert(new Video(6, 3, "Título do Video 6", "Descrição do Vídeo 6", "video6.com"));
        mongoTemplate.insert(new Video(7, 3, "Título do Video 7", "Descrição do Vídeo 7", "video7.com"));
        mongoTemplate.insert(new Category(1, "LIVRE", "Branco"));
        mongoTemplate.insert(new Category(2, "JAVA", "Azul"));
        mongoTemplate.insert(new Category(3, "SPRING", "Rosa"));
        mongoTemplate.insert(new Category(4, "C++", "Preto"));
        mongoTemplate.insert(new Category(5, "MICROSERVIÇOS", "Verde"));
        mongoTemplate.insert(new Category(6, "COBOL", "Roxo"));
        mongoTemplate.insert(new CustomSequences("videoSequences", 7));
        mongoTemplate.insert(new CustomSequences("categorySequence", 3));

        System.setProperty("free_category", "1");
        System.setProperty("page_limit", "5");
    }
}
