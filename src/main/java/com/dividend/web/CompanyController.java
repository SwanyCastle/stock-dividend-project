package com.dividend.web;

import com.dividend.model.Company;
import com.dividend.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/company")
public class CompanyController {
    private final CompanyService companyService;

    @GetMapping("/auto-complete")
    public ResponseEntity<?> autoComplete(@RequestParam String keyword) {
        return null;
    }

    @GetMapping
    public ResponseEntity<?> searchComapny() {
        return null;
    }

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
