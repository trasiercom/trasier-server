package com.trasier.server.elastic;

import com.trasier.api.server.model.ConversationInfo;
import com.trasier.api.server.model.Span;
import com.trasier.api.server.service.ReadService;
import com.trasier.api.server.service.WriteService;
import com.trasier.server.data.ConversationProcessor;
import com.trasier.server.data.SpanProcessor;
import org.apache.http.HttpHost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

import javax.inject.Singleton;
import java.io.Closeable;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Singleton
public class ElasticService implements WriteService, ReadService, Closeable {
    static final TimeZone TIME_ZONE = TimeZone.getTimeZone(ZoneId.of("CET"));
    private static final int MAX_NUMBER_OF_RESULTS = 500;
    private static final String TYPE = "span";

    private ElasticConfiguration configuration;
    private final ElasticConverter converter;

    private static RestHighLevelClient client;

    public ElasticService(ElasticConfiguration configuration, ElasticConverter converter) {
        this.configuration = configuration;
        this.converter = converter;

        HttpHost[] httpHosts = new HttpHost[configuration.getHosts().length];
        for (int i = 0; i < configuration.getHosts().length; i++) {
            String host = configuration.getHosts()[i];
            httpHosts[i] = new HttpHost(host, configuration.getPort(), configuration.getScheme());
        }
        RestClientBuilder clientBuilder = RestClient.builder(httpHosts);
        clientBuilder.setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                .setConnectTimeout(30000)
                .setSocketTimeout(30000)
                .setConnectionRequestTimeout(10000));
        clientBuilder.setHttpClientConfigCallback(httpClientBuilder -> {
            try {
                return httpClientBuilder
                        .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                        .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, (TrustStrategy) (arg0, arg1) -> true).build())
                        .setDefaultIOReactorConfig(IOReactorConfig.custom().setIoThreadCount(8).build());
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
        client = new RestHighLevelClient(clientBuilder);
    }

