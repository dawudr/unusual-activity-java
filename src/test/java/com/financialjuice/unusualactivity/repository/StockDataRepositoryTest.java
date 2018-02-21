package com.financialjuice.unusualactivity.repository;

import com.financialjuice.unusualactivity.model.StockData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class StockDataRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    StockDataRepository stockDataRepository;
    StockData expected;

    @Before
    public void setUp() throws Exception {
        expected = new StockData(new Date(), "TEST", 1.0, 1.1, 1.2, 0.9, 1000L);
        System.out.println("EXPECTED:" + expected.toString());

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
}