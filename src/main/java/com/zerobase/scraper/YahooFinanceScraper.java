package com.zerobase.scraper;

import com.zerobase.model.Company;
import com.zerobase.model.Dividend;
import com.zerobase.model.ScrapedResult;
import com.zerobase.model.constants.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scrapper {
    private static final String STATISTICS_URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";

    private static final long START_TIME = 86400; // 60 * 60 * 24 --> 초단위로 사용

    @Override
    public ScrapedResult scrap(Company company){

        var scrapedResult = new ScrapedResult();
        scrapedResult.setCompany(company);
        try{

            long end = System.currentTimeMillis() / 1000; // 초단위로 사용

            String url = String.format(STATISTICS_URL, company.getTicker(), START_TIME, end);

            Connection connect = Jsoup.connect(url);
            Document document = connect.get();

            Elements parsingDivs = document.getElementsByAttributeValue("data-test","historical-prices");

            Element tableEle = parsingDivs.get(0);

            Element tbody = tableEle.children().get(1);

            List<Dividend> dividends = new ArrayList<>();

            for(Element e : tbody.children()){
                String txt =  e.text();
                if(!txt.endsWith("Dividend")){
                    continue;
                }
//                System.out.println(txt);

                /*
                String[] splits = txt.split(" ");
                String month = splits[0];
                int day = Integer.parseInt(splits[1].replace(",",""));
                int year = Integer.parseInt(splits[2]);
                String dividend = splits[3];

                System.out.println(year+"/"+month+"/"+day+"/"+dividend);
                */

                String[] splits = txt.split(" ");
                int month = Month.strToNum(splits[0]);

                int day = Integer.parseInt(splits[1].replace(",",""));
                int year = Integer.parseInt(splits[2]);
                String dividend = splits[3];

                if(month < 0){
                    throw new RuntimeException("Unexpected Month enum value -> "+splits[0]);
                }

                dividends.add(Dividend.builder()
                        .date(LocalDateTime.of(year, month, day, 0,0))
                        .dividend(dividend)
                        .build());

            }
            scrapedResult.setDividends(dividends);
            //  System.out.println(element);
        }catch (IOException e){
            e.printStackTrace();
        }
        return scrapedResult;
    }




    @Override
    public Company scrapCompanyByTicker(String ticker) {
        String url = String.format(SUMMARY_URL, ticker, ticker);

        try {
            Document document = Jsoup.connect(url).get();
            Element titleEle = document.getElementsByTag("h1").getFirst();
            String title = titleEle.text().trim();

            return Company.builder()
                    .ticker(ticker)
                    .name(title)
                    .build();
        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }
}
