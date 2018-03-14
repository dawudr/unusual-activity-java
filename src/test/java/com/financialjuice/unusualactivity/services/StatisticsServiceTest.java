package com.financialjuice.unusualactivity.services;

import com.financialjuice.unusualactivity.model.StockData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = Application.class)
public class StatisticsServiceTest {

    private StatisticsService statisticsService;
    private LocalDate today;
    private List<StockData> stockDataList;

    @Before
    public void setUp() throws Exception {
        today = LocalDate.now();
        Date todayDate = Date.from(today.minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Setup
        stockDataList = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            String postfix = String.valueOf(i % 10);
            StockData testStock = new StockData(
                    todayDate,
                    "TEST" + postfix,
                    new Integer(i).doubleValue() + 1.0,
                    new Integer(i).doubleValue() + 1.1,
                    new Integer(i).doubleValue() + 1.2,
                    new Integer(i).doubleValue() + 0.9,
                    new Integer(i) + 1000L);
            stockDataList.add(testStock);
        }
        StockData testStock = new StockData(
                todayDate,
                "AAAA",
                1.0,
                1.1,
                1.2,
                0.9,
                1000L);
        stockDataList.add(testStock);
        StockData testStock1 = new StockData(
                todayDate,
                "YYYY",
                1.0,
                1.1,
                1.2,
                0.9,
                1000L);
        stockDataList.add(testStock1);
        StockData testStock2 = new StockData(
                todayDate,
                "BBBB",
                1.0,
                1.1,
                1.2,
                0.9,
                1000L);
        stockDataList.add(testStock2);
        StockData testStock3 = new StockData(
                todayDate,
                "ZZZZ",
                1.0,
                1.1,
                1.2,
                0.9,
                1000L);
        stockDataList.add(testStock3);
    }

    @After
    public void tearDown() throws Exception {
        stockDataList.clear();
    }

    @Test
    public void testCompute() {
        statisticsService = new StatisticsService();
        statisticsService.getDataSeries(stockDataList);

        List<Map<String, Object>> l = statisticsService.getDataSeries(stockDataList);
        System.out.println(l);
        assertNotNull(l);
    }

    @Test
    public void testgetDataSeries() {
        stockDataList.clear();
        Date todayDate = Date.from(today.minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        for(int i = 0; i < 100; i++) {
            StockData testStock = new StockData(
                    todayDate,
                    "AAAA",
                    new Integer(i).doubleValue() + 1.0,
                    new Integer(i).doubleValue() + 1.1,
                    new Integer(i).doubleValue() + 1.2,
                    new Integer(i).doubleValue() + 0.9,
                    1000L);
            stockDataList.add(testStock);
        }
        StockData testStock = new StockData(
                todayDate,
                "AAAA",
                1.0,
                1.1,
                1.2,
                0.9,
                1000L);
        stockDataList.add(testStock);
        StockData testStock1 = new StockData(
                todayDate,
                "AAAA",
                1.0,
                1.1,
                1.2,
                0.9,
                1000L);
        stockDataList.add(testStock1);
        StockData testStock2 = new StockData(
                todayDate,
                "BBBB",
                1.0,
                1.1,
                1.2,
                0.9,
                2000L);
        stockDataList.add(testStock2);
        StockData testStock3 = new StockData(
                todayDate,
                "CCCC",
                1.0,
                1.1,
                1.2,
                0.9,
                1L);
        stockDataList.add(testStock3);
        StockData testStock4 = new StockData(
                todayDate,
                "CCCC",
                1.0,
                1.1,
                1.2,
                0.9,
                3000L);
        stockDataList.add(testStock4);

        statisticsService = new StatisticsService();
        statisticsService.getDataSeries(stockDataList);

        List<Map<String, Object>> l = statisticsService.getDataSeries(stockDataList);
        System.out.println(l);
        assertNotNull(l);
        assertEquals(1000.0, l.get(0).get("max"));
        assertEquals(1000.0, l.get(0).get("min"));
        assertEquals(1000.0, l.get(0).get("mean"));

        Map<String, Object> m = l.stream().filter(stringObjectMap -> stringObjectMap.get("symbol").equals("CCCC"))
                .findFirst()
                .get();

        System.out.println("Map=" + m);

    }

    @Test
    public void testGetBoxPlotChartSeries() {
        statisticsService = new StatisticsService();

        List<Map<String, Object>> l = statisticsService.getBoxPlotChartSeries(stockDataList);
        l.forEach(map -> {
            testComputeBoxPlotStats(map);
        });
        System.out.println(l);
    }


    private void testComputeBoxPlotStats(Map map) {
        assertNotNull(map);
        assertTrue(map.containsKey("values"));
        assertTrue(map.containsKey("label"));
        assertTrue(((Map<String, Double>) map.get("values")).containsKey("Q1") && ((Map<String, Double>) map.get("values")).containsKey("Q2") && ((Map<String, Double>) map.get("values")).containsKey("Q3"));
        assertTrue(((Map<String, Double>) map.get("values")).containsKey("whisker_low") && ((Map<String, Double>) map.get("values")).containsKey("whisker_high"));
    }


}