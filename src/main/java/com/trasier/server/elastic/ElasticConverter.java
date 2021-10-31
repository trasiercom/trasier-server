package com.trasier.server.elastic;

import com.trasier.api.server.model.Endpoint;
import com.trasier.api.server.model.Span;
import com.trasier.server.data.SnappyUtils;
import jakarta.inject.Singleton;
import org.apache.commons.lang3.time.FastDateFormat;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.SearchHit;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@Singleton
public class ElasticConverter {
    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static Span convert(SearchHit searchHit) {
        Span.SpanBuilder builder = Span.builder();

        builder.id(searchHit.getSourceAsMap().get("spanId").toString());
        builder.traceId(searchHit.getSourceAsMap().get("traceId").toString());
        builder.conversationId(searchHit.getSourceAsMap().get("conversationId").toString());
        builder.status(searchHit.getSourceAsMap().get("status").toString());
        builder.name(searchHit.getSourceAsMap().get("name").toString());

        if (searchHit.getSourceAsMap().get("incomingEndpoint.name") != null) {
            Endpoint.EndpointBuilder endpointBuilder = Endpoint.builder().name(searchHit.getSourceAsMap().get("incomingEndpoint.name").toString());
            if (searchHit.getSourceAsMap().get("incomingEndpoint.hostname") != null) {
                endpointBuilder.hostname(searchHit.getSourceAsMap().get("incomingEndpoint.hostname").toString());
            }
            if (searchHit.getSourceAsMap().get("incomingEndpoint.ipAddress") != null) {
                endpointBuilder.ipAddress(searchHit.getSourceAsMap().get("incomingEndpoint.ipAddress").toString());
            }
            if (searchHit.getSourceAsMap().get("incomingEndpoint.port") != null) {
                endpointBuilder.port(searchHit.getSourceAsMap().get("incomingEndpoint.port").toString());
            }
            builder.incomingEndpoint(endpointBuilder.build());
        }
        if (searchHit.getSourceAsMap().get("outgoingEndpoint.name") != null) {
            Endpoint.EndpointBuilder endpointBuilder = Endpoint.builder().name(searchHit.getSourceAsMap().get("outgoingEndpoint.name").toString());
            if (searchHit.getSourceAsMap().get("outgoingEndpoint.hostname") != null) {
                endpointBuilder.hostname(searchHit.getSourceAsMap().get("outgoingEndpoint.hostname").toString());
            }
            if (searchHit.getSourceAsMap().get("outgoingEndpoint.ipAddress") != null) {
                endpointBuilder.ipAddress(searchHit.getSourceAsMap().get("outgoingEndpoint.ipAddress").toString());
            }
            if (searchHit.getSourceAsMap().get("outgoingEndpoint.port") != null) {
                endpointBuilder.port(searchHit.getSourceAsMap().get("outgoingEndpoint.port").toString());
            }
            builder.outgoingEndpoint(endpointBuilder.build());
        }

        if (searchHit.getSourceAsMap().get("parentSpanId") != null) {
            builder.parentId(searchHit.getSourceAsMap().get("parentSpanId").toString());
        }

        if(searchHit.getSourceAsMap().get("startTimestamp") != null) {
            try {
                builder.startTimestamp(DATE_FORMAT.parse(searchHit.getSourceAsMap().get("startTimestamp").toString()).getTime());
            } catch (ParseException e) {
                throw new IllegalStateException(e);
            }
        }

        if(searchHit.getSourceAsMap().get("endTimestamp") != null) {
            try {
                builder.endTimestamp(DATE_FORMAT.parse(searchHit.getSourceAsMap().get("endTimestamp").toString()).getTime());
            } catch (ParseException e) {
                throw new IllegalStateException(e);
            }
        }

        if (searchHit.getSourceAsMap().get("incomingData") != null) {
            builder.incomingData(searchHit.getSourceAsMap().get("incomingData").toString());
        }

        if (searchHit.getSourceAsMap().get("outgoingData") != null) {
            builder.outgoingData(searchHit.getSourceAsMap().get("outgoingData").toString());
        }

        return builder.build();
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
                builder.field("incomingData", SnappyUtils.decodeUncompressData(span.getIncomingData()));
            }

            if (span.getOutgoingData() != null) {
                builder.field("outgoingData", SnappyUtils.decodeUncompressData(span.getOutgoingData()));
            }

            return builder.endObject();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
