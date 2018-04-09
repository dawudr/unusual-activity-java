package com.financialjuice.unusualactivity.repository;

import com.financialjuice.unusualactivity.dto.StockDataDTO;
import com.financialjuice.unusualactivity.model.StockData;
import com.financialjuice.unusualactivity.model.SymbolData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class StockDataRepositoryTest {

    @Autowired
    StockDataRepository stockDataRepository;

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
    public void updateStock() {

        stockDataRepository.save(expected);
        List<StockData> l = stockDataRepository.findAllBySymbol(expected.getSymbol());
        StockData actual = l.get(0);
        System.out.println("ACTUAL:" + actual.toString());
        assertEquals(expected, actual);

        // Tear down
        stockDataRepository.delete(actual.getId());
    }

    @Test
    public void getLastUpdated() {

        Date actual_no_data = stockDataRepository.getLastUpdated("TEST");
        System.out.println("ACTUAL:" + actual_no_data);
        assertEquals(null, actual_no_data);

        stockDataRepository.save(expected);
        List<StockData> l = stockDataRepository.findAllBySymbol(expected.getSymbol());
        StockData s = l.get(0);

        Date actual = stockDataRepository.getLastUpdated("TEST");
        System.out.println("ACTUAL:" + actual);
        assertEquals(s.getDate(), actual);

        // Tear down
        stockDataRepository.delete(s.getId());

    }

    @Test
    public void findAllBySymbol() {

        stockDataRepository.save(expected);
        stockDataRepository.save(expected2);

        List<StockData> l = stockDataRepository.findAllBySymbol("TEST");
        l.forEach(System.out::println);
        assertEquals(2, l.size());
        StockData actual = l.get(0);
        StockData actual2 = l.get(1);
        assertEquals(expected, actual);
        assertEquals(expected2, actual2);

        // Tear down
        stockDataRepository.delete(actual.getId());
        stockDataRepository.delete(actual2.getId());

    }

    @Test
    public void findAllBySymbolAfterAndDate() {

        LocalDate today = LocalDate.now();

        // Setup
        SymbolData symbolData = new SymbolData(
                "TEST",
                "TEST1 Name",
                "ORD",
                "LSE",
                "MAIN MARKET",
                "Equities",
                "Test Industry",
                "Test Sector",
                java.sql.Date.valueOf(today.minusDays(1)).toString(),
                "United Kingdom",
                "GBX",
                "£40.50",
                "Standard Shares"
        );
        symbolRepository.save(symbolData);

        for(int i = 0; i < 100; i++) {
            StockData expected = new StockData(
                    java.sql.Date.valueOf(today.minusDays(i)),
                    "TEST",
                    new Integer(i).doubleValue() + 1.0,
                    new Integer(i).doubleValue() + 1.1,
                    new Integer(i).doubleValue() + 1.2,
                    new Integer(i).doubleValue() + 0.9,
                    new Integer(i) + 1000L);
//            expected.setSymbolData(symbolData);
            stockDataRepository.save(expected);
        }

        List<StockData> l = stockDataRepository.findAllByDateAfterOrderBySymbol(java.sql.Date.valueOf(today.minusMonths(1)));
        l.forEach(System.out::println);

        Duration duration = Duration.between(today.atStartOfDay(), today.minusMonths(1).atStartOfDay());
        long diff = Math.abs(duration.toDays());
        System.out.println(diff);

        System.out.println(l.size());
        assertEquals(diff, l.size());
    }


    // TODO: Fix me
    @Test
    public void findAllBySectorAndDateAfter() {

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Date todayDate = new Date();

        // Setup
        for(int i = 0; i < 10; i++) {
            SymbolData symbolData = new SymbolData(
                    "TEST" + i,
                    "TEST1 Name",
                    "ORD",
                    "LSE",
                    "MAIN MARKET",
                    "Equities",
                    "Test Industry",
                    "Test Sector",
                    todayDate.toString(),
                    "United Kingdom",
                    "GBX",
                    "£40.50",
                    "Standard Shares"
            );
            symbolRepository.save(symbolData);
        }

        for(int i = 0; i < 100; i++) {
            String postfix = String.valueOf(i % 10);
            StockData expected = new StockData(
                    java.sql.Date.valueOf(today.minusDays(i)),
                    "TEST" + postfix,
                    new Integer(i).doubleValue() + 1.0,
                    new Integer(i).doubleValue() + 1.1,
                    new Integer(i).doubleValue() + 1.2,
                    new Integer(i).doubleValue() + 0.9,
                    new Integer(i) + 1000L);

//            expected.setSymbolData(symbolRepository.findOne("TEST" + postfix));
            stockDataRepository.save(expected);
        }

        List<StockData> l = stockDataRepository.findAllBySectorAndDateAfter(java.sql.Date.valueOf(today.minusDays(1)));

//        l.forEach(System.out::println);

//        assertEquals(l.size(), 10);

    }


    @Test
    public void findTopByDate() {

        LocalDate today = LocalDate.now();


        for(int i = 0; i < 100; i++) {
            int postfix = i % 10;
            StockData expected = new StockData(
                    java.sql.Date.valueOf(today.minusDays(postfix)),
                    "TEST" + i,
                    new Integer(i).doubleValue() + 1.0,
                    new Integer(i).doubleValue() + 1.1,
                    new Integer(i).doubleValue() + 1.2,
                    new Integer(i).doubleValue() + 0.9,
                    new Integer(i) + 1000L);

//            expected.setSymbolData(symbolRepository.findOne("TEST" + postfix));
            stockDataRepository.save(expected);
        }

        List<StockData> l = stockDataRepository.findAllByDateAfterOrderBySymbol(java.sql.Date.valueOf(today.minusDays(1)));
        l.forEach(System.out::println);

        assertEquals(10, l.size());


    }



    @Test
    public void findAllByDateAfterOrderByDate() {

        LocalDate today = LocalDate.now();

        for(int i = 0; i < 100; i++) {
            int postfix = i % 10;
            StockData expected = new StockData(
                    java.sql.Date.valueOf(today.minusDays(postfix)),
                    "TEST" + i,
                    new Integer(i).doubleValue() + 1.0,
                    new Integer(i).doubleValue() + 1.1,
                    new Integer(i).doubleValue() + 1.2,
                    new Integer(i).doubleValue() + 0.9,
                    new Integer(i) + 1000L);

//            expected.setSymbolData(symbolRepository.findOne("TEST" + postfix));
            stockDataRepository.save(expected);
        }

        List<StockDataDTO> l = stockDataDTORepository.findHistoricStockData(java.sql.Date.valueOf(today.minusDays(1)));
        l.forEach(System.out::println);

        assertEquals(10, l.size());

    }


    @Test
    public void findAllByDateOrderByDate() {

        LocalDate today = LocalDate.now();

        for(int i = 0; i < 100; i++) {
            int postfix = i % 10;
            StockData expected = new StockData(
                    java.sql.Date.valueOf(today.minusDays(postfix)),
                    "TEST" + i,
                    new Integer(i).doubleValue() + 1.0,
                    new Integer(i).doubleValue() + 1.1,
                    new Integer(i).doubleValue() + 1.2,
                    new Integer(i).doubleValue() + 0.9,
                    new Integer(i) + 1000L);

//            expected.setSymbolData(symbolRepository.findOne("TEST" + postfix));
            stockDataRepository.save(expected);
        }

        List<StockDataDTO> l = stockDataDTORepository.findRealtimeStockData(java.sql.Date.valueOf(today.minusDays(1)));
        l.forEach(System.out::println);

        assertEquals(10, l.size());

    }
}