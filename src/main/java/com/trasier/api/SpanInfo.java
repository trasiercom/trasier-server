package com.trasier.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpanInfo {
    private String id;
    private List<SpanInfo> children = new ArrayList<>();
    private String name;
    private String status;
    private Long startTimestamp;
    private Long endTimestamp;
    private Long beginProcessingTimestamp;
    private Long finishProcessingTimestamp;
    private Map<String, String> tags;
    private Endpoint incomingEndpoint;
    private Map<String, String> incomingHeader;
    private Endpoint outgoingEndpoint;
    private Map<String, String> outgoingHeader;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<SpanInfo> getChildren() {
        return children;
    }

    public void setChildren(List<SpanInfo> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Long getBeginProcessingTimestamp() {
        return beginProcessingTimestamp;
    }

    public void setBeginProcessingTimestamp(Long beginProcessingTimestamp) {
        this.beginProcessingTimestamp = beginProcessingTimestamp;
    }

    public Long getFinishProcessingTimestamp() {
        return finishProcessingTimestamp;
    }

    public void setFinishProcessingTimestamp(Long finishProcessingTimestamp) {
        this.finishProcessingTimestamp = finishProcessingTimestamp;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public Endpoint getIncomingEndpoint() {
        return incomingEndpoint;
    }

    public void setIncomingEndpoint(Endpoint incomingEndpoint) {
        this.incomingEndpoint = incomingEndpoint;
    }

    public Map<String, String> getIncomingHeader() {
        return incomingHeader;
    }

    public void setIncomingHeader(Map<String, String> incomingHeader) {
        this.incomingHeader = incomingHeader;
    }

    public Endpoint getOutgoingEndpoint() {
        return outgoingEndpoint;
    }

    public void setOutgoingEndpoint(Endpoint outgoingEndpoint) {
        this.outgoingEndpoint = outgoingEndpoint;
    }

    public Map<String, String> getOutgoingHeader() {
        return outgoingHeader;
    }

    public void setOutgoingHeader(Map<String, String> outgoingHeader) {
        this.outgoingHeader = outgoingHeader;
    }
}
