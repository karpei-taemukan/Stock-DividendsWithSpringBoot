package com.zerobase.scraper;

import com.zerobase.model.Company;
import com.zerobase.model.ScrapedResult;

public interface Scrapper {
    ScrapedResult scrap(Company company);
    Company scrapCompanyByTicker(String ticker);
}
