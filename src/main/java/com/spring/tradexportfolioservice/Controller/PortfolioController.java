package com.spring.tradexportfolioservice.Controller;

import com.spring.tradexportfolioservice.DTO.PortfolioRequest;
import com.spring.tradexportfolioservice.Service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/internal/protfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @PostMapping("/buy")
    public void handleBuy(@RequestBody PortfolioRequest request){
        portfolioService.handleBuy(
                request.getUserId(),
                request.getStockId(),
                request.getQuantity(),
                request.getPrice()
        );
    }

    @PostMapping("/sell")
    public void handleSell(@RequestBody PortfolioRequest request){
        portfolioService.handleSell(
                request.getUserId(),
                request.getStockId(),
                request.getQuantity()
        );
    }
}
