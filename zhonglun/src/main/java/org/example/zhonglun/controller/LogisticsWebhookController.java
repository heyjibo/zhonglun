// src/main/java/org/example/zhonglun/controller/LogisticsWebhookController.java
package org.example.zhonglun.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.zhonglun.entity.Order;
import org.example.zhonglun.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/logistics/webhook")
public class LogisticsWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(LogisticsWebhookController.class);
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    public LogisticsWebhookController(OrderRepository orderRepository, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/kdn")
    @Transactional
    public ResponseEntity<Map<String, Object>> handleKuaidiNiaoCallback(@RequestParam("RequestData") String requestData) {
        logger.info("Received KDN Webhook with data: {}", requestData);

        Map<String, Object> responseBody = new HashMap<>();

        try {
            JsonNode rootNode = objectMapper.readTree(requestData);
            for (JsonNode dataNode : rootNode.get("Data")) {
                if(dataNode.get("Success").asBoolean()){
                    String logisticCode = dataNode.get("LogisticCode").asText();

                    Optional<Order> orderOpt = orderRepository.findByTrackingNumber(logisticCode);

                    if (orderOpt.isPresent()) {
                        Order order = orderOpt.get();
                        order.setLogisticsTrace(dataNode.toString());
                        orderRepository.save(order);
                        logger.info("Successfully updated logistics for order with tracking number: {}", logisticCode);
                    } else {
                        logger.warn("Received tracking for unknown order with LogisticCode: {}", logisticCode);
                    }
                } else {
                    logger.warn("Received a failed tracking update from KDN: {}", dataNode.toString());
                }
            }

            responseBody.put("EBusinessID", rootNode.get("EBusinessID").asText());
            responseBody.put("UpdateTime", Instant.now().toString());
            responseBody.put("Success", true);
            responseBody.put("Reason", "");

            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            logger.error("Error processing KDN webhook", e);
            responseBody.put("Success", false);
            responseBody.put("Reason", "Internal Server Error: " + e.getMessage());
            return ResponseEntity.status(500).body(responseBody);
        }
    }
}
