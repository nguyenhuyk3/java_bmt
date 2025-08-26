package com.bmt.java_bmt.configurations;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@Configuration
public class ElasticsearchConfiguration {
    @Value("${elasticsearch.host}")
    private String HOST;

    @Value("${elasticsearch.port}")
    private int PORT;

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        // Tạo RestClient cấp thấp
        RestClient restClient = RestClient.builder(new HttpHost(HOST, PORT)).build();
        // Tạo transport với Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        // Và tạo API client
        return new ElasticsearchClient(transport);
    }
}