    @Override
    public Span readSpanById(String accountId, String spaceKey, String conversationId, String traceId, String spanId) {
        SearchRequest searchRequest = new SearchRequest()
                .indices(createIndexName(accountId, spaceKey))
                .searchType(SearchType.DFS_QUERY_THEN_FETCH)
                .indicesOptions(IndicesOptions.lenientExpandOpen());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(createQuery(accountId, spaceKey, conversationId, traceId, spanId))
                .from(0)
                .sort("startTimestamp", SortOrder.DESC)
                .size(MAX_NUMBER_OF_RESULTS)
                .trackTotalHitsUpTo(MAX_NUMBER_OF_RESULTS)
                .timeout(new TimeValue(30, TimeUnit.SECONDS));
        searchRequest.source(searchSourceBuilder);

        RequestOptions options = RequestOptions.DEFAULT;

        try {
            SearchResponse response = client.search(searchRequest, options);
            if (response.getHits().getTotalHits().value > 0) {
                List<Span> spans = Arrays.stream(response.getHits().getHits())
                        .map(ElasticConverter::convert)
                        .collect(Collectors.toList());
                List<Span> cleanedSpans = SpanProcessor.cleanSpans(spans);
                return cleanedSpans.get(0);
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public ConversationInfo readConversationById(String accountId, String spaceKey, String conversationId) {
        SearchRequest searchRequest = new SearchRequest()
                .indices(createIndexName(accountId, spaceKey))
                .searchType(SearchType.DFS_QUERY_THEN_FETCH)
                .indicesOptions(IndicesOptions.lenientExpandOpen());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(createQuery(accountId, spaceKey, conversationId))
                .from(0)
                .sort("startTimestamp", SortOrder.DESC)
                .size(MAX_NUMBER_OF_RESULTS)
                .trackTotalHitsUpTo(MAX_NUMBER_OF_RESULTS)
                .timeout(new TimeValue(30, TimeUnit.SECONDS));
        searchRequest.source(searchSourceBuilder);

        RequestOptions options = RequestOptions.DEFAULT;

        try {
            SearchResponse response = client.search(searchRequest, options);
            if (response.getHits().getTotalHits().value > 0) {
                List<Span> spans = Arrays.stream(response.getHits().getHits())
                        .map(ElasticConverter::convert)
                        .collect(Collectors.toList());
                List<Span> cleanedSpans = SpanProcessor.cleanSpans(spans);
                return ConversationProcessor.convert(conversationId, cleanedSpans);
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<ConversationInfo> findByQuery(String accountId, String spaceKey, String query, Long from, Long to) {
        SearchRequest searchRequest = new SearchRequest()
                .indices(createIndexName(accountId, spaceKey))
                .searchType(SearchType.DFS_QUERY_THEN_FETCH)
                .indicesOptions(IndicesOptions.lenientExpandOpen());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(createQuery(accountId, spaceKey, query, from, to))
                .from(0)
                .sort("startTimestamp", SortOrder.DESC)
                .size(MAX_NUMBER_OF_RESULTS)
                .trackTotalHitsUpTo(MAX_NUMBER_OF_RESULTS)
                .timeout(new TimeValue(30, TimeUnit.SECONDS));
        searchRequest.source(searchSourceBuilder);

        RequestOptions options = RequestOptions.DEFAULT;

        try {
            SearchResponse response = client.search(searchRequest, options);
            if (response.getHits().getTotalHits().value > 0) {
                List<Span> spans = Arrays.stream(response.getHits().getHits())
                        .map(ElasticConverter::convert)
                        .collect(Collectors.toList());
                List<Span> cleanedSpans = SpanProcessor.cleanSpans(spans);
                return ConversationProcessor.convert(cleanedSpans);
            } else {
                return Collections.emptyList();
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void writeSpans(String accountId, String spaceKey, List<Span> spans) {
        spans.forEach(span -> write(accountId, spaceKey, span));
    }

    public void write(String accountId, String spaceKey, Span span) {
        IndexRequest indexRequest = createIndexRequest(accountId, spaceKey, span);
        client.indexAsync(indexRequest, RequestOptions.DEFAULT, new ActionListener<>() {
            @Override
            public void onResponse(IndexResponse response) {
                //Currently ignored
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }

    BoolQueryBuilder createQuery(String accountId, String spaceKey, String conversationId) {

        BoolQueryBuilder appIdQuery = QueryBuilders.boolQuery();
        appIdQuery.must(QueryBuilders.matchQuery("accountId", accountId));
        appIdQuery.must(QueryBuilders.matchQuery("spaceKey", spaceKey));
        appIdQuery.must(QueryBuilders.matchQuery("conversationId", conversationId));

        return QueryBuilders.boolQuery()
                .must(appIdQuery);
    }

    BoolQueryBuilder createQuery(String accountId, String spaceKey, String conversationId, String traceId, String spanId) {

        BoolQueryBuilder appIdQuery = QueryBuilders.boolQuery();
        appIdQuery.must(QueryBuilders.matchQuery("accountId", accountId));
        appIdQuery.must(QueryBuilders.matchQuery("spaceKey", spaceKey));
        appIdQuery.must(QueryBuilders.matchQuery("conversationId", conversationId));
        appIdQuery.must(QueryBuilders.matchQuery("traceId", traceId));
        appIdQuery.must(QueryBuilders.matchQuery("spanId", spanId));

        return QueryBuilders.boolQuery()
                .must(appIdQuery);
    }

    BoolQueryBuilder createQuery(String accountId, String spaceKey, String query, Long from, Long to) {
        Calendar calendar = Calendar.getInstance(ElasticService.TIME_ZONE);
        calendar.setTimeInMillis(from);
        Date fromDate = calendar.getTime();
        calendar.setTimeInMillis(to);
        Date toDate = calendar.getTime();
        RangeQueryBuilder timestampQuery = QueryBuilders.rangeQuery("startTimestamp").gte(fromDate).lte(toDate);

        BoolQueryBuilder appIdQuery = QueryBuilders.boolQuery();
        appIdQuery.must(QueryBuilders.matchQuery("accountId", accountId));
        appIdQuery.must(QueryBuilders.matchQuery("spaceKey", spaceKey));

        return QueryBuilders.boolQuery()
                .must(QueryBuilders.queryStringQuery(query))
                .must(appIdQuery)
                .must(timestampQuery);
    }

    IndexRequest createIndexRequest(String accountId, String spaceKey, Span span) {
        IndexRequest indexRequest = new IndexRequest(createIndexName(accountId, spaceKey), TYPE);
        indexRequest.source(converter.convert(accountId, spaceKey, span));
        indexRequest.routing(accountId + "_" + spaceKey);
        return indexRequest;
    }

    String createIndexName(String accountId, String spaceKey) {
        return configuration.getNamespace() + "_" + accountId + "_" + spaceKey;
    }

    @Override
    public void close() throws IOException {
        client.close();
    }
}