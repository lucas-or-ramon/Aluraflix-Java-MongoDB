package br.com.devcanoa.aluraflix.services;

import br.com.devcanoa.aluraflix.models.CustomSequences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class NextSequenceService {

    @Autowired
    private MongoOperations mongo;

    public Integer getNextSequence(String seqName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(seqName));

        Update update = new Update();
        update.inc("seq", 1);

        CustomSequences counter = mongo.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                CustomSequences.class);

        return !Objects.isNull(counter) ? counter.getSeq() : 1;
    }
}
