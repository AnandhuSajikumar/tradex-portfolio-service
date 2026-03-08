package com.spring.tradexportfolioservice.Controller;

import com.spring.tradexportfolioservice.DTO.PortfolioUpdateRequest;
import com.spring.tradexportfolioservice.Service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.spring.tradexportfolioservice.UserPrincipal;
import com.spring.tradexportfolioservice.DTO.PortfolioResponse;
import java.util.List;

@RestController
@RequestMapping("/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

        private final PortfolioService portfolioService;

        @PostMapping("/buy")
        public void handleBuy(
                        @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
                        @RequestBody PortfolioUpdateRequest request) {
                portfolioService.handleBuy(
                                idempotencyKey,
                                request.getUserId(),
                                request.getStockId(),
                                request.getQuantity(),
                                request.getPrice());
        }

        @PostMapping("/sell")
        public void handleSell(
                        @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
                        @RequestBody PortfolioUpdateRequest request) {
                portfolioService.handleSell(
                                idempotencyKey,
                                request.getUserId(),
                                request.getStockId(),
                                request.getQuantity());
        }

        @PostMapping("/rollback-buy")
        public void rollbackBuy(
                        @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
                        @RequestBody PortfolioUpdateRequest request) {
                portfolioService.rollbackBuy(
                                idempotencyKey,
                                request.getUserId(),
                                request.getStockId(),
                                request.getQuantity(),
                                request.getPrice());
        }

        @PostMapping("/rollback-sell")
        public void rollbackSell(
                        @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
                        @RequestBody PortfolioUpdateRequest request) {
                portfolioService.rollbackSell(
                                idempotencyKey,
                                request.getUserId(),
                                request.getStockId(),
                                request.getQuantity());
        }

        @GetMapping("/holdings")
        public List<PortfolioResponse> getHoldings(@AuthenticationPrincipal UserPrincipal userPrincipal) {
                return portfolioService.getUserHoldings(userPrincipal.getId());
        }
}
