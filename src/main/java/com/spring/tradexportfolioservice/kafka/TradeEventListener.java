package com.spring.tradexportfolioservice.kafka;

import com.spring.tradexportfolioservice.Enums.TradeType;
import com.spring.tradexportfolioservice.Service.PortfolioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradeEventListener {

    private final PortfolioService portfolioService;
    private final KafkaTemplate<String, TradeResultEvent> kafkaTemplate;

    @KafkaListener(topics = "trade-requests-topic", groupId = "portfolio-service-group")
    public void listen(TradeEvent event) {
        log.info("Received TradeEvent: {}", event);

        TradeResultEvent resultEvent = new TradeResultEvent();
        resultEvent.setIdempotencyKey(event.getIdempotencyKey());
        resultEvent.setTradeId(event.getTradeId());

        try {
            if (event.getTradeType() == TradeType.BUY) {
                portfolioService.handleBuy(
                        event.getIdempotencyKey(),
                        event.getUserId(),
                        event.getStockId(),
                        event.getQuantity(),
                        event.getExecutionPrice()
                );
            } else if (event.getTradeType() == TradeType.SELL) {
                portfolioService.handleSell(
                        event.getIdempotencyKey(),
                        event.getUserId(),
                        event.getStockId(),
                        event.getQuantity()
                );
            }
            resultEvent.setSuccess(true);
            log.info("Successfully processed TradeEvent for user: {}", event.getUserId());
        } catch (Exception e) {
            log.error("Error processing TradeEvent: {}", e.getMessage());
            resultEvent.setSuccess(false);
            resultEvent.setErrorMessage(e.getMessage());
        }

        log.info("Publishing TradeResultEvent: {}", resultEvent);
        kafkaTemplate.send("trade-responses-topic", resultEvent);
    }
}
