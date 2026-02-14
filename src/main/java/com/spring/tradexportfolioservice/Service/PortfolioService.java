package com.spring.tradexportfolioservice.Service;

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

    @Transactional
    public void handleBuy(Long userId, Long stockId, Integer quantity, BigDecimal price) {

        Portfolio portfolio = portfolioRepository
                .findByUserIdAndStockId(userId, stockId)
                .orElse(Portfolio.createEmptyPortfolio(userId, stockId));

        portfolio.addHoldings(quantity, price);

        portfolioRepository.save(portfolio);
    }

    @Transactional
    public void handleSell(Long userId, Long stockId, Integer quantity) {

        Portfolio portfolio = portfolioRepository
                .findByUserIdAndStockId(userId, stockId)
                .orElseThrow(() -> new IllegalStateException("You do not own this portfolio"));

        portfolio.removeHoldings(quantity);

        portfolioRepository.save(portfolio);
    }
}
