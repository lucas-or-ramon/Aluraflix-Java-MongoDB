package br.com.alura.aluraflix.services;

import br.com.alura.aluraflix.models.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface VideoService {
    Page<Video> findVideosByCategory(Pageable pageable, Integer categoryId);
    Page<Video> findVideos(Pageable pageable, String search);
    Optional<Video> findVideoById(Integer id);
    Boolean insertOrUpdateVideo(Video video);
    Boolean deleteVideo(Integer id);
    Boolean existsById(Integer id);
}
