package com.trasier.server.elastic;

import com.trasier.api.Endpoint;
import com.trasier.api.Span;
import org.apache.commons.lang3.time.FastDateFormat;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.SearchHit;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class ElasticConverter {
    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private final ElasticDataConverter dataConverter;

    public ElasticConverter(ElasticDataConverter dataConverter) {
        this.dataConverter = dataConverter;
    }

    public static Span convert(SearchHit searchHit) {
        Span.Builder builder = Span.newSpan();

        builder.id(searchHit.getSourceAsMap().get("spanId").toString());
        builder.traceId(searchHit.getSourceAsMap().get("traceId").toString());
        builder.conversationId(searchHit.getSourceAsMap().get("conversationId").toString());
        builder.status(searchHit.getSourceAsMap().get("status").toString());
        builder.name(searchHit.getSourceAsMap().get("name").toString());

        if (searchHit.getSourceAsMap().get("incomingEndpoint.name") != null) {
            builder.incomingEndpoint(Endpoint.newEndpoint().name(searchHit.getSourceAsMap().get("incomingEndpoint.name").toString()).build());
        }
        if (searchHit.getSourceAsMap().get("outgoingEndpoint.name") != null) {
            builder.outgoingEndpoint(Endpoint.newEndpoint().name(searchHit.getSourceAsMap().get("outgoingEndpoint.name").toString()).build());
        }

        if (searchHit.getSourceAsMap().get("parentSpanId") != null) {
            builder.parentId(searchHit.getSourceAsMap().get("parentSpanId").toString());
        }

        if (searchHit.getSourceAsMap().get("startTimestamp") != null) {
            try {
                builder.startTimestamp(DATE_FORMAT.parse(searchHit.getSourceAsMap().get("startTimestamp").toString()).getTime());
            } catch (ParseException e) {
                throw new IllegalStateException(e);
            }
        }

        if (searchHit.getSourceAsMap().get("endTimestamp") != null) {
            try {
                builder.endTimestamp(DATE_FORMAT.parse(searchHit.getSourceAsMap().get("endTimestamp").toString()).getTime());
            } catch (ParseException e) {
                throw new IllegalStateException(e);
            }
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

            Calendar calendar = Calendar.getInstance(ElasticService.TIME_ZONE);
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

            if (span.getIncomingEndpoint() != null) {
                builder.field("incomingEndpoint.name", span.getIncomingEndpoint().getName());
            }

            if (span.getOutgoingEndpoint() != null) {
                builder.field("outgoingEndpoint.name", span.getOutgoingEndpoint().getName());
            }

            if (span.getParentId() != null) {
                builder.field("parentId", span.getParentId());
            }

            if (span.getIncomingData() != null) {
                builder.field("incomingData", dataConverter.removeMarkup(span.getIncomingContentType(), span.getIncomingData()));
            }

            if (span.getOutgoingData() != null) {
                builder.field("outgoingData", dataConverter.removeMarkup(span.getOutgoingContentType(), span.getOutgoingData()));
            }

            return builder.endObject();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
