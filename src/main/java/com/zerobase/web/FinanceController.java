package com.zerobase.web;

import com.zerobase.model.ScrapedResult;
import com.zerobase.service.FinanceService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/finance")
public class FinanceController {
    private final FinanceService financeService;
    @GetMapping("/dividend/{companyName}")
    public ResponseEntity<?> searchFinance(
            @PathVariable(name = "companyName") String companyName
    ){
        ScrapedResult dividendByCompanyName
                = this.financeService.getDividendByCompanyName(companyName);
        return ResponseEntity.ok(dividendByCompanyName);
    }
}
