package com.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private Gcs gcs;
    private Graphql graphql;
    private Bigquery bigquery;

    @Data
    public static class Gcs {
        private String bucket;
        private String file;
    }

    @Data
    public static class Graphql {
        private String url;
        private int rateLimitPerSec;
        private Headers headers;
    }

    @Data
    public static class Headers {
        private String authorization;
    }

    @Data
    public static class Bigquery {
        private String dataset;
        private String table;
    }
} 