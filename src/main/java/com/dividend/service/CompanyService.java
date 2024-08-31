package com.dividend.service;

import com.dividend.exception.impl.FailedToScrapTickerException;
import com.dividend.exception.impl.NoCompanyException;
import com.dividend.model.Company;
import com.dividend.model.ScrapedResult;
import com.dividend.persist.CompanyRepository;
import com.dividend.persist.DividendRepository;
import com.dividend.persist.entity.CompanyEntity;
import com.dividend.persist.entity.DividendEntity;
import com.dividend.scraper.Scraper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
//@AllArgsConstructor // 이거사용하려면 this 써야하나 ?
@RequiredArgsConstructor
public class CompanyService {

    private final Trie<String, String> trie;
    private final Scraper yahooFinanceScraper;
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker) {
        if (companyRepository.existsByTicker(ticker)) {
            throw new RuntimeException("Already exists ticker -> " + ticker);
        }
        return storeCompanyAndDividend(ticker);
    }

    public Page<CompanyEntity> getAllCompany(Pageable pageable) {
        return companyRepository.findAll(pageable);
    }

    private Company storeCompanyAndDividend(String ticker) {
        // ticker 를 기준으로 회사를 스크래핑
        Company company = yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if (ObjectUtils.isEmpty(company)) {
            throw new FailedToScrapTickerException(ticker);
        }

        // 해당 회사가 존재할 경우, 회사의 배당금 정보를 스크래핑
        ScrapedResult scrapedResult =
                yahooFinanceScraper.scrapCompanyDividend(company);

        // 스크래핑 결과 저장
        CompanyEntity companyEntity = companyRepository.save(
                CompanyEntity.builder()
                        .ticker(company.getTicker())
                        .name(company.getName())
                        .build()
        );

        List<DividendEntity> dividendEntityList =
                scrapedResult.getDividendEntities().stream()
                        .map(dividend -> DividendEntity.builder()
                                .companyId(companyEntity.getId())
                                .dateTime(dividend.getDate())
                                .dividend(dividend.getDividend())
                                .build())
                        .collect(Collectors.toList());

        dividendRepository.saveAll(dividendEntityList);
        return company;
    }

    public void addAutoCompleteKeyword(String keyword) {
        trie.put(keyword, null);
    }

    public List<String> autoComplete(String keyword) {
        return trie.prefixMap(keyword).keySet()
                .stream().collect(Collectors.toList());
    }

    public String deleteCompany(String ticker) {
        CompanyEntity company = companyRepository.findByTicker(ticker)
                .orElseThrow(NoCompanyException::new);

        dividendRepository.deleteAllByCompanyId(company.getId());
        companyRepository.delete(company);

        deleteAutoCompleteKeyword(company.getName());

        return company.getName();
    }

    public void deleteAutoCompleteKeyword(String keyword) {
        trie.remove(keyword);
    }

    public List<String> getCompanyNamesByKeyword(String keyword) {
        Pageable limit = PageRequest.of(0, 10);
        Page<CompanyEntity> companyEntityList =
                companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);

        return companyEntityList.stream()
                .map(entity -> entity.getName())
                .collect(Collectors.toList());
    }
}
