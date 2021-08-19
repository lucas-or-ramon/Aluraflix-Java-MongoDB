package br.com.alura.aluraflix.service;

import br.com.alura.aluraflix.domain.Video;
import br.com.alura.aluraflix.repository.VideoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class VideoService {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    NextSequenceService nextSequenceService;

    public List<Video> findAllVideos() {
        try {
            return mongoTemplate.findAll(Video.class);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public Video findVideoById(final Long id) {
        try {
            return mongoTemplate.findById(id, Video.class);
        } catch (Exception e) {
            return new Video();
        }
    }

    public Boolean insertVideo(Video video) {
        try {
            video.setId(nextSequenceService.getNextSequence(Video.SEQUENCE_NAME));
            mongoTemplate.insert(video);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean updateVideo(Video video, final Video videoDetails) {
        try {
            video.setTitle(videoDetails.getTitle());
            video.setDescription(videoDetails.getDescription());
            video.setLink(videoDetails.getLink());

            mongoTemplate.save(video);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public Boolean deleteVideo(Long id) {
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(id));

            mongoTemplate.remove(query, Video.class);
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
