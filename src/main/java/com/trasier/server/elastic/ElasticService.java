package com.trasier.server.elastic;

import com.trasier.api.server.model.Span;
import com.trasier.api.server.service.WriteService;
import org.apache.http.HttpHost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import javax.inject.Singleton;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;

@Singleton
public class ElasticService implements WriteService, Closeable {
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

    @Override
    public void writeSpans(String accountId, String spaceKey, List<Span> spans) {
        spans.forEach(span -> write(accountId, spaceKey, span));
    }

    public void write(String accountId, String spaceKey, Span span) {
        IndexRequest indexRequest = createIndexRequest(accountId, spaceKey, span);
        client.indexAsync(indexRequest, new ActionListener<>() {
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