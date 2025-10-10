package co.com.foodcourt.consumer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.okhttp3.OkHttpMetricsEventListener;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestConsumerConfig {

    @Bean
    public OkHttpClient getHttpClient(OkHttpMetricsEventListener listener) {
        return new OkHttpClient.Builder()
                .eventListener(listener)
                .build();
    }

    @Bean
    public OkHttpMetricsEventListener okHttpMetricsListener(MeterRegistry registry) {
        return OkHttpMetricsEventListener.builder(registry, "http-outgoing")
                .uriMapper(req -> req.url().encodedPath()).build();
    }

    @Bean
    public ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

}
