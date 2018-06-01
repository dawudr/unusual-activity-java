package com.financialjuice.unusualactivity.tasks;

import com.financialjuice.unusualactivity.model.SymbolData;
import com.financialjuice.unusualactivity.repository.SymbolRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.*;
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
@DataJpaTest
public class IntradayBatchFeederTest {

    @Autowired
    SymbolRepository symbolRepository;

    @Before
    public void setUp() throws Exception {

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Date todayDate = new Date();

        // Setup
        for(int i = 0; i < 201; i++) {
            SymbolData symbolData = new SymbolData(
                    "TEST" + i,
                    "TEST Name" + i,
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

    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void cleanFeed() {
    }

    @Test
    public void importFeed() {
    }

    @Test
    public void getSymbolPartitions() {

        Iterable<SymbolData> iterator = symbolRepository.findAll();
        List<SymbolData> ls = StreamSupport
                .stream(iterator.spliterator(), true)
                .collect(Collectors.toList());
        System.out.println("Size=" + ls.size());

        IntradayBatchFeeder test = new IntradayBatchFeeder();
        List<List<SymbolData>> partitions = test.getSymbolPartitions(ls);
        System.out.println("Partitions Count=" + partitions.size());

        int expectedPartitions = ls.size()/test.batchSize + ((ls.size()%test.batchSize == 0) ? 0:1);
        assertEquals(expectedPartitions, partitions.size());


        partitions.forEach(symbolData -> {
            System.out.println("Batch count=" + symbolData.size());
//            symbolData.forEach(symbolData1 -> {
//                System.out.println("Symbol=" + symbolData1);
//            });
        });

    }

}