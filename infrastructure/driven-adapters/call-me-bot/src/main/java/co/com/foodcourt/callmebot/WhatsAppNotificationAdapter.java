package co.com.foodcourt.callmebot;

import co.com.foodcourt.model.order.gateways.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class WhatsAppNotificationAdapter implements NotificationService {


    private static final String WHATSAPP_SENT = "WhatsApp sent to 3155849871 - Response: {}";
    private static final String ERROR_MESSAGE = "Error sending whatsapp message";

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String apiKey;
    private final String defaultPhone;

    public WhatsAppNotificationAdapter(
            @Value("${whatsapp.api.url}") String apiUrl,
            @Value("${whatsapp.api.key}") String apiKey,
            @Value("${whatsapp.api.default-phone}") String defaultPhone,
            RestTemplate restTemplate) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.defaultPhone = defaultPhone;
        this.restTemplate = restTemplate;
    }

    @Override
    public void sendOrderReadyNotification(String msg) {
        try {
            String sanitizedMsg = msg.replace("\n", "%0A");
            String encodedMsg = URLEncoder.encode(sanitizedMsg, StandardCharsets.UTF_8);

            String url = String.format("%s?phone=%s&text=%s&apikey=%s",
                    apiUrl.trim(),
                    defaultPhone.trim(),
                    encodedMsg.trim(),
                    apiKey.trim()
            );

            String response = restTemplate.getForObject(url, String.class);
            log.info(WHATSAPP_SENT,response);
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e);
        }
    }
}
