package com.dividend.web;

import com.dividend.model.Company;
import com.dividend.persist.entity.CompanyEntity;
import com.dividend.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/company")
public class CompanyController {
    private final CompanyService companyService;

    @GetMapping("/auto-complete")
    public ResponseEntity<?> autoComplete(@RequestParam String keyword) {
        return null;
    }

    /**
     * 회사 정보 전체 조회
     * @return ResponseEntity<List<CompanyEntity>>
     *     회사 티커 목록 : O, MMM, NKE, COKE, AAPL, QQQ, SPY, DIA
     */
    @GetMapping
    public ResponseEntity<Page<CompanyEntity>> searchComapny(final Pageable pageable) {
        return ResponseEntity.ok(companyService.getAllCompany(pageable));
    }

    /**
     * 회사 및 배당금 정보 저장
     * @param companyRequest
     * @return ResponseEntity<Company>
     */
    @PostMapping
    public ResponseEntity<Company> addCompany(
            @RequestBody Company companyRequest
    ) {
        String ticker = companyRequest.getTicker().trim();
        if (ObjectUtils.isEmpty(ticker)) {
            throw new RuntimeException("Ticker is empty");
        }

        Company savedCompany = companyService.save(ticker);
        return ResponseEntity.ok(savedCompany);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteCompany() {
        return null;
    }
}
