package com.trasier.server.controller;

import com.trasier.api.server.model.ConversationInfo;
import com.trasier.api.server.model.Span;
import com.trasier.api.server.service.ReadService;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.QueryValue;

import java.util.List;

@Controller("/api")
public class ReadController {

    private ReadService readService;

    public ReadController(ReadService readService) {
        this.readService = readService;
    }

    @Get(uri = "/accounts/{accountId}/spaces/{spaceKey}/conversations/{conversationId}/traces/{traceId}/spans/{spanId}", produces = MediaType.APPLICATION_JSON)
    public Span findBySpanId(
            @PathVariable("accountId") String accountId,
            @PathVariable("spaceKey") String spaceKey,
            @PathVariable("conversationId") String conversationId,
            @PathVariable("traceId") String traceId,
            @PathVariable("spanId") String spanId) {
        return readService.readSpanById(accountId, spaceKey, conversationId, traceId, spanId);
    }

    @Get(uri = "/accounts/{accountId}/spaces/{spaceKey}/conversations/{conversationId}", produces = MediaType.APPLICATION_JSON)
    public ConversationInfo findConversationById(
            @PathVariable("accountId") String accountId,
            @PathVariable("spaceKey") String spaceKey,
            @PathVariable("conversationId") String conversationId) {
        return readService.readConversationById(accountId, spaceKey, conversationId);
    }

    @Get(uri = "/accounts/{accountId}/spaces/{spaceKey}/conversations", produces = MediaType.APPLICATION_JSON)
    public List<ConversationInfo> findByQuery(
            @PathVariable("accountId") String accountId,
            @PathVariable("spaceKey") String spaceKey,
            @QueryValue("query") String query,
            @QueryValue("from") Long from,
            @QueryValue("to") Long to) {
        return readService.findByQuery(accountId, spaceKey, query, from, to);
    }

}