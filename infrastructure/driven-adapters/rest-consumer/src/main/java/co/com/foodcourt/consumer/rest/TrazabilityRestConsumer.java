package co.com.foodcourt.consumer.rest;

import co.com.foodcourt.model.order.OrderTrace;
import co.com.foodcourt.model.order.gateways.TraceabilityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TrazabilityRestConsumer implements TraceabilityService {



    @Override
    public void saveTrace(OrderTrace trace) {
        log.info("CALL SERVICE");
    }
}
