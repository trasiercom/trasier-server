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
        TraceInfo info = new TraceInfo();
        info.setId(traceId);
        //TODO Extract Labels
        info.setStartTimestamp(getStartTimestamp(spans));
        info.setEndTimestamp(getEndTimestamp(spans));

        List<SpanInfo> parentSpans = spans.stream()
                .filter(span -> span.getParentId() == null)
                .map(ConversationProcessor::convert)
                .collect(Collectors.toList());

        List<Span> childSpans = spans.stream()
                .filter(span -> span.getParentId() != null)
                .collect(Collectors.toList());

        createRecursiveSpans(parentSpans, childSpans);

        info.setSpans(parentSpans);
        return info;
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
        ConversationInfo info = new ConversationInfo();
        info.setId(conversationId);
        info.setTraces(traces);
        info.setStartTimestamp(traces.get(0).getStartTimestamp());
        info.setEndTimestamp(traces.get(traces.size() - 1).getEndTimestamp());
        return info;
    }

    private static SpanInfo convert(Span span) {
        SpanInfo info = new SpanInfo();
        info.setId(span.getId());
        info.setStatus(span.getStatus());
        info.setName(span.getName());
        info.setStartTimestamp(span.getStartTimestamp());
        info.setEndTimestamp(span.getEndTimestamp());
        info.setBeginProcessingTimestamp(span.getBeginProcessingTimestamp());
        info.setFinishProcessingTimestamp(span.getFinishProcessingTimestamp());
        info.setTags(span.getTags());
        info.setIncomingEndpoint(span.getIncomingEndpoint());
        info.setIncomingHeader(span.getIncomingHeader());
        info.setOutgoingEndpoint(span.getOutgoingEndpoint());
        info.setOutgoingHeader(span.getOutgoingHeader());
        return info;
    }
}