package com.zerobase.scheduler;

import com.zerobase.model.Company;
import com.zerobase.model.ScrapedResult;
import com.zerobase.model.constants.CacheKey;
import com.zerobase.persist.CompanyRepository;
import com.zerobase.persist.DividendRepository;
import com.zerobase.persist.entity.CompanyEntity;
import com.zerobase.persist.entity.DividendEntity;
import com.zerobase.scraper.Scrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
@EnableCaching
public class ScraperScheduler {
   /* @Scheduled(cron = "0/5 * * * * *")
    public void test(){
        System.out.println("SCHEDULER TEST");
    }*/

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    private final Scrapper yahooFinanceScraper;


    //###########################################################################
    // 쓰레드 풀 예시 코드

/*    @Scheduled(fixedDelay = 1000)
    public void test1() throws InterruptedException{
        Thread.sleep(10000); // 10초 간 일시 정지
        System.out.println(Thread.currentThread().getName() + " -> 테스트 1 : " + LocalDateTime.now());
    }

    @Scheduled(fixedDelay = 1000)
    public void test2() throws InterruptedException{
        System.out.println(Thread.currentThread().getName()+ " -> 테스트 2 : " +LocalDateTime.now());
    }*/

    // -> 예상 : test1 1번 실행, test2 10번 실행
    // -> 실제 : 10초에 test1, test2 같이 실행됨 ==> scheduler 가 1개의 쓰레드로 동작하기 때문

    // -> 해결 : Thread pool(여러개의 쓰레드 유지/관리) 필요


    /*      Thread pool
    *
    *   설정된 크기의 쓰레드를 만들어놓고 해당 쓰레드를 재사용할 수 있도록 관리
    *
    *   Thread를 불필요하게 많이 생성하면 메모리 낭비
    *   Thread를 적게 생성하면 효율성 떨어짐
    *
    *  --> Thread Pool 에 적정 사이즈가 필요
    *
    *       CPU 처리가 많은 경우 --> CPU 코어 갯수 + 1
    *       I/O 작업(blocking 이 많은 작업)이 많은 경우 --> CPU 코어 갯수 * 2
    *
     * */

    //###########################################################################

    // 일정 주기마다 수행 --> 새로 스크래핑할때 이미 스크래핑한 정보와 중복되지 않도록 유니크 키 설정
    @Scheduled(cron = "${scheduler.scrap.yahoo}")

    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true) // Scheduler 가 동작할때 같이 실행
    // redis 캐쉬에 있는 finance 에 해당하는 데이터는 모두 삭제
    // 특정 키에 해당하는 데이터를 삭제하고 싶다면, key="~~" 에 작성
    public void yahooFinanceScheduling() {
        // 저장된 회사 목록 저장

       log.info("Scraping is scheduler is started");
        List<CompanyEntity> companies = companyRepository.findAll();



        // 회사마다 배당금 정보를 새로 스크래핑

        for (CompanyEntity c : companies) { // 스크래핑하는 사이트에 부하가 갈 수 있음

            log.info("Scraping is scheduler is started -> " +c.getName());

            ScrapedResult scrapResult = this.yahooFinanceScraper.scrap(Company.builder()
                    .ticker(c.getTicker())
                    .name(c.getName())
                    .build());




            // 스크래핑된 배당금 정보 중 데이터베이스에 없는 값은 저장

            scrapResult.getDividends().stream()
                    // Dividend 모델을 Dividend Entity 로 바꿈
                    .map(e -> new DividendEntity(c.getId(), e))
                    // Dividend 모델을 Dividend Entity 로 하나씩 맵핑
                    .forEach(
                            e -> {
                                boolean exists = this.dividendRepository
                                        .existsByCompanyIdAndDate(e.getCompanyId(),
                                                e.getDate());
                                if(!exists){
                                    dividendRepository.save(e);
                                    log.info("insert new dividend -> "+e.toString());
                                }
                            }
                    );



            // 연속적으로 스크래핑 대상 사이트 서버에 요청을 하지 않도록 함
            try {
                Thread.sleep(3000);

                /*
                *       sleep() VS wait()
                *
                *       sleep() : 설정한 시간 후 자동으로 작업을 다시 실행
                *
                *       wait() : 스레드를 대기 상태에 빠뜨림
                *       notify() 나 notifyAll() 메소드를 호출할 때까지 자동으로 깨지 않음
                *
                * */
            }
            catch (InterruptedException e){
                // InterruptedException : 인터럽트를 받는 스레드가 blocking(방해) 될 수 있는 메소드를 실행 할때 발생
                Thread.currentThread().interrupt();
            }
        }


    }
}