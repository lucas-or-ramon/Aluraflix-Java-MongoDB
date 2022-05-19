package br.com.alura.aluraflix.builder;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class QueryBuilder {

    private final Query query;

    private QueryBuilder() {
        this.query = new Query();
    }

    public static QueryBuilder builder() {
        return new QueryBuilder();
    }

    public QueryBuilder withId(int id) {
        query.addCriteria(Criteria.where("id").is(id));
        return this;
    }

    public QueryBuilder with(Pageable pageable) {
        query.with(pageable);
        return this;
    }

    public QueryBuilder withUsername(String username) {
        query.addCriteria(Criteria.where("username").is(username));
        return this;
    }

    public QueryBuilder withCategory(Integer categoryId) {
        query.addCriteria(Criteria.where("categoryId").is(categoryId));
        return this;
    }

    public QueryBuilder withSearch(String search) {
        var titleCriteria = Criteria.where("title").regex(search, "i");
        var descriptionCriteria = Criteria.where("description").regex(search, "i");

        query.addCriteria(new Criteria().orOperator(titleCriteria, descriptionCriteria));
        return this;
    }

    public Query get() {
        return this.query;
    }
}
