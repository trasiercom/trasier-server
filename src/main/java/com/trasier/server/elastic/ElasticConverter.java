package com.trasier.server.elastic;

import com.trasier.api.server.model.Endpoint;
import com.trasier.api.server.model.Span;
import jakarta.inject.Singleton;
import org.apache.commons.lang3.time.FastDateFormat;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.SearchHit;

import java.io.IOException;
import java.text.ParseException;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@Singleton
public class ElasticConverter {
    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static Span convert(SearchHit searchHit) {
        Span span = new Span();

        span.setId(searchHit.getSourceAsMap().get("spanId").toString());
        span.setTraceId(searchHit.getSourceAsMap().get("traceId").toString());
        span.setConversationId(searchHit.getSourceAsMap().get("conversationId").toString());
        span.setStatus(searchHit.getSourceAsMap().get("status").toString());
        span.setName(searchHit.getSourceAsMap().get("name").toString());

        if (searchHit.getSourceAsMap().get("incomingEndpoint.name") != null) {
            Endpoint endpoint = new Endpoint();
            endpoint.setName(searchHit.getSourceAsMap().get("incomingEndpoint.name").toString());
            if (searchHit.getSourceAsMap().get("incomingEndpoint.hostname") != null) {
                endpoint.setHostname(searchHit.getSourceAsMap().get("incomingEndpoint.hostname").toString());
            }
            if (searchHit.getSourceAsMap().get("incomingEndpoint.ipAddress") != null) {
                endpoint.setIpAddress(searchHit.getSourceAsMap().get("incomingEndpoint.ipAddress").toString());
            }
            if (searchHit.getSourceAsMap().get("incomingEndpoint.port") != null) {
                endpoint.setPort(searchHit.getSourceAsMap().get("incomingEndpoint.port").toString());
            }
            span.setIncomingEndpoint(endpoint);
        }
        if (searchHit.getSourceAsMap().get("outgoingEndpoint.name") != null) {
            Endpoint endpoint = new Endpoint();
            endpoint.setName(searchHit.getSourceAsMap().get("outgoingEndpoint.name").toString());
            if (searchHit.getSourceAsMap().get("outgoingEndpoint.hostname") != null) {
                endpoint.setHostname(searchHit.getSourceAsMap().get("outgoingEndpoint.hostname").toString());
            }
            if (searchHit.getSourceAsMap().get("outgoingEndpoint.ipAddress") != null) {
                endpoint.setIpAddress(searchHit.getSourceAsMap().get("outgoingEndpoint.ipAddress").toString());
            }
            if (searchHit.getSourceAsMap().get("outgoingEndpoint.port") != null) {
                endpoint.setPort(searchHit.getSourceAsMap().get("outgoingEndpoint.port").toString());
            }
            span.setOutgoingEndpoint(endpoint);
        }

        if (searchHit.getSourceAsMap().get("parentSpanId") != null) {
            span.setParentId(searchHit.getSourceAsMap().get("parentSpanId").toString());
        }

        if(searchHit.getSourceAsMap().get("startTimestamp") != null) {
            try {
                span.setStartTimestamp(DATE_FORMAT.parse(searchHit.getSourceAsMap().get("startTimestamp").toString()).getTime());
            } catch (ParseException e) {
                throw new IllegalStateException(e);
            }
        }

        if(searchHit.getSourceAsMap().get("endTimestamp") != null) {
            try {
                span.setEndTimestamp(DATE_FORMAT.parse(searchHit.getSourceAsMap().get("endTimestamp").toString()).getTime());
            } catch (ParseException e) {
                throw new IllegalStateException(e);
            }
        }

        if (searchHit.getSourceAsMap().get("incomingData") != null) {
            span.setIncomingData(searchHit.getSourceAsMap().get("incomingData").toString());
        }

        if (searchHit.getSourceAsMap().get("outgoingData") != null) {
            span.setOutgoingData(searchHit.getSourceAsMap().get("outgoingData").toString());
        }

        return span;
    }

    public XContentBuilder convert(String accountId, String spaceKey, Span span) throws IllegalStateException {
        try {
            XContentBuilder builder = jsonBuilder()
                    .startObject()
                    .field("accountId", accountId)
                    .field("spaceKey", spaceKey)
                    .field("spanId", span.getId())
                    .field("traceId", span.getTraceId())
                    .field("conversationId", span.getConversationId())
                    .field("status", span.getStatus())
                    .field("name", span.getName());

            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            if (span.getStartTimestamp() != null) {
                calendar.setTimeInMillis(span.getStartTimestamp());
            } else if (span.getBeginProcessingTimestamp() != null) {
                calendar.setTimeInMillis(span.getBeginProcessingTimestamp());
            } else if (span.getFinishProcessingTimestamp() != null) {
                calendar.setTimeInMillis(span.getFinishProcessingTimestamp());
            } else if (span.getEndTimestamp() != null) {
                calendar.setTimeInMillis(span.getEndTimestamp());
            } else {
                calendar.setTimeInMillis(System.currentTimeMillis());
            }
            builder.field("startTimestamp", calendar.getTime());
            builder.field("beginProcessingTimestamp", span.getBeginProcessingTimestamp() != null ? new Date(span.getBeginProcessingTimestamp()) : null);
            builder.field("finishProcessingTimestamp", span.getFinishProcessingTimestamp() != null ? new Date(span.getFinishProcessingTimestamp()) : null);
            builder.field("endTimestamp", span.getEndTimestamp() != null ? new Date(span.getEndTimestamp()) : null);

            if (span.getIncomingEndpoint() != null) {
                builder.field("incomingEndpoint.name", span.getIncomingEndpoint().getName());
                builder.field("incomingEndpoint.hostname", span.getIncomingEndpoint().getHostname());
                builder.field("incomingEndpoint.ipAddress", span.getIncomingEndpoint().getIpAddress());
                builder.field("incomingEndpoint.port", span.getIncomingEndpoint().getPort());
            }

            if (span.getOutgoingEndpoint() != null) {
                builder.field("outgoingEndpoint.name", span.getOutgoingEndpoint().getName());
                builder.field("outgoingEndpoint.hostname", span.getOutgoingEndpoint().getHostname());
                builder.field("outgoingEndpoint.ipAddress", span.getOutgoingEndpoint().getIpAddress());
                builder.field("outgoingEndpoint.port", span.getOutgoingEndpoint().getPort());
            }

            if (span.getParentId() != null) {
                builder.field("parentId", span.getParentId());
            }

            if (span.getIncomingData() != null) {
                builder.field("incomingData", new String(Base64.getDecoder().decode(span.getIncomingData())));
            }

            if (span.getOutgoingData() != null) {
                builder.field("outgoingData", new String(Base64.getDecoder().decode(span.getOutgoingData())));
            }

            return builder.endObject();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
