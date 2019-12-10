package com.trasier.api;

import java.util.Map;

public class Span {
    private String id;
    private String parentId;
    private String traceId;
    private String conversationId;
    private String name;
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

    private Span(Builder builder) {
        this.id = builder.id;
        this.parentId = builder.parentId;
        this.traceId = builder.traceId;
        this.conversationId = builder.conversationId;
        this.name = builder.name;
        this.status = builder.status;
        this.startTimestamp = builder.startTimestamp;
        this.endTimestamp = builder.endTimestamp;
        this.beginProcessingTimestamp = builder.beginProcessingTimestamp;
        this.finishProcessingTimestamp = builder.finishProcessingTimestamp;
        this.tags = builder.tags;
        this.features = builder.features;
        this.incomingEndpoint = builder.incomingEndpoint;
        this.incomingContentType = builder.incomingContentType;
        this.incomingHeader = builder.incomingHeader;
        this.incomingData = builder.incomingData;
        this.outgoingEndpoint = builder.outgoingEndpoint;
        this.outgoingContentType = builder.outgoingContentType;
        this.outgoingHeader = builder.outgoingHeader;
        this.outgoingData = builder.outgoingData;
    }

    public static Builder newSpan() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
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

    private String outgoingData;

    public static final class Builder {
        private String id;
        private String parentId;
        private String traceId;
        private String conversationId;
        private String name;
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

        private Builder() {
        }

        public Span build() {
            return new Span(this);
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder parentId(String parentId) {
            this.parentId = parentId;
            return this;
        }

        public Builder traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public Builder conversationId(String conversationId) {
            this.conversationId = conversationId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder startTimestamp(Long startTimestamp) {
            this.startTimestamp = startTimestamp;
            return this;
        }

        public Builder endTimestamp(Long endTimestamp) {
            this.endTimestamp = endTimestamp;
            return this;
        }

        public Builder beginProcessingTimestamp(Long beginProcessingTimestamp) {
            this.beginProcessingTimestamp = beginProcessingTimestamp;
            return this;
        }

        public Builder finishProcessingTimestamp(Long finishProcessingTimestamp) {
            this.finishProcessingTimestamp = finishProcessingTimestamp;
            return this;
        }

        public Builder tags(Map<String, String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder features(Map<String, String> features) {
            this.features = features;
            return this;
        }

        public Builder incomingEndpoint(Endpoint incomingEndpoint) {
            this.incomingEndpoint = incomingEndpoint;
            return this;
        }

        public Builder incomingContentType(ContentType incomingContentType) {
            this.incomingContentType = incomingContentType;
            return this;
        }

        public Builder incomingHeader(Map<String, String> incomingHeader) {
            this.incomingHeader = incomingHeader;
            return this;
        }

        public Builder incomingData(String incomingData) {
            this.incomingData = incomingData;
            return this;
        }

        public Builder outgoingEndpoint(Endpoint outgoingEndpoint) {
            this.outgoingEndpoint = outgoingEndpoint;
            return this;
        }

        public Builder outgoingContentType(ContentType outgoingContentType) {
            this.outgoingContentType = outgoingContentType;
            return this;
        }

        public Builder outgoingHeader(Map<String, String> outgoingHeader) {
            this.outgoingHeader = outgoingHeader;
            return this;
        }

        public Builder outgoingData(String outgoingData) {
            this.outgoingData = outgoingData;
            return this;
        }
    }
}
