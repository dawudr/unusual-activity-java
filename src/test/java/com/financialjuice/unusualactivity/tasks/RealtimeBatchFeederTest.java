package com.financialjuice.unusualactivity.tasks;

import com.financialjuice.unusualactivity.Application;
import com.financialjuice.unusualactivity.model.StockCompositeKey;
import com.financialjuice.unusualactivity.model.StockData;
import com.financialjuice.unusualactivity.model.SymbolData;
import com.financialjuice.unusualactivity.repository.StockDataRepository;
import com.financialjuice.unusualactivity.repository.SymbolRepository;
import com.financialjuice.unusualactivity.rest.RealtimeBatchRestClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = {Application.class})
@SpringBootTest(properties = "/application-test.properties", classes = Application.class)
//@SpringBootTest(properties = "/application-test.properties", classes = { Application.class, StockDataRepository.class, StockCompositeKey.class})
//@ContextConfiguration
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
//@EnableConfigurationProperties
//@EnableAutoConfiguration
//@SpringBootApplication
//@DataJpaTest
//@AutoConfigureMockMvc
public class RealtimeBatchFeederTest {

    @Autowired
    SymbolRepository symbolRepository;

    @Autowired
    StockDataRepository stockDataRepository;

    @Autowired
    @InjectMocks
    private RealtimeBatchFeeder feeder;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {

        Date todayDate = new Date();
        String[] symbols = {"AAPL", "BP", "FB", "TSLA", "MSFT", "GOOG"};

        // Setup
        for(int i = 0; i < symbols.length; i++) {
            SymbolData symbolData = new SymbolData(
                    symbols[i],
                    symbols[i] + "-Name" + i,
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

        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(feeder).build();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void importFeed() {
        Iterable<SymbolData> iterator = symbolRepository.findAll();
        List<SymbolData> ls = StreamSupport
                .stream(iterator.spliterator(), true)
                .collect(Collectors.toList());
        feeder.importFeed(ls);
        Iterable<StockData> iterator2 = stockDataRepository.findAll();
        List<StockData> result = StreamSupport
                .stream(iterator2.spliterator(), false)
                .collect(Collectors.toList());
        assertEquals(ls.size(), result.size());

        result.forEach(System.out::println);

    }

    @Test
    public void getSymbolPartitions() {

        Iterable<SymbolData> iterator = symbolRepository.findAll();
        List<SymbolData> ls = StreamSupport
                .stream(iterator.spliterator(), true)
                .collect(Collectors.toList());
        System.out.println("Size=" + ls.size());

        List<List<SymbolData>> partitions = feeder.getSymbolPartitions(ls);
        System.out.println("Partitions Count=" + partitions.size());

        int expectedPartitions = ls.size()/feeder.batchSize + ((ls.size()%feeder.batchSize == 0) ? 0:1);
        assertEquals(expectedPartitions, partitions.size());

        partitions.forEach(symbolData -> {
            System.out.println("Batch count=" + symbolData.size());
            symbolData.forEach(symbolData1 -> {
                System.out.println("Symbol=" + symbolData1);
            });
        });

    }

}