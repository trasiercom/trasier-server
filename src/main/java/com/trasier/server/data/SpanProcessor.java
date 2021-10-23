package com.trasier.server.data;


import com.trasier.api.server.model.Endpoint;
import com.trasier.api.server.model.Span;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpanProcessor {

    private static final String UNKNOWN = "UNKNOWN";

    private SpanProcessor() {}

    public static List<Span> cleanSpans(List<Span> allSpans) {
        List<Span> cleaned = new ArrayList<>();
        Map<String, List<Span>> spansByConversationId = groupByConversationId(allSpans);
        spansByConversationId.forEach((conversationId, spansOfConversation) -> {
            Map<String, List<Span>> spansByTraceId = groupByTraceId(spansOfConversation);
            spansByTraceId.forEach((traceId, spansOfTrace) -> {
                sortSpans(spansOfTrace);
                List<Span> mergedSpans = mergeSpans(spansOfTrace);
                calculateCorrectParentHierarchy(mergedSpans);
                cleaned.addAll(mergedSpans);
            });
        });
        return cleaned;
    }

    private static void sortSpans(List<Span> spansOfTrace) {
        spansOfTrace.forEach(span -> {
            if(span.getStartTimestamp() == null) {
                span.setStartTimestamp(span.getEndTimestamp());
            }
            if(span.getEndTimestamp() == null) {
                span.setEndTimestamp(span.getStartTimestamp());
            }
        });

        spansOfTrace.sort(Comparator.comparing(Span::getId).thenComparing(Comparator.comparing(Span::getStartTimestamp).thenComparing(Span::getEndTimestamp).reversed()));

        //remove all invalid parentIds
        spansOfTrace.stream()
                .filter(span -> span.getId().equals(span.getParentId()))
                .forEach(span -> span.setParentId(null));
    }

    private static List<Span> mergeSpans(List<Span> spans) {
        Map<String, Span> spanById = new HashMap<>();
        //TODO support multiple
        spans.forEach(span -> spanById.merge(span.getId(), span, SpanProcessor::mergeSpans));
        return new ArrayList<>(spanById.values());
    }

    private static void calculateCorrectParentHierarchy(List<Span> spansOfTrace) {
        spansOfTrace.sort(Comparator.comparing(Span::getStartTimestamp).thenComparing(Span::getEndTimestamp).reversed());

        //try to set correct parentIds if not already set
        if(spansOfTrace.size() > 1) {
            for (int i = 1; i < spansOfTrace.size(); i++) {
                Span span = spansOfTrace.get(i);
                if (span.getParentId() != null) {
                    for (int k = i; k >= 0; k--) {
                        Span parent = spansOfTrace.get(k);
                        if(span.getStartTimestamp() > parent.getStartTimestamp()
                                && span.getStartTimestamp() < parent.getEndTimestamp()) {
                            span.setParentId(parent.getId());
                            break;
                        }
                    }
                }
            }
        }

        //clean all parents
        spansOfTrace.stream()
            .filter(span -> span.getId().equals(span.getParentId()))
            .forEach(span -> span.setParentId(null));

        //clean all with missing parent
        spansOfTrace.stream()
                .filter(span -> span.getParentId() != null)
                .filter(span -> spansOfTrace.stream().noneMatch(parent -> parent.getId().equals(span.getParentId())))
                .forEach(span -> span.setParentId(null));

        spansOfTrace.sort(Comparator.comparing(Span::getStartTimestamp).thenComparing(Span::getEndTimestamp));
    }

    private static Span mergeSpans(Span original, Span merge) {
        if(merge.getStatus() != null && "ERROR".equalsIgnoreCase(merge.getStatus())) {
            original.setStatus("ERROR");
        }

        if(merge.getName() != null) {
            original.setName(merge.getName());
        }

        if(original.getStartTimestamp() == null
                || (merge.getStartTimestamp() != null && original.getStartTimestamp() > merge.getStartTimestamp())) {
            original.setStartTimestamp(merge.getStartTimestamp());
        }

        if(original.getTags() == null) {
            original.setTags(merge.getTags());
        } else if(merge.getTags() != null) {
            original.getTags().putAll(merge.getTags());
        }

        if(original.getFeatures() == null) {
            original.setFeatures(merge.getFeatures());
        } else if(merge.getFeatures() != null) {
            original.getFeatures().putAll(merge.getFeatures());
        }

        if(original.getIncomingEndpoint() == null) {
            original.setIncomingEndpoint(merge.getIncomingEndpoint());
        } else if(merge.getIncomingEndpoint() != null) {
            mergeEndpoints(original.getIncomingEndpoint(), merge.getIncomingEndpoint());
        }
        if(original.getIncomingHeader() == null) {
            original.setIncomingHeader(merge.getIncomingHeader());
        } else if(merge.getIncomingHeader() != null) {
            original.getIncomingHeader().putAll(merge.getIncomingHeader());
        }
        if(original.getIncomingData() == null) {
            original.setIncomingData(merge.getIncomingData());
            original.setIncomingContentType(merge.getIncomingContentType());
        }

        if(original.getEndTimestamp() == null
                || (merge.getEndTimestamp() != null && original.getEndTimestamp() < merge.getEndTimestamp())) {
            original.setEndTimestamp(merge.getEndTimestamp());
        }
        if(original.getOutgoingEndpoint() == null) {
            original.setOutgoingEndpoint(merge.getOutgoingEndpoint());
        } else if(merge.getOutgoingEndpoint() != null) {
            Endpoint originalOutgoingEndpoint = original.getOutgoingEndpoint();
            original.setOutgoingEndpoint(merge.getOutgoingEndpoint());
            mergeEndpoints(original.getOutgoingEndpoint(), originalOutgoingEndpoint);
        }
        if(original.getOutgoingHeader() == null) {
            original.setOutgoingHeader(merge.getOutgoingHeader());
        } else if(merge.getOutgoingHeader() != null) {
            original.getOutgoingHeader().putAll(merge.getOutgoingHeader());
        }
        if(original.getOutgoingData() == null) {
            original.setOutgoingData(merge.getOutgoingData());
            original.setOutgoingContentType(merge.getOutgoingContentType());
        }
        if (original.getParentId() == null) {
            original.setParentId(merge.getParentId());
        }

        return original;
    }

    private static void mergeEndpoints(Endpoint original, Endpoint merge) {
        if(original.getName() == null || original.getName().contains(UNKNOWN)) {
            original.setName(merge.getName());
        }
        if(original.getHostname() == null) {
            original.setHostname(merge.getHostname());
        }
        if(original.getIpAddress() == null) {
            original.setIpAddress(merge.getIpAddress());
        }
        if(original.getPort() == null) {
            original.setPort(merge.getPort());
        }
    }

    public static Map<String, List<Span>> groupByConversationId(List<Span> spans) {
        return spans.stream().collect(Collectors.groupingBy(Span::getConversationId, Collectors.toList()));
    }

    public static Map<String, List<Span>> groupByTraceId(List<Span> spans) {
        return spans.stream().collect(Collectors.groupingBy(Span::getTraceId, Collectors.toList()));
    }
}