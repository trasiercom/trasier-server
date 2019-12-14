package com.trasier.server.controller;

import com.trasier.api.server.model.Span;
import com.trasier.server.elastic.ElasticService;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;

import java.util.List;

@Controller("/api")
public class WriteController {

    private ElasticService elasticService;

    public WriteController(ElasticService elasticService) {
        this.elasticService = elasticService;
    }

    @Post(uri = "/accounts/{$accountId}/spaces/{$spaceKey}/spans", produces = MediaType.TEXT_PLAIN)
    public void postSpan(@PathVariable("$accountId") String accountId, @PathVariable("$spaceKey") String spaceKey, @Body List<Span> spans) {
        elasticService.writeSpans(accountId, spaceKey, spans);
    }

}
