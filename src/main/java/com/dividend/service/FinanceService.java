package com.dividend.service;

import com.dividend.model.Company;
import com.dividend.model.Dividend;
import com.dividend.model.ScrapedResult;
import com.dividend.persist.CompanyRepository;
import com.dividend.persist.DividendRepository;
import com.dividend.persist.entity.CompanyEntity;
import com.dividend.persist.entity.DividendEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public ScrapedResult getDividendByCompanyName(String companyName) {
        // 1. 회사명을 기준으로 회사 정보를 조회
        CompanyEntity companyEntity = companyRepository.findByName(companyName)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회사명입니다."));

        // 2. 조회된 회사 id 로 배당금을 조회
        List<DividendEntity> dividendEntityList =
                dividendRepository.findAllByCompanyId(companyEntity.getId());

        // 3. 결과 조합후 반환
        List<Dividend> dividendList = dividendEntityList.stream()
                .map(dividendEntity -> Dividend.builder()
                        .date(dividendEntity.getDateTime())
                        .dividend(dividendEntity.getDividend())
                        .build())
                .collect(Collectors.toList());

        return ScrapedResult.builder()
                .company(Company.builder()
                        .ticker(companyEntity.getTicker())
                        .name(companyEntity.getName())
                        .build())
                .dividendEntities(dividendList)
                .build();
    }
}
