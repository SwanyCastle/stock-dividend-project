package com.dividend.scraper;

import com.dividend.model.Company;
import com.dividend.model.ScrapedResult;

public interface Scraper {
    Company scrapCompanyByTicker(String ticker);

    ScrapedResult scrapCompanyDividend(Company company);
}
