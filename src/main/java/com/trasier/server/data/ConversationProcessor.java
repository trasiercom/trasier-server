package com.trasier.server.data;

import com.trasier.api.server.model.ConversationInfo;
import com.trasier.api.server.model.Span;
import com.trasier.api.server.model.SpanInfo;
import com.trasier.api.server.model.TraceInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConversationProcessor {
    private ConversationProcessor() {}

    public static List<ConversationInfo> convert(List<Span> spans) {
        if(spans.isEmpty()) {
            return Collections.emptyList();
        }

        return SpanProcessor.groupByConversationId(spans).entrySet().stream()
                .map(c -> ConversationProcessor.convert(c.getKey(), c.getValue()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static ConversationInfo convert(String conversationId, List<Span> conversationSpans) {
        if(conversationSpans.isEmpty()) {
            return null;
        }

        List<TraceInfo> traces = SpanProcessor.groupByTraceId(conversationSpans).entrySet().stream()
                .map(entry -> createTraceInfo(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(TraceInfo::getStartTimestamp))
                .collect(Collectors.toList());

        ConversationInfo conversation = createConversationInfo(conversationId, traces);

        return conversation;
    }

    private static TraceInfo createTraceInfo(String traceId, List<Span> spans) {
        TraceInfo.TraceInfoBuilder traceInfoBuilder = TraceInfo.builder();
        traceInfoBuilder.id(traceId);
        //TODO Extract Labels
        traceInfoBuilder.startTimestamp(getStartTimestamp(spans));
        traceInfoBuilder.endTimestamp(getEndTimestamp(spans));

        List<SpanInfo> parentSpans = spans.stream()
                .filter(span -> span.getParentId() == null)
                .map(ConversationProcessor::convert)
                .collect(Collectors.toList());

        List<Span> childSpans = spans.stream()
                .filter(span -> span.getParentId() != null)
                .collect(Collectors.toList());

        createRecursiveSpans(parentSpans, childSpans);

        traceInfoBuilder.spans(parentSpans);
        return traceInfoBuilder.build();
    }

    private static Long getStartTimestamp(List<Span> spans) {
        return spans.stream()
                .map(Span::getStartTimestamp)
                .min(Comparator.naturalOrder())
                .get();
    }

    private static Long getEndTimestamp(List<Span> spans) {
        return spans.stream()
                .map(span -> span.getEndTimestamp() != null ? span.getEndTimestamp() : span.getStartTimestamp())
                .max(Comparator.naturalOrder())
                .get();
    }

    private static void createRecursiveSpans(List<SpanInfo> parentSpans, List<Span> childSpans) {
        if(parentSpans != null && parentSpans.size() > 0 && childSpans.size() > 0) {
            parentSpans.forEach(parentSpan -> {
                List<SpanInfo> children = new ArrayList<>();
                for (Iterator<Span> iterator = childSpans.iterator(); iterator.hasNext(); ) {
                    Span childSpan = iterator.next();
                    if (childSpan.getParentId().equals(parentSpan.getId())) {
                        children.add(ConversationProcessor.convert(childSpan));
                        iterator.remove();
                    }
                }
                parentSpan.setChildren(children);
            });
            parentSpans.forEach(parentSpan -> {
                createRecursiveSpans(parentSpan.getChildren(), childSpans);
            });
        }
    }

    private static ConversationInfo createConversationInfo(String conversationId, List<TraceInfo> traces) {
        ConversationInfo.ConversationInfoBuilder builder = ConversationInfo.builder();
        builder.id(conversationId);
        builder.traces(traces);
        builder.startTimestamp(traces.get(0).getStartTimestamp());
        builder.endTimestamp(traces.get(traces.size() - 1).getEndTimestamp());
        return builder.build();
    }

    private static SpanInfo convert(Span span) {
        SpanInfo.SpanInfoBuilder builder = SpanInfo.builder();
        builder.id(span.getId());
        builder.status(span.getStatus());
        builder.name(span.getName());
        builder.startTimestamp(span.getStartTimestamp());
        builder.endTimestamp(span.getEndTimestamp());
        builder.beginProcessingTimestamp(span.getBeginProcessingTimestamp());
        builder.finishProcessingTimestamp(span.getFinishProcessingTimestamp());
        builder.tags(span.getTags());
        builder.incomingEndpoint(span.getIncomingEndpoint());
        builder.incomingHeader(span.getIncomingHeader());
        builder.outgoingEndpoint(span.getOutgoingEndpoint());
        builder.outgoingHeader(span.getOutgoingHeader());
        return builder.build();
    }
}