package com.financialjuice.unusualactivity.controller;

import com.financialjuice.unusualactivity.model.StockData;
import com.financialjuice.unusualactivity.repository.StockDataRepository;
import com.financialjuice.unusualactivity.services.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController    // This means that this class is a Controller
@RequestMapping(path="api/stats") // This means URL's start with /stock (after Application path)
public class StockStatsController {

    private static final Logger log = LoggerFactory.getLogger(StockStatsController.class);

    @Autowired
    private StockDataRepository stockDataRepository;

    @Autowired
    private StatisticsService statisticsService;


    @RequestMapping(value = "/data", method = RequestMethod.GET)
    public List<Map<String, Object>> getData(@RequestParam(value = "symbol", defaultValue = "", required = false) String symbol) {

        Date date = Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        log.debug("Working out descriptive statistics for StockData since date: {}", date);

        List<StockData> l = stockDataRepository.findAllByDateAfterOrderBySymbol(date);
        List<Map<String, Object>> chartSeries = statisticsService.getDataSeries(l);
        return chartSeries;
    }

    @RequestMapping(value = "/data/{symbol}", method = RequestMethod.GET)
    public List<Map<String, Object>> getDataBySymbol(@PathVariable(value = "symbol", required = false) String symbol) {

        Date start = Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        log.debug("Working out descriptive statistics for StockData for symbol: {} Start date: {} End date: {}", symbol, start, end);

        // TODO: Fix me
//        List<StockData> l = stockDataRepository.findAllBySymbolAndDateBetweenAndDate(symbol, start, end);
        List<StockData> l = stockDataRepository.findAllBySymbolAndDateAfter(symbol, start);

        List<Map<String, Object>> chartSeries = statisticsService.getDataSeries(l);
        return chartSeries;
    }

    @RequestMapping(value = "/chartseries", method = RequestMethod.GET)
    public List<Map<String, Object>> getBoxPlotChartSeries(@RequestParam(value = "symbol", defaultValue = "", required = false) String symbol,
                                                           @RequestParam(value = "sector", defaultValue = "", required = false) String sector) {

        Date start = Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        log.debug("Working out Boxplot chart series using StockData for symbol: {} Start date: {} End date: {}", symbol, start, end);


        List<StockData> l = new ArrayList<>();

        if(!symbol.isEmpty()) {
            l = stockDataRepository.findAllBySymbolAndDateBetweenAndDate(symbol, start, end);
        } else if(!sector.isEmpty()) {

        } else {
            l = stockDataRepository.findAllByDateAfterOrderBySymbol(start);
        }

        List<Map<String, Object>> chartSeries = statisticsService.getBoxPlotChartSeries(l);
        return chartSeries;
    }

}
