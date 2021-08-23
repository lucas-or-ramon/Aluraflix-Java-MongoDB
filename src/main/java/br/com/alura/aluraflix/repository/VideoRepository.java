package br.com.alura.aluraflix.repository;

import br.com.alura.aluraflix.domain.Video;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

import javax.validation.constraints.NotNull;
import java.util.Optional;

public interface VideoRepository extends MongoRepository<Video, Long> {
}
