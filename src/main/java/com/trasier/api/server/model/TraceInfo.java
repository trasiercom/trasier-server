package com.trasier.api.server.model;

import io.micronaut.core.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TraceInfo {
    @NonNull
    private String id;

    @NonNull
    private Long startTimestamp;

    @NonNull
    private Long endTimestamp;

    private Map<String, String> labels = new HashMap<>();

    private List<SpanInfo> spans = new ArrayList<>();

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public Long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(@NonNull Long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    @NonNull
    public Long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(@NonNull Long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    public List<SpanInfo> getSpans() {
        return spans;
    }

    public void setSpans(List<SpanInfo> spans) {
        this.spans = spans;
    }
}