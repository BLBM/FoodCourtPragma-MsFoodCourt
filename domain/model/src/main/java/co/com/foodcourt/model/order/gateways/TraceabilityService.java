package co.com.foodcourt.model.order.gateways;

import co.com.foodcourt.model.order.OrderTrace;

public interface TraceabilityService {
    void saveTrace(OrderTrace trace);
}
