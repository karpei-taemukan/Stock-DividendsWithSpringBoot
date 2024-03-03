package com.zerobase.service;

import com.zerobase.exception.implement.NoCompanyException;
import com.zerobase.model.Company;
import com.zerobase.model.Dividend;
import com.zerobase.model.ScrapedResult;
import com.zerobase.model.constants.CacheKey;
import com.zerobase.persist.CompanyRepository;
import com.zerobase.persist.DividendRepository;
import com.zerobase.persist.entity.CompanyEntity;
import com.zerobase.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class FinanceService {
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    @Cacheable(value = CacheKey.KEY_FINANCE, key = "#companyName")
    /*
    *   @Cacheable
    *
    *  -> cache 에 데이터가 없는 경우: 메소드를 실행(DB에 저장하는 과정)후 리턴 값을 cache 에 추가
    *
    *   -> cache 에 데이터가 있는 경우: 메소드를 실행(DB에 저장하는 과정) 하지 않고 cache 의 값을 반환
    * */
    public ScrapedResult getDividendByCompanyName(String companyName){

        log.info("search company -> "+ companyName);

        // 1. 회사명 기준으로 회사정보 조회
        CompanyEntity company
                = this.companyRepository.findByName(companyName)
                .orElseThrow(NoCompanyException::new);


        // 2. 조회된 회사 ID 로 배당금 정보 조회
        List<DividendEntity> dividendEntities
                = this.dividendRepository.findAllByCompanyId(company.getId());

        // 3. 결과 조합 후 변환

       /* List<Dividend> dividends = new ArrayList<>();

        for (DividendEntity d : dividendEntities){
            dividends.add(Dividend.builder()
                            .dividend(d.getDividend())
                            .date(d.getDate())
                    .build());
        }*/

        List<Dividend> dividends = dividendEntities.stream()
                .map(d -> Dividend.builder()
                        .date(d.getDate())
                        .dividend(d.getDividend())
                        .build())
                .collect(Collectors.toList());

        return new ScrapedResult(Company.builder()
                .ticker(company.getTicker())
                .name(company.getName())
                .build(), dividends);
    }
}
