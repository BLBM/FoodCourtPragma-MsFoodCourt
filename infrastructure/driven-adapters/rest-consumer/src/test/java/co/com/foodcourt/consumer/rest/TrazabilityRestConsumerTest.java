package co.com.foodcourt.consumer.rest;

import co.com.foodcourt.model.order.OrderTrace;
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
class TrazabilityRestConsumerTest {

    @Mock
    private OkHttpClient client;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private Call call;

    @Mock
    private Response response;

    private TrazabilityRestConsumer consumer;

    private final String baseUrl = "http://localhost:8085/api/v1/trace";
    private final OrderTrace trace = new OrderTrace();

    @BeforeEach
    void setUp() {
        consumer = new TrazabilityRestConsumer(baseUrl, client, mapper);
        trace.setOrderId(1L);
        trace.setClientId("C001");
        trace.setClientEmail("client@mail.com");
        trace.setPrevStatus("PENDING");
        trace.setNewStatus("READY");
        trace.setEmployeeId(10L);
        trace.setEmployeeEmail("chef@mail.com");
    }

    @Test
    void shouldSendTraceSuccessfully() throws IOException {
        String jsonBody = "{\"orderId\":1}";
        when(mapper.writeValueAsString(trace)).thenReturn(jsonBody);
        when(client.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(true);

        consumer.saveTrace(trace);

        verify(mapper, times(1)).writeValueAsString(trace);
        verify(client, times(1)).newCall(any(Request.class));
        verify(call, times(1)).execute();
    }

    // 2️⃣ Caso error: respuesta HTTP no exitosa (500)
    @Test
    void shouldThrowExternalServiceExceptionWhenResponseNotSuccessful() throws IOException {
        String jsonBody = "{\"orderId\":1}";
        when(mapper.writeValueAsString(trace)).thenReturn(jsonBody);
        when(client.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(false);
        when(response.code()).thenReturn(500);

        assertThrows(ExternalServiceException.class, () -> consumer.saveTrace(trace));

        verify(client, times(1)).newCall(any(Request.class));
        verify(call, times(1)).execute();
    }


    @Test
    void shouldThrowExternalServiceExceptionWhenIOExceptionOccurs() throws IOException {
        String jsonBody = "{\"orderId\":1}";
        when(mapper.writeValueAsString(trace)).thenReturn(jsonBody);
        when(client.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenThrow(new IOException("Connection timeout"));

        assertThrows(ExternalServiceException.class, () -> consumer.saveTrace(trace));

        verify(client, times(1)).newCall(any(Request.class));
        verify(call, times(1)).execute();
    }
    
    @Test
    void shouldBuildRequestWithCorrectHeadersAndUrl() throws IOException {
        String jsonBody = "{\"orderId\":1}";
        when(mapper.writeValueAsString(trace)).thenReturn(jsonBody);

        when(client.newCall(any(Request.class))).thenAnswer(invocation -> {
            Request request = invocation.getArgument(0);
            assertEquals(baseUrl, request.url().toString());
            assertEquals("application/json", request.header("Accept"));
            assertEquals("application/json", request.header("Content-Type"));
            assertEquals("POST", request.method());
            return call;
        });

        when(call.execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(true);

        consumer.saveTrace(trace);

        verify(client, times(1)).newCall(any(Request.class));
        verify(call, times(1)).execute();
    }
}
