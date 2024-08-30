package com.dividend.scheduler;

import com.dividend.model.Company;
import com.dividend.model.Dividend;
import com.dividend.model.ScrapedResult;
import com.dividend.persist.CompanyRepository;
import com.dividend.persist.DividendRepository;
import com.dividend.persist.entity.CompanyEntity;
import com.dividend.persist.entity.DividendEntity;
import com.dividend.scraper.Scraper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final Scraper yahooFinanceScraper;
    private final DividendRepository dividendRepository;

    // ThreadPool 테스트
    @Scheduled(fixedDelay = 1000)
    public void test1() throws InterruptedException {
        Thread.sleep(10000);
        System.out.println(Thread.currentThread().getName() + " -> Test 1 : " + LocalDateTime.now());
    }

    @Scheduled(fixedDelay = 1000)
    public void test2() throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " -> Test 2 : " + LocalDateTime.now());
    }

//    @Scheduled(cron = "${scheduler.scrap.yahoo}")
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
