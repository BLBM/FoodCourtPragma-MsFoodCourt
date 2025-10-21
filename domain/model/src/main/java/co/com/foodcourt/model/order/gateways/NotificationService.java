package co.com.foodcourt.model.order.gateways;

public interface NotificationService {
    void sendOrderReadyNotification(String msg);
}
