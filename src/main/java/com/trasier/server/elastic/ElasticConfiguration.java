package com.trasier.server.elastic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticConfiguration {
    @Value("${trasier.elastic.namespace}")
    private String namespace;
    @Value("${trasier.elastic.hosts}")
    private String[] hosts;
    @Value("${trasier.elastic.port:9200}")
    private Integer port;
    @Value("${trasier.elastic.scheme:http}")
    private String scheme;
    @Value("${trasier.elastic.index}")
    private String index;
    @Value("${trasier.elastic.type:event}")
    private String type;
    @Value("${trasier.elastic.cluster-name}")
    private String clusterName;
    @Value("${trasier.elastic.username}")
    private String username;
    @Value("${trasier.elastic.password}")
    private String password;

    @Bean
    public ElasticService elasticService() {
        ElasticService elasticService = new ElasticService(namespace, new ElasticConverter(new ElasticDataConverter()));
        elasticService.init(clusterName, hosts, port, scheme, username, password);
        return elasticService;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String[] getHosts() {
        return hosts;
    }

    public void setHosts(String[] hosts) {
        this.hosts = hosts;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}