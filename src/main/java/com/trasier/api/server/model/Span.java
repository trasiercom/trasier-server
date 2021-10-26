package com.trasier.api.server.model;

import io.micronaut.core.annotation.NonNull;

import java.util.Map;

public class Span {

    @NonNull
    private String id;

    private String parentId;

    @NonNull
    private String traceId;

    @NonNull
    private String conversationId;

    @NonNull
    private String name;

    @NonNull
    private String status;

    private Long startTimestamp;

    private Long endTimestamp;

    private Long beginProcessingTimestamp;

    private Long finishProcessingTimestamp;

    private Map<String, String> tags;

    private Map<String, String> features;

    private Endpoint incomingEndpoint;

    private ContentType incomingContentType;

    private Map<String, String> incomingHeader;

    private String incomingData;

    private Endpoint outgoingEndpoint;

    private ContentType outgoingContentType;

    private Map<String, String> outgoingHeader;

    private String outgoingData;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @NonNull
    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(@NonNull String traceId) {
        this.traceId = traceId;
    }

    @NonNull
    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(@NonNull String conversationId) {
        this.conversationId = conversationId;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getStatus() {
        return status;
    }

    public void setStatus(@NonNull String status) {
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

    public Map<String, String> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, String> features) {
        this.features = features;
    }

    public Endpoint getIncomingEndpoint() {
        return incomingEndpoint;
    }

    public void setIncomingEndpoint(Endpoint incomingEndpoint) {
        this.incomingEndpoint = incomingEndpoint;
    }

    public ContentType getIncomingContentType() {
        return incomingContentType;
    }

    public void setIncomingContentType(ContentType incomingContentType) {
        this.incomingContentType = incomingContentType;
    }

    public Map<String, String> getIncomingHeader() {
        return incomingHeader;
    }

    public void setIncomingHeader(Map<String, String> incomingHeader) {
        this.incomingHeader = incomingHeader;
    }

    public String getIncomingData() {
        return incomingData;
    }

    public void setIncomingData(String incomingData) {
        this.incomingData = incomingData;
    }

    public Endpoint getOutgoingEndpoint() {
        return outgoingEndpoint;
    }

    public void setOutgoingEndpoint(Endpoint outgoingEndpoint) {
        this.outgoingEndpoint = outgoingEndpoint;
    }

    public ContentType getOutgoingContentType() {
        return outgoingContentType;
    }

    public void setOutgoingContentType(ContentType outgoingContentType) {
        this.outgoingContentType = outgoingContentType;
    }

    public Map<String, String> getOutgoingHeader() {
        return outgoingHeader;
    }

    public void setOutgoingHeader(Map<String, String> outgoingHeader) {
        this.outgoingHeader = outgoingHeader;
    }

    public String getOutgoingData() {
        return outgoingData;
    }

    public void setOutgoingData(String outgoingData) {
        this.outgoingData = outgoingData;
    }

}
