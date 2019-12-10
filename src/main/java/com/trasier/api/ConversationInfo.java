package com.trasier.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversationInfo {
    private String id;
    private Long startTimestamp;
    private Long endTimestamp;
    private Map<String, String> labels = new HashMap<>();
    private List<TraceInfo> traces = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public Long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(Long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    public List<TraceInfo> getTraces() {
        return traces;
    }

    public void setTraces(List<TraceInfo> traces) {
        this.traces = traces;
    }
}