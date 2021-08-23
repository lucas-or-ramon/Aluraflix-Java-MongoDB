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

import java.util.*;

@Service
public class VideoService {

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    NextSequenceService nextSequenceService;

    public List<Video> findAllVideos() {
        try {
            return videoRepository.findAll();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public Video findVideoById(final Long id) {
        try {
            Video video = videoRepository.findById(id).orElse(null);
            if (Objects.nonNull(video)) {
                return video;
            }
        } catch (Exception e) {
            return new Video();
        }
        return new Video();
    }

    public Boolean insertVideo(Video video) {
        try {
            video.setId(nextSequenceService.getNextSequence(Video.SEQUENCE_NAME));
            videoRepository.insert(video);
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

            videoRepository.save(video);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public Boolean deleteVideo(Long id) {
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(id));

            videoRepository.deleteById(id);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

//    public void dropCollection(String collection) throws Exception {
//        try {
//            //videoRepository.(collection);
//        } catch (Exception e) {
//            throw new Exception("message:", e);
//        }
//    }

    public int test(int valor) {
        return valor;
    }
}
