package co.com.foodcourt.consumer.rest;


import co.com.foodcourt.consumer.common.LogConstants;
import co.com.foodcourt.consumer.dto.UserResponse;
import co.com.foodcourt.consumer.mapper.UserRestMapper;
import co.com.foodcourt.model.user.User;
import co.com.foodcourt.model.user.exception.ExternalServiceException;
import co.com.foodcourt.model.user.gateways.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class RestConsumer implements UserRepository
{
    private final String url;
    private final OkHttpClient client;
    private final ObjectMapper mapper;

    public RestConsumer(@Value("${adapter.restconsumer.url}") String url, OkHttpClient client, ObjectMapper mapper) {
        this.url = url;
        this.client = client;
        this.mapper = mapper;
    }


    @Override
    @CircuitBreaker(name = "getUserById", fallbackMethod = "getUserByIdFallback")
    public User getUserById(Long userId) {
        try {
        String endpoint = String.format("%s/%s", url, userId);
        log.info(String.valueOf(LogConstants.REQUEST_MS_AUTH), userId);
        Request request = new Request.Builder()
                .url(endpoint)
                .get()
                .addHeader("Accept", "application/json")
                .build();

        UserResponse response = callAndMap(request, UserResponse.class);
        log.info(response.getRole(), LogConstants.SUCCESS_MS_AUTH);
        return UserRestMapper.INSTANCE.toDomain(response);

        } catch (IOException ex) {
            log.warn(LogConstants.ERROR_GET_USER_BY_ID.getMessage(),ex.getMessage());
            throw new ExternalServiceException(LogConstants.ERROR_MS_AUTH.getMessage());
        }
    }

    public User getUserByIdFallback(Long userId, Throwable ex) {
        log.error(LogConstants.ERROR_FALLBACK_EXECUTE.getMessage(), ex.getMessage());
        log.error(LogConstants.ERROR_FALLBACK_EXECUTE.getMessage(),userId);

        return User.builder()
                .firstName("Unknown")
                .lastName("Unknown")
                .email("unknown@example.com")
                .phone("N/A")
                .role("UnknownRole")
                .build();
    }

    private <T> T callAndMap(Request request, Class<T> clazz) throws IOException {
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return mapper.readValue(response.body().string(), clazz);
        }
        throw new IOException(response.toString());
    }


}
