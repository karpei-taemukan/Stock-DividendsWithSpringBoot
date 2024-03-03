package com.zerobase.service;

import com.zerobase.exception.implement.NoCompanyException;
import com.zerobase.model.Company;
import com.zerobase.model.ScrapedResult;
import com.zerobase.persist.CompanyRepository;
import com.zerobase.persist.DividendRepository;
import com.zerobase.persist.entity.CompanyEntity;
import com.zerobase.persist.entity.DividendEntity;
import com.zerobase.scraper.Scrapper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class CompanyService {

    private final Scrapper yahooFicnanceScrapper;
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    private final Trie trie;


    public Company save(String ticker) {
        boolean exists = companyRepository.existsByTicker(ticker);

        if (exists) {
            throw new RuntimeException("already exists ticker -> " + ticker);
        }

        return this.storeCompanyAndDividend(ticker);
    }

    private Company storeCompanyAndDividend(String ticker) {
        // ticker 를 기준으로 회사를 스크래핑
        Company company = this.yahooFicnanceScrapper.scrapCompanyByTicker(ticker);

        if (ObjectUtils.isEmpty(company)) {
            throw new RuntimeException("Failed to scrap ticker -> " + ticker);
        }

        // 해당 회사가 존재할 경우, 회사의 배당금 정보를 스크래핑
        ScrapedResult scrapedResult = this.yahooFicnanceScrapper.scrap(company);

        // 스크래핑 결과
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));

        List<DividendEntity> dividendEntities = scrapedResult.getDividends().stream()
                .map(e -> new DividendEntity(companyEntity.getId(), e))
                .toList();

        this.dividendRepository.saveAll(dividendEntities);

        return company;
    }
    //#######################################################################################################

    public Page<CompanyEntity> getAllCompany(Pageable pageable) {
        return this.companyRepository.findAll(pageable);
    }

    //#######################################################################################################

    public void addAutocompleteKeyword(String keyword) {
        this.trie.put(keyword, null);
    }

    public List<String> autocomplete(String keyword) {
        return (List<String>) this.trie.prefixMap(keyword).keySet()
                .stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    public void deleteAutocompleteKeyword(String keyword) {
        this.trie.remove(keyword);
    }


    //#######################################################################################################


    public List<String> getCompanyNamesByKeyword(String keyword) {

        Pageable limit = PageRequest.of(0, 10);

        Page<CompanyEntity> companyEntities =
                this.companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);

        return companyEntities.stream()
                .map(e -> e.getName())
                .collect(Collectors.toList());
    }
    //#######################################################################################################
    public String deleteCompany(String ticker) {
        // 삭제할 회사명 찾기
        CompanyEntity companyEntity = this.companyRepository.findByTicker(ticker)
                .orElseThrow(NoCompanyException::new);

        // 회사ID로 해당 회사의 배당금 삭제
        this.dividendRepository.deleteAllByCompanyId(companyEntity.getId());

        // 회사 정보 삭제
        this.companyRepository.delete(companyEntity);

        // Trie 에 있는 삭제한 회사 키워드 삭제
        this.deleteAutocompleteKeyword(companyEntity.getName());

        return companyEntity.getName();
    }
}