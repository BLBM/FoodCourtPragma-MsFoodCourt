package co.com.foodcourt.callmebot;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


@ExtendWith(MockitoExtension.class)
class WhatsAppNotificationAdapterTest {

    @Mock
    private RestTemplate restTemplate;

    private WhatsAppNotificationAdapter adapter;

    private final String apiUrl = "https://api.callmebot.com/whatsapp.php";
    private final String apiKey = "TEST_KEY";
    private final String defaultPhone = "3155849871";

    @BeforeEach
    void setUp() {
        adapter = new WhatsAppNotificationAdapter(apiUrl, apiKey, defaultPhone, restTemplate);
    }

    @Test
    void shouldSendOrderReadyNotificationSuccessfully() {
        String msg = "Order #1 ready for pickup";
        String encodedMsg = URLEncoder.encode(msg.replace("\n", "%0A"), StandardCharsets.UTF_8);

        String expectedUrl = String.format("%s?phone=%s&text=%s&apikey=%s",
                apiUrl.trim(),
                defaultPhone.trim(),
                encodedMsg.trim(),
                apiKey.trim()
        );

        when(restTemplate.getForObject(eq(expectedUrl), eq(String.class))).thenReturn("Message sent");

        assertDoesNotThrow(() -> adapter.sendOrderReadyNotification(msg));

        verify(restTemplate, times(1)).getForObject(eq(expectedUrl), eq(String.class));
    }

    @Test
    void shouldHandleExceptionGracefullyWhenRestTemplateFails() {
        String msg = "Order #2 ready for pickup";

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new RuntimeException("Connection error"));

        assertDoesNotThrow(() -> adapter.sendOrderReadyNotification(msg));

        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
    }
}


