package com.financialjuice.unusualactivity.repository;

import com.financialjuice.unusualactivity.dto.StockDataDTO;
import com.financialjuice.unusualactivity.model.StockData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
public class StockDataDTORepositoryTest {

    @Autowired
    StockDataDTORepository stockDataDTORepository;

    StockData expected , expected2;

    @Autowired
    SymbolRepository symbolRepository;

    @Before
    public void setUp() throws Exception {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        expected = new StockData(java.sql.Date.valueOf(today), "TEST", 1.0, 1.1, 1.2, 0.9, 1000L);
        expected2 = new StockData(java.sql.Date.valueOf(yesterday), "TEST", 1.0, 1.1, 1.2, 0.9, 1000L);
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void findAllByDateOrderBySymbol() {

        Date date = Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        StockDataDTORepository repository = new StockDataDTORepository();
        List<StockDataDTO> s = repository.findHistoricStockData(date);
        System.out.println(s);
    }

    @Test
    public void findAllByDateAfterOrderByDate() {
        Date date = Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        StockDataDTORepository repository = new StockDataDTORepository();
        List<StockDataDTO> s = repository.findHistoricStockData(date);
        System.out.println(s);
    }
}