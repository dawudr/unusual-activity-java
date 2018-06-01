package com.financialjuice.unusualactivity.repository;

import com.financialjuice.unusualactivity.Application;
import com.financialjuice.unusualactivity.model.StockData;
import com.financialjuice.unusualactivity.model.SymbolData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
//@DataJpaTest
//@ContextConfiguration(classes = {Application.class})
@SpringBootTest(properties = "/application-test.properties", classes = Application.class)
//@SpringBootTest(properties = "/application-test.properties", classes = { Application.class, StockDataRepository.class, StockCompositeKey.class})
//@ContextConfiguration
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
public class StockDataRepositoryTest {

    @Autowired
    StockDataRepository stockDataRepository;


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
    public void findTopBySymbolOrderByDateDesc() {

        LocalDateTime ldt_today = LocalDateTime.now();

        for(int i = 0; i < 100; i++) {
            int postfix = i % 10;
            LocalDateTime realtime = ldt_today.minusMinutes(postfix);
            Date today = Date.from(realtime.atZone(ZoneId.systemDefault()).toInstant());

            StockData expected = new StockData(
                    today,
                    "TEST" + postfix,
                    new Integer(i).doubleValue() + 1.0,
                    new Integer(i).doubleValue() + 1.1,
                    new Integer(i).doubleValue() + 1.2,
                    new Integer(i).doubleValue() + 0.9,
                    new Integer(i) + 1000L,
                    Date.from(realtime.toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                    java.sql.Time.valueOf(realtime.toLocalTime()),
                    ""
            );

            stockDataRepository.save(expected);
        }

//        List<StockData> setupl = stockDataRepository.findAll();
//        setupl.forEach(System.out::println);

        StockData l = stockDataRepository.findTopBySymbolOrderByDateDesc("TEST1");
        System.out.println(l);
        assertEquals("TEST1", l.getSymbol());
        StockData l2 = stockDataRepository.findTopBySymbolOrderByDateDesc("TEST2");
        System.out.println(l2);
        assertEquals("TEST2", l2.getSymbol());
        StockData l3 = stockDataRepository.findTopBySymbolOrderByDateDesc("TEST9");
        System.out.println(l3);
        assertEquals("TEST9", l3.getSymbol());

    }
}