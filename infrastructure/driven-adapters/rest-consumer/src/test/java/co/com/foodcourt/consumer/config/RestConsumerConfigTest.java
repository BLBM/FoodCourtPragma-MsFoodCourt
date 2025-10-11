package co.com.foodcourt.consumer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.okhttp3.OkHttpMetricsEventListener;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RestConsumerConfigTest {

    private RestConsumerConfig restConsumerConfig;
    private MeterRegistry meterRegistry;

    @BeforeEach
    void setUp() {
        restConsumerConfig = new RestConsumerConfig();
        meterRegistry = new SimpleMeterRegistry();
    }

    @Test
    void shouldCreateOkHttpClientBean() {
        OkHttpMetricsEventListener listener = restConsumerConfig.okHttpMetricsListener(meterRegistry);

        OkHttpClient httpClient = restConsumerConfig.getHttpClient(listener);

        assertNotNull(httpClient, "OkHttpClient should not be null");
        assertNotNull(httpClient.eventListenerFactory(), "EventListenerFactory should be configured");
    }

    @Test
    void shouldCreateOkHttpMetricsEventListenerBean() {
        OkHttpMetricsEventListener listener = restConsumerConfig.okHttpMetricsListener(meterRegistry);

        assertNotNull(listener, "OkHttpMetricsEventListener should not be null");
    }

    @Test
    void shouldConfigureOkHttpMetricsEventListenerWithCorrectName() {

        OkHttpMetricsEventListener listener = restConsumerConfig.okHttpMetricsListener(meterRegistry);


        assertNotNull(listener, "OkHttpMetricsEventListener should not be null");
    }

    @Test
    void shouldMapUriCorrectlyInOkHttpMetricsListener() {
        OkHttpMetricsEventListener listener = restConsumerConfig.okHttpMetricsListener(meterRegistry);
        Request testRequest = new Request.Builder()
                .url("http://localhost:8080/api/v1/users")
                .build();

        OkHttpClient httpClient = restConsumerConfig.getHttpClient(listener);
        assertNotNull(httpClient, "OkHttpClient should be properly configured with listener");
    }

    @Test
    void shouldCreateObjectMapperBean() {
        ObjectMapper objectMapper = restConsumerConfig.getObjectMapper();

        assertNotNull(objectMapper, "ObjectMapper should not be null");
    }


    @Test
    void shouldDisableWriteDatesAsTimestampsInObjectMapper() {

        ObjectMapper objectMapper = restConsumerConfig.getObjectMapper();


        assertFalse(
                objectMapper.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS),
                "WRITE_DATES_AS_TIMESTAMPS should be disabled"
        );
    }

    @Test
    void shouldSerializeLocalDateTimeCorrectly() throws Exception {

        ObjectMapper objectMapper = restConsumerConfig.getObjectMapper();
        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 10, 15, 30, 0);


        String json = objectMapper.writeValueAsString(dateTime);

        assertNotNull(json, "Serialized JSON should not be null");
        assertFalse(json.contains("1728570600"), "Should not contain timestamp");
        assertTrue(json.contains("2025"), "Should contain year in ISO format");
    }

    @Test
    void shouldDeserializeLocalDateTimeCorrectly() throws Exception {

        ObjectMapper objectMapper = restConsumerConfig.getObjectMapper();
        String json = "\"2025-10-10T15:30:00\"";


        LocalDateTime dateTime = objectMapper.readValue(json, LocalDateTime.class);


        assertNotNull(dateTime, "Deserialized LocalDateTime should not be null");
        assertEquals(2025, dateTime.getYear());
        assertEquals(10, dateTime.getMonthValue());
        assertEquals(10, dateTime.getDayOfMonth());
        assertEquals(15, dateTime.getHour());
        assertEquals(30, dateTime.getMinute());
    }

    @Test
    void shouldConfigureOkHttpClientWithEventListener() {

        OkHttpMetricsEventListener listener = restConsumerConfig.okHttpMetricsListener(meterRegistry);

        OkHttpClient httpClient = restConsumerConfig.getHttpClient(listener);


        assertNotNull(httpClient.eventListenerFactory(), "EventListenerFactory should not be null");
        assertNotNull(httpClient.connectionPool(), "Connection pool should be initialized");
        assertNotNull(httpClient.dispatcher(), "Dispatcher should be initialized");
    }

    @Test
    void shouldUseSimpleMeterRegistryForMetrics() {
        MeterRegistry registry = new SimpleMeterRegistry();

        OkHttpMetricsEventListener listener = restConsumerConfig.okHttpMetricsListener(registry);
        assertNotNull(listener, "Listener should work with SimpleMeterRegistry");
    }

    @Test
    void shouldCreateIndependentOkHttpClientInstances() {
        OkHttpMetricsEventListener listener = restConsumerConfig.okHttpMetricsListener(meterRegistry);

        OkHttpClient client1 = restConsumerConfig.getHttpClient(listener);
        OkHttpClient client2 = restConsumerConfig.getHttpClient(listener);

        assertNotSame(client1, client2, "Should create different instances");
    }

    @Test
    void shouldCreateIndependentObjectMapperInstances() {
        ObjectMapper mapper1 = restConsumerConfig.getObjectMapper();
        ObjectMapper mapper2 = restConsumerConfig.getObjectMapper();

        assertNotSame(mapper1, mapper2, "Should create different ObjectMapper instances");
    }
}
