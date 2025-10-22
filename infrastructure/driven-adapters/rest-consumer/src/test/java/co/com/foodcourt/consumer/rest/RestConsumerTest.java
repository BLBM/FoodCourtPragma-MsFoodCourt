package co.com.foodcourt.consumer.rest;

import co.com.foodcourt.consumer.dto.UserResponse;
import co.com.foodcourt.model.user.User;
import co.com.foodcourt.model.user.exception.ExternalServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestConsumerTest {

    @Mock
    private OkHttpClient client;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private Call call;

    @Mock
    private Response response;

    @Mock
    private ResponseBody responseBody;

    private RestConsumer restConsumer;
    private UserResponse userResponse;

    private final String baseUrl = "http://localhost:8090/api/v1/users";
    private final String jsonResponse = "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@example.com\",\"phone\":\"+573155544545\",\"role\":\"OWNER\"}";

    @BeforeEach
    void setUp() {
        restConsumer = new RestConsumer(baseUrl, client, mapper);

        userResponse = UserResponse.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("+573155544545")
                .role("OWNER")
                .build();
    }

    @Test
    void shouldGetUserByIdSuccessfully() throws IOException {

        Long userId = 1L;

        when(client.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(true);
        when(response.body()).thenReturn(responseBody);
        when(responseBody.string()).thenReturn(jsonResponse);
        when(mapper.readValue(jsonResponse, UserResponse.class)).thenReturn(userResponse);


        User result = restConsumer.getUserById(userId);


        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john@example.com", result.getEmail());
        assertEquals("+573155544545", result.getPhone());
        assertEquals("OWNER", result.getRole());

        verify(client, times(1)).newCall(any(Request.class));
        verify(call, times(1)).execute();
    }

    @Test
    void shouldThrowExternalServiceExceptionWhenResponseIsNotSuccessful() throws IOException {

        Long userId = 1L;

        when(client.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(false);
        when(response.toString()).thenReturn("Response{protocol=http/1.1, code=404, message=Not Found}");

        assertThrows(ExternalServiceException.class, () -> restConsumer.getUserById(userId));

        verify(client, times(1)).newCall(any(Request.class));
        verify(call, times(1)).execute();
    }

    @Test
    void shouldThrowExternalServiceExceptionWhenIOExceptionOccurs() throws IOException {

        Long userId = 1L;

        when(client.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenThrow(new IOException("Connection timeout"));


        assertThrows(ExternalServiceException.class, () -> restConsumer.getUserById(userId));

        verify(client, times(1)).newCall(any(Request.class));
        verify(call, times(1)).execute();
    }

    @Test
    void shouldReturnFallbackUserWhenCircuitBreakerActivates() {

        Long userId = 1L;
        Throwable exception = new RuntimeException("Service unavailable");


        User fallbackUser = restConsumer.getUserByIdFallback(userId, exception);


        assertNotNull(fallbackUser);
        assertEquals("Unknown", fallbackUser.getFirstName());
        assertEquals("Unknown", fallbackUser.getLastName());
        assertEquals("unknown@example.com", fallbackUser.getEmail());
        assertEquals("N/A", fallbackUser.getPhone());
        assertEquals("UnknownRole", fallbackUser.getRole());
    }
    

    @Test
    void shouldConstructUrlCorrectly() throws IOException {

        Long userId = 123L;
        String expectedUrl = baseUrl + "/" + userId;

        when(client.newCall(any(Request.class))).thenAnswer(invocation -> {
            Request request = invocation.getArgument(0);
            assertEquals(expectedUrl, request.url().toString());
            return call;
        });
        when(call.execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(true);
        when(response.body()).thenReturn(responseBody);
        when(responseBody.string()).thenReturn(jsonResponse);
        when(mapper.readValue(anyString(), eq(UserResponse.class))).thenReturn(userResponse);

        restConsumer.getUserById(userId);

        verify(client, times(1)).newCall(any(Request.class));
    }

    @Test
    void shouldAddCorrectHeadersToRequest() throws IOException {

        Long userId = 1L;

        when(client.newCall(any(Request.class))).thenAnswer(invocation -> {
            Request request = invocation.getArgument(0);
            assertEquals("application/json", request.header("Accept"));
            return call;
        });
        when(call.execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(true);
        when(response.body()).thenReturn(responseBody);
        when(responseBody.string()).thenReturn(jsonResponse);
        when(mapper.readValue(anyString(), eq(UserResponse.class))).thenReturn(userResponse);

        restConsumer.getUserById(userId);

        verify(client, times(1)).newCall(any(Request.class));
    }
}