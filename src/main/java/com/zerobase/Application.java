package com.zerobase;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class Application {

    public static void main(String[] args) {
       SpringApplication.run(Application.class, args);
        System.out.println("Main -> "+Thread.currentThread().getName());
/*
        String s1 = "Hello %s";
        String[] s2 = {"A","B","C"};

        for (String s3 : s2){
            System.out.println(String.format(s1,s3));
        }*/

      //  System.out.println(System.currentTimeMillis());

     /*   Scrapper scraper = new YahooFinanceScraper();

   *//*     ScrapedResult o = scraper.scrap(Company.builder()
                .ticker("o")
                .build());

        System.out.println(o);*//*

        Company o1 = scraper.scrapCompanyByTicker("COKE");
        System.out.println(o1);*/

   /*     Trie trie = new PatriciaTrie();

        AutoComplete autoComplete = new AutoComplete(trie);
        AutoComplete autoComplete1 = new AutoComplete(trie);

        autoComplete.add("hello");

        System.out.println(autoComplete.get("hello"));
        System.out.println(autoComplete1.get("hello"));*/


    /*    for (int i = 0; i < 10; i++) {
            System.out.println("Hello -> " + i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/



    }
}