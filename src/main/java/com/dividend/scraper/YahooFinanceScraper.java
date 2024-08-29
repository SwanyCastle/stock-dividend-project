package com.dividend.scraper;

import com.dividend.model.Company;
import com.dividend.model.Dividend;
import com.dividend.model.ScrapedResult;
import com.dividend.model.constants.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scraper {
    private static final Long START_TIME = 86400L;  // 60 * 60 * 24
    private static final String STATISTICS_URL = "https://finance.yahoo.com/quote/%s/history/?frequency=1mo&period1=%d&period2=%d";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s";

    @Override
    public ScrapedResult scrapCompanyDividend(Company company) {
        ScrapedResult scrapedResult = new ScrapedResult();
        scrapedResult.setCompany(company);
        try {
            long now = System.currentTimeMillis() / 1000;

            String url = String.format(STATISTICS_URL, company.getTicker(), START_TIME, now);
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();

            Elements parsedDivs = document.getElementsByClass("table yf-ewueuo");
            Element tableElement = parsedDivs.get(0);

            Element tbody = tableElement.children().get(1);

            List<Dividend> dividendList = new ArrayList<>();
            for (Element e : tbody.children()) {
                String txt = e.text();
                if (!txt.endsWith("Dividend")) {
                    continue;
                }

                String[] splits = txt.split(" ");
                int month = Month.strToNumber(splits[0]);
                int day = Integer.parseInt(splits[1].replace(",", ""));
                int year = Integer.parseInt(splits[2]);
                String dividend = splits[3];

                if (month < 0) {
                    throw new RuntimeException("Unexpected Month enum value -> " + splits[0]);
                }

                dividendList.add(Dividend.builder()
                        .date(LocalDateTime.of(year, month, day, 0, 0))
                        .dividend(dividend)
                        .build());
            }
            scrapedResult.setDividendEntities(dividendList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scrapedResult;
    }

    @Override
    public Company scrapCompanyByTicker(String ticker) {
        try {
            String url = String.format(SUMMARY_URL, ticker);
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();

            Elements parsedSection = document.getElementsByClass("container yf-3a2v0c paddingRight");
            Element h1Element = parsedSection.get(0);

            String companyName = h1Element.text().split("[()]")[0].trim();

            return Company.builder()
                    .ticker(ticker)
                    .name(companyName)
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
