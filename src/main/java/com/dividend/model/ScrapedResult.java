package com.dividend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ScrapedResult {

    private Company company;
    private List<Dividend> dividendEntities;

    public ScrapedResult() {
        this.dividendEntities = new ArrayList<>();
    }
}
