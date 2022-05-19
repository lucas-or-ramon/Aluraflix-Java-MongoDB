package br.com.alura.aluraflix.services;

import br.com.alura.aluraflix.builder.QueryBuilder;
import br.com.alura.aluraflix.controllers.request.VideoRequest;
import br.com.alura.aluraflix.controllers.response.VideoResponse;
import br.com.alura.aluraflix.exception.video.CategoryNotFoundException;
import br.com.alura.aluraflix.exception.video.VideoNotFoundException;
import br.com.alura.aluraflix.models.Category;
import br.com.alura.aluraflix.models.User;
import br.com.alura.aluraflix.models.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class VideoService {

    private final MongoTemplate mongoTemplate;
    private final CategoryRepository categoryRepository;
    private final NextSequenceService nextSequenceService;

    @Autowired
    public VideoService(MongoTemplate mongoTemplate, CategoryRepository categoryRepository, NextSequenceService nextSequenceService) {
        this.mongoTemplate = mongoTemplate;
        this.categoryRepository = categoryRepository;
        this.nextSequenceService = nextSequenceService;
    }

    public List<VideoResponse> findVideos(int pageNumber, String search, String username) {
        Pageable pageable = PageRequest.of(pageNumber, Video.PAGE_LIMIT);

        var query = (Objects.isNull(search))
                ? QueryBuilder.builder().withUsername(username).with(pageable).get()
                : QueryBuilder.builder().withUsername(username).withSearch(search).with(pageable).get();

        var videoPage = getPageVideo(query, pageable);
        if (videoPage.isEmpty()) {
            throw new VideoNotFoundException("Video not found");
        }
        return VideoResponse.fromList(videoPage.toList());
    }

    public List<VideoResponse> findFreeVideos(int pageNumber) {
        var query = QueryBuilder.builder().withCategory(Category.FREE_CATEGORY).get();

        var videoPage = getPageVideo(query, PageRequest.of(pageNumber, User.PAGE_LIMIT));
        if (videoPage.isEmpty()) {
            throw new VideoNotFoundException("Videos not found");
        }
        return VideoResponse.fromList(videoPage.toList());
    }

    public VideoResponse findVideoById(int id, String username) {
        var query = QueryBuilder.builder().withId(id).withUsername(username).get();
        var video = Optional.ofNullable(mongoTemplate.findOne(query, Video.class));

        return VideoResponse.from(video.orElseThrow(() -> new VideoNotFoundException("Videos not found")));
    }

    public VideoResponse insertOrUpdateVideo(Integer id, String username, VideoRequest videoRequest) {
        if (!categoryRepository.existsById(videoRequest.getCategoryId(), username)) {
            throw new CategoryNotFoundException("Category not found");
        }

        var videoId = Objects.isNull(id) ? nextSequenceService.getNextSequence(Video.SEQUENCE_NAME) : id;
        Video video = Video.from(videoId, username, videoRequest);

        return VideoResponse.from(mongoTemplate.save(video));
    }

    public void deleteVideo(Integer id, String username) {
        mongoTemplate.remove(QueryBuilder.builder().withId(id).withUsername(username).get(), Video.class);
    }

    public List<VideoResponse> findVideosByCategory(int pageNumber, int categoryId, String username) {
        var pageable = PageRequest.of(pageNumber, Video.PAGE_LIMIT);
        var query = QueryBuilder.builder().withUsername(username).withCategory(categoryId).with(pageable).get();

        Page<Video> videoPage = getPageVideo(query, pageable);
        if (videoPage.isEmpty()) {
            throw new VideoNotFoundException("Videos not found");
        }
        return VideoResponse.fromList(videoPage.toList());
    }

    private Page<Video> getPageVideo(Query query, Pageable pageable) {
        List<Video> videoList = mongoTemplate.find(query, Video.class);
        long count = mongoTemplate.count(query.skip(-1).limit(-1), Video.class);
        return new PageImpl<>(videoList, pageable, count);
    }
}
