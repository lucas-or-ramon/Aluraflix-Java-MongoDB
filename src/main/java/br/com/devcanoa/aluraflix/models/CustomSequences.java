package br.com.devcanoa.aluraflix.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "customSequences")
public class CustomSequences {

    @Id
    private String id;
    private Integer seq;

    public CustomSequences(String id, Integer seq) {
        this.id = id;
        this.seq = seq;
    }
}
