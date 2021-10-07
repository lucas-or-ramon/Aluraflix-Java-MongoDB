package br.com.alura.aluraflix.repository;

import br.com.alura.aluraflix.models.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface VideoRepository {
    Page<Video> findVideosByCategory(Pageable pageable, Integer categoryId);
    Page<Video> findVideos(Pageable pageable, String search);
    Optional<Video> findVideoById(Integer id);
    Boolean insertOrUpdateVideo(Video video);
    Boolean deleteVideo(Integer id);
    Boolean existsById(Integer id);
}
