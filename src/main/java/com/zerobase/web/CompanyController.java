package com.zerobase.web;

import com.zerobase.model.Company;
import com.zerobase.model.constants.CacheKey;
import com.zerobase.persist.entity.CompanyEntity;
import com.zerobase.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/company")
@AllArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    private final CacheManager redisCacheManager;

    @GetMapping("/autocomplete")
    public ResponseEntity<?> autocomplete(
            @RequestParam String keyword
    ){
       // List<String> autocomplete
        // = this.companyService.autocomplete(keyword); -- trie 구현 자동완성
        List<String> autocomplete
                = this.companyService.getCompanyNamesByKeyword(keyword);
        return ResponseEntity.ok(autocomplete);
    }


    @GetMapping
    @PreAuthorize("hasRole('READ')") // 읽기 권한이 있어야 이 API 호출 가능
    public ResponseEntity<?> searchCompany(
            final Pageable pageable
    ){
        Page<CompanyEntity> companyEntities = companyService.getAllCompany(pageable);
        return ResponseEntity.ok(companyEntities);
    }

    @PostMapping
    @PreAuthorize("hasRole('WRITE')") // 쓰기 권한이 있어야 이 API 호출 가능
    public ResponseEntity<?> addCompany(
            @RequestBody Company company
    ){
        String ticker = company.getTicker().trim();

        if(ObjectUtils.isEmpty(ticker)){
            throw new RuntimeException("ticker is empty");
        }
        Company save = this.companyService.save(ticker);

        this.companyService.addAutocompleteKeyword(save.getName());

        return ResponseEntity.ok(save);
    }


    @DeleteMapping("/{ticker}")
    @PreAuthorize("hasRole('WRITE')")// 쓰기 권한이 있어야 이 API 호출 가능
    public ResponseEntity<?> deleteCompany(
            @PathVariable String ticker
    ){
        String companyName = this.companyService.deleteCompany(ticker);
        this.clearFinanceCache(companyName);

        return ResponseEntity.ok(companyName); // 삭제한 회사이름 리턴
    }

    // 캐쉬에서도 삭제한 회사 정보 삭제
    public void clearFinanceCache(String companyName){
        Objects.requireNonNull(this.redisCacheManager.getCache(CacheKey.KEY_FINANCE)).evict(companyName);
    }
}
