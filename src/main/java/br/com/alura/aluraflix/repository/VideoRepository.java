package br.com.alura.aluraflix.repository;

import br.com.alura.aluraflix.models.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface VideoRepository {
    Page<Video> findVideosByCategory(Pageable pageable, Integer categoryId, String username);

    Page<Video> findVideos(Pageable pageable, String search, String username);

    Page<Video> findFreeVideos(Pageable pageable, Integer freeCategory);

    Optional<Video> findVideoById(Integer id, String username);

    boolean insertOrUpdateVideo(Video video);

    boolean deleteVideo(Integer id, String username);

    boolean existsById(Integer id, String username);
}
