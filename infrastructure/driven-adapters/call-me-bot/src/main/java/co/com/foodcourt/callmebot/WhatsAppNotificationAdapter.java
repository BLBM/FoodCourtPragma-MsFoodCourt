package co.com.foodcourt.callmebot;

import co.com.foodcourt.model.order.gateways.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class WhatsAppNotificationAdapter implements NotificationService {


    private static final String WHATSAPP_SENT= "WhatsApp sent to 3155849871 - Response: {}";
    private static final String ERROR_MESSAGE = "Error sending whatsapp message";


    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${whatsapp.api.url}")
    private String apiUrl;

    @Value("${whatsapp.api.key}")
    private String apiKey;

    @Value("${whatsapp.api.default-phone}")
    private String defaultPhone;



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
