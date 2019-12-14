package com.trasier.server.elastic;

import com.trasier.api.server.model.ConversationInfo;
import com.trasier.api.server.model.Span;
import com.trasier.api.server.service.WriteService;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.http.HttpHost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
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
public class ElasticService implements WriteService, Closeable {
    static final TimeZone TIME_ZONE = TimeZone.getTimeZone(ZoneId.of("CET"));
    private static final FastDateFormat DATE_TIME_FORMATTER = FastDateFormat.getInstance("yyyy-MM-dd");
    private static final FastDateFormat ALTERNATIVE_DATE_TIME_FORMATTER = FastDateFormat.getInstance("yyyy.MM.dd");
    private static final int MAX_NUMBER_OF_RESULTS = 500;
    private static final String TYPE = "span";

    private ElasticConfiguration configuration;
    private final ElasticConverter converter;

    private static RestHighLevelClient client;

    public ElasticService(ElasticConfiguration configuration, ElasticConverter converter) {
        this.configuration = configuration;
        this.converter = converter;
        init(configuration.getClusterName(), configuration.getHosts(), configuration.getPort(), configuration.getScheme());
    }

    public void init(String clusterName, String[] hosts, Integer port, String scheme) {
        //TODO settings wofür
        Settings settings = Settings.builder()
                .put("client.transport.sniff", true)
                .put("cluster.name", clusterName)
                .build();
        HttpHost[] httpHosts = new HttpHost[hosts.length];
        for (int i = 0; i < hosts.length; i++) {
            String host = hosts[i];
            httpHosts[i] = new HttpHost(host, port, scheme);
        }
        RestClientBuilder clientBuilder = RestClient.builder(httpHosts);
        clientBuilder.setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                .setConnectTimeout(30000)
                .setSocketTimeout(30000)
                .setConnectionRequestTimeout(10000))
                .setMaxRetryTimeoutMillis(30000);
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

    public void shutdown() throws IOException {
        client.close();
    }

    @Override
    public void writeSpans(String accountId, String spaceKey, List<Span> spans) {
        spans.forEach(span -> write(accountId, spaceKey, span));
    }

    public void write(String accountId, String spaceKey, Span span) {
        IndexRequest indexRequest = createIndexRequest(accountId, spaceKey, span);
        client.indexAsync(indexRequest, new ActionListener<IndexResponse>() {
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

    public List<ConversationInfo> findByQuery(String accountId, String spaceKey, String query, Long from, Long to) {
        SearchRequest searchRequest = new SearchRequest()
                .indices(createIndexName(accountId, spaceKey))
                .types(TYPE)
                .searchType(SearchType.DFS_QUERY_THEN_FETCH)
                .indicesOptions(IndicesOptions.lenientExpandOpen());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(createQuery(accountId, spaceKey, query, from, to))
                .from(0)
                .sort("startTimestamp", SortOrder.DESC)
                .size(MAX_NUMBER_OF_RESULTS)
                .timeout(new TimeValue(30, TimeUnit.SECONDS));
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse response = client.search(searchRequest);
            if (response.getHits().totalHits > 0) {
                List<Span> spans = Arrays.stream(response.getHits().getHits())
                        .map(ElasticConverter::convert)
                        .collect(Collectors.toList());
//                List<Span> cleanedSpans = SpanProcessor.cleanSpans(spans);
//                return ConversationProcessor.convert(cleanedSpans);
                return null; //TODO
            } else {
                return Collections.emptyList();
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private BulkRequest createBulkRequest(String accountId, String spaceKey, List<Span> spans) {
        BulkRequest bulkRequest = new BulkRequest();
        spans.forEach(span -> {
            IndexRequest indexRequest = createIndexRequest(accountId, spaceKey, span);
            bulkRequest.add(indexRequest);
        });
        return bulkRequest;
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
        indexRequest.routing(accountId + "_" + spaceKey); //TODO Ist das eine gute Idee?
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