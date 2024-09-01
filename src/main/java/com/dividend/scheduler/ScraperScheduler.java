package com.dividend.scheduler;

import com.dividend.model.Company;
import com.dividend.model.ScrapedResult;
import com.dividend.persist.CompanyRepository;
import com.dividend.persist.DividendRepository;
import com.dividend.persist.entity.CompanyEntity;
import com.dividend.persist.entity.DividendEntity;
import com.dividend.scraper.Scraper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.dividend.model.constants.CacheKey.KEY_FINANCE;

@Slf4j
@Component
@EnableCaching
@RequiredArgsConstructor
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final Scraper yahooFinanceScraper;
    private final DividendRepository dividendRepository;

    // ThreadPool 테스트
//    @Scheduled(fixedDelay = 1000)
//    public void test1() throws InterruptedException {
//        Thread.sleep(10000);
//        System.out.println(Thread.currentThread().getName() + " -> Test 1 : " + LocalDateTime.now());
//    }
//
//    @Scheduled(fixedDelay = 1000)
//    public void test2() throws InterruptedException {
//        System.out.println(Thread.currentThread().getName() + " -> Test 2 : " + LocalDateTime.now());
//    }

    // 이렇게 Scheduler 에다가 CacheEvict 를 적용하게 되면 Scheduler 가 동작할 때 마다
    // Cache 에 있는 데이터들이 지워지게 된다. 이후에 회사 배당금을 조회하는 시점에 다시 캐시에 저장되게 된다
    // redis 에 있는 key 중 finance 로 시작하는 키는 모두 비워준다는 의미
    // @CacheEvict(value = "finance", key = "3M Company") 이런식으로 하면 특정 키만 지워준다
    @CacheEvict(value = KEY_FINANCE, allEntries = true)
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScrapSchedule() {
        // 저장된 회사 목록을 조회
        List<CompanyEntity> companyEntityList = companyRepository.findAll();

        // 회사마다 배당금 정보를 새로 스크래핑
        for (CompanyEntity companyEntity : companyEntityList) {
            log.info("scraping scheduler is started -> " + companyEntity.getName());
            ScrapedResult scrapedResult = yahooFinanceScraper.scrapCompanyDividend(
                    Company.builder()
                            .name(companyEntity.getName())
                            .ticker(companyEntity.getTicker())
                            .build()
            );

            // 스크래핑한 배당금 정보 중 DB 에 없는 값은 저장
            scrapedResult.getDividendEntities().stream()
                    // Dividend 모델을 DividendEntity 로 맵핑
                    .map(entity -> new DividendEntity().builder()
                            .companyId(companyEntity.getId())
                            .dateTime(entity.getDate())
                            .dividend(entity.getDividend())
                            .build())
                    // Element 를 하나씩 DividendRepository 에 삽입 (존재하지 않는 것들만)
                    .forEach(entity -> {
                        boolean exists = dividendRepository.existsByCompanyIdAndDateTime(
                                entity.getCompanyId(), entity.getDateTime());

                        if (!exists) {
                            dividendRepository.save(entity);
                            log.info("새로운 배당금 정보를 저장했습니다 -> " + entity.toString());
                        }
                    });

            // 연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시 정지
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }
}
