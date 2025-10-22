package co.com.foodcourt.consumer.rest;

import co.com.foodcourt.consumer.common.LogConstants;
import co.com.foodcourt.model.order.OrderTrace;
import co.com.foodcourt.model.order.gateways.TraceabilityService;
import co.com.foodcourt.model.user.exception.ExternalServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class TrazabilityRestConsumer implements TraceabilityService {


    private final String url;
    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private static final String APPLICATION_JSON= "application/json";

    public TrazabilityRestConsumer(
            @Value("${adapter.traceability.url}") String url,
            OkHttpClient client,
            ObjectMapper mapper) {
        this.url = url;
        this.client = client;
        this.mapper = mapper;
    }


    @Override
    public void saveTrace(OrderTrace trace) {
        try {
            String jsonBody = mapper.writeValueAsString(trace);
            log.info(jsonBody, LogConstants.SEND_REQUEST_TRACEABILITY);

            RequestBody body = RequestBody.create(jsonBody, MediaType.parse(APPLICATION_JSON));
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Accept", APPLICATION_JSON)
                    .addHeader("Content-Type", APPLICATION_JSON)
                    .build();

            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                log.error(String.valueOf(LogConstants.ERROR_MS_TRACEABILITY), response.code());
                throw new IOException(LogConstants.IO_EXCEPTION_MESSAGE.getMessage() + response.code());
            }

            log.info("");
        } catch (IOException ex) {
            log.error(ex.getMessage(), LogConstants.ERROR_MS_TRACEABILITY);
            throw new ExternalServiceException(LogConstants.ERROR_EXTERNAL_EXCEPTION.getMessage());
        }
    }
}