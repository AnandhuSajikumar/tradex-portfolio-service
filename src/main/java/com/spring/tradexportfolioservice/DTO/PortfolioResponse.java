package com.spring.tradexportfolioservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioResponse {
    private Long id;
    private Long stockId;
    private Integer quantity;
    private BigDecimal avgBuyPrice;
}
