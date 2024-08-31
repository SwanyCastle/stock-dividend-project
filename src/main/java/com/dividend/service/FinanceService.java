package com.dividend.service;

import com.dividend.exception.impl.NoCompanyException;
import com.dividend.model.Company;
import com.dividend.model.Dividend;
import com.dividend.model.ScrapedResult;
import com.dividend.persist.CompanyRepository;
import com.dividend.persist.DividendRepository;
import com.dividend.persist.entity.CompanyEntity;
import com.dividend.persist.entity.DividendEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.dividend.model.constants.CacheKey.KEY_FINANCE;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    // 요청이 자주 들어오는가 ? -> 요청이 자주 들어오게된다면 캐싱을 했을 때 효율적이다.
    // 자주 변경되는 데이터 인가 ? -> 데이터가 자주 변경되는데 캐싱을 한다면 효율적이지 못하다.
    @Cacheable(key = "#companyName", value = KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName) {
        log.info("search company -> " + companyName);

        // 1. 회사명을 기준으로 회사 정보를 조회
        CompanyEntity companyEntity = companyRepository.findByName(companyName)
                .orElseThrow(NoCompanyException::new);

        // 2. 조회된 회사 id 로 배당금을 조회
        List<DividendEntity> dividendEntityList =
                dividendRepository.findAllByCompanyId(companyEntity.getId());

        // 3. 결과 조합후 반환
        return ScrapedResult.builder()
                .company(Company.builder()
                        .ticker(companyEntity.getTicker())
                        .name(companyEntity.getName())
                        .build()
                )
                .dividendEntities(dividendEntityList.stream()
                        .map(dividendEntity -> Dividend.builder()
                                .date(dividendEntity.getDateTime())
                                .dividend(dividendEntity.getDividend())
                                .build())
                        .collect(Collectors.toList())
                )
                .build();
    }
}
