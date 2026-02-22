package com.spring.tradexportfolioservice.Service;

import com.spring.tradexportfolioservice.Enums.IdempotencyStatus;
import com.spring.tradexportfolioservice.Models.IdempotencyKey;
import com.spring.tradexportfolioservice.Models.Portfolio;
import com.spring.tradexportfolioservice.Repository.PortfolioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final IdempotencyService idempotencyService;

    @Transactional
    public void handleBuy(String idempotencyKeyStr, Long userId, Long stockId, Integer quantity, BigDecimal price) {

        IdempotencyKey key = idempotencyService.createOrReturnKey(idempotencyKeyStr, userId);
        if (key != null && key.getStatus() == IdempotencyStatus.COMPLETED) {
            return;
        }

        try {
            Portfolio portfolio = portfolioRepository
                    .findByUserIdAndStockId(userId, stockId)
                    .orElse(Portfolio.createEmptyPortfolio(userId, stockId));

            portfolio.addHoldings(quantity, price);

            portfolioRepository.save(portfolio);

            idempotencyService.markCompleted(idempotencyKeyStr);

        } catch (Exception e) {
            idempotencyService.markFailed(idempotencyKeyStr);
            throw e;
        }
    }

    @Transactional
    public void handleSell(String idempotencyKeyStr, Long userId, Long stockId, Integer quantity) {

        IdempotencyKey key = idempotencyService.createOrReturnKey(idempotencyKeyStr, userId);
        if (key != null && key.getStatus() == IdempotencyStatus.COMPLETED) {
            return;
        }

        try {
            Portfolio portfolio = portfolioRepository
                    .findByUserIdAndStockId(userId, stockId)
                    .orElseThrow(() -> new IllegalStateException("You do not own this portfolio"));

            portfolio.removeHoldings(quantity);

            portfolioRepository.save(portfolio);

            idempotencyService.markCompleted(idempotencyKeyStr);

        } catch (Exception e) {
            idempotencyService.markFailed(idempotencyKeyStr);
            throw e;
        }
    }

    @Transactional
    public void rollbackBuy(String idempotencyKeyStr, Long userId, Long stockId, Integer quantity, BigDecimal price) {

        String rollbackKey = idempotencyKeyStr + "-rollback";
        IdempotencyKey key = idempotencyService.createOrReturnKey(rollbackKey, userId);
        if (key != null && key.getStatus() == IdempotencyStatus.COMPLETED) {
            return;
        }

        try {
            Portfolio portfolio = portfolioRepository
                    .findByUserIdAndStockId(userId, stockId)
                    .orElseThrow(() -> new IllegalStateException("Portfolio not found for rollback"));

            portfolio.removeHoldings(quantity);

            portfolioRepository.save(portfolio);
            idempotencyService.markCompleted(rollbackKey);
        } catch (Exception e) {
            idempotencyService.markFailed(rollbackKey);
            throw e;
        }
    }

    @Transactional
    public void rollbackSell(String idempotencyKeyStr, Long userId, Long stockId, Integer quantity) {
        String rollbackKey = idempotencyKeyStr + "-rollback";
        IdempotencyKey key = idempotencyService.createOrReturnKey(rollbackKey, userId);
        if (key != null && key.getStatus() == IdempotencyStatus.COMPLETED) {
            return;
        }

        try {
            Portfolio portfolio = portfolioRepository
                    .findByUserIdAndStockId(userId, stockId)
                    .orElse(Portfolio.createEmptyPortfolio(userId, stockId));

            portfolio.addHoldings(quantity, BigDecimal.ZERO);

            portfolioRepository.save(portfolio);
            idempotencyService.markCompleted(rollbackKey);
        } catch (Exception e) {
            idempotencyService.markFailed(rollbackKey);
            throw e;
        }
    }
}
