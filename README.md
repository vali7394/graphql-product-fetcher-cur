# Product Search Service

A Spring Boot application that processes product search keywords from Google Cloud Storage, queries a GraphQL API, and stores results in BigQuery.

## Features

- Java 21 with Virtual Threads
- Spring Boot 3.2+
- Google Cloud Storage integration
- GraphQL client with rate limiting
- BigQuery integration
- Asynchronous processing
- Health endpoints

## Prerequisites

- Java 21 or higher
- Gradle 8.5 or higher
- Google Cloud Platform account with:
  - GCS bucket with keywords file
  - BigQuery dataset and table
  - Appropriate service account credentials

## Configuration

Configure the application using `application.yml` or environment variables:

```yaml
app:
  gcs:
    bucket: ${GCS_BUCKET:my-keywords-bucket}
    file: ${GCS_FILE:keywords.txt}
  graphql:
    url: ${GRAPHQL_URL:https://your-api/graphql}
    rateLimitPerSec: ${GRAPHQL_RATE_LIMIT:10}
    headers:
      authorization: ${GRAPHQL_AUTH_TOKEN:}
  bigquery:
    dataset: ${BIGQUERY_DATASET:product_data}
    table: ${BIGQUERY_TABLE:product_search_results}
```

## Building

```bash
./gradlew build
```

## Running

```bash
./gradlew bootRun
```

Or with custom configuration:

```bash
java -jar build/libs/product-search-service-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --app.gcs.bucket=my-bucket \
  --app.graphql.url=https://api.example.com/graphql
```

## API Endpoints

- `POST /api/v1/search/process`: Start processing keywords
- `GET /api/v1/health`: Health check endpoint

## Google Cloud Setup

1. Create a service account with appropriate permissions:
   - Storage Object Viewer
   - BigQuery Data Editor

2. Download the service account key and set the environment variable:
   ```bash
   export GOOGLE_APPLICATION_CREDENTIALS=/path/to/service-account-key.json
   ```

3. Create a GCS bucket and upload your keywords file:
   ```bash
   gsutil cp keywords.txt gs://my-bucket/
   ```

4. Create a BigQuery dataset and table:
   ```sql
   CREATE DATASET product_data;
   CREATE TABLE product_data.product_search_results (
     id STRING,
     numFound INT64,
     success BOOL
   );
   ```

## Development

The application uses:
- Virtual threads for concurrent processing
- Bucket4j for rate limiting
- Netflix DGS GraphQL client
- Google Cloud client libraries
- Lombok for reducing boilerplate
- SLF4J for logging

## Error Handling

The application includes comprehensive error handling:
- GCS file reading errors
- GraphQL API errors
- BigQuery writing errors
- Rate limiting errors

All errors are logged with appropriate context and stack traces.

## Monitoring

The application exposes:
- Health endpoint
- Actuator endpoints (if enabled)
- Structured logging

## License

This project is licensed under the MIT License. 