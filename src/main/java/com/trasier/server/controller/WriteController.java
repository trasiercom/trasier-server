package com.trasier.server.controller;

import com.trasier.api.Span;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;

import java.util.List;

@Controller("/api")
public class WriteController {

    @Post(uri = "/accounts/{$accountId}/spaces/{$spaceKey}/spans", produces = MediaType.TEXT_PLAIN)
    public String postSpan(@PathVariable("$accountId") String accountId, @PathVariable("$spaceKey") String spaceKey, @Body List<Span> spans) {
        return "added " + spans.size();
    }

}
