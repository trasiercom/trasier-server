package com.trasier.api.server.model;

import io.micronaut.core.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversationInfo {
    @NonNull
    private String id;

    @NonNull
    private Long startTimestamp;

    @NonNull
    private Long endTimestamp;

    private Map<String, String> labels = new HashMap<>();

    @NonNull
    private List<TraceInfo> traces = new ArrayList<>();

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

    @NonNull
    public List<TraceInfo> getTraces() {
        return traces;
    }

    public void setTraces(@NonNull List<TraceInfo> traces) {
        this.traces = traces;
    }
}