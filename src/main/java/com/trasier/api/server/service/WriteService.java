package com.trasier.api.server.service;

import com.trasier.api.server.model.Span;

import java.util.List;

public interface WriteService {
    void writeSpans(String accountId, String spaceKey, List<Span> spans);
}