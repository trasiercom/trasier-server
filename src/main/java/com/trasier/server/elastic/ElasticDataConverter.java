package com.trasier.server.elastic;

import com.trasier.api.ContentType;

public class ElasticDataConverter {
    public String removeMarkup(ContentType contentType, String data) {
        String noXml = data.replaceAll("<[^>^=]+>", " ");
        String noWhitespace1 = noXml.replaceAll("\\s+", " ");
        String cleanData = removeMarkupByContentType(noWhitespace1, contentType);
        return cleanData.substring(0, Math.min(cleanData.length(), 50 * 1024)); //max 100KB
    }

    private String removeMarkupByContentType(String payload, ContentType contentType) {
        if (ContentType.XML.equals(contentType)) {
            String noLongWords = payload.replaceAll("[^\\s]{51,}", "");
            String noWhitespace2 = noLongWords.replaceAll("\\s+", " ");
            return noWhitespace2.trim();
        } else {
            return payload.trim();
        }
    }
}