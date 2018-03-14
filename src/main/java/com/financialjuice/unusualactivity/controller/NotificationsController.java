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
@RequestMapping(path="api/notify") // This means URL's start with /stock (after Application path)
public class NotificationsController {
    private static final Logger log = LoggerFactory.getLogger(NotificationsController.class);

    @Autowired
    private StockDataRepository stockDataRepository;

    @Autowired
    private StatisticsService statisticsService;

    @RequestMapping(value = "/data", method = RequestMethod.GET)
    public List<StockData> getData(@RequestParam(value = "symbol", defaultValue = "", required = false) String symbol) {

        Date date = java.util.Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Fetch stock data for given date range
        List<StockData> l = stockDataRepository.findAllByDateAfterOrderBySymbol(date);
        // Compute statistics
        log.debug("Working out descriptive statistics for StockData since date: {}", date);
        List<Map<String, Object>> statsSeries = statisticsService.getDataSeries(l);
        // Fetch Intraday
        Date lastUpdateDate = stockDataRepository.getLastUpdatedAll();
        log.debug("Fetching Intraday for StockData on date: {}", lastUpdateDate);

        List<StockData> i = stockDataRepository.findAllByDateOrderBySymbol(lastUpdateDate);

        List<StockData> breachLimitsStock = new ArrayList<>();

        if(i.size() > 0) {

            // For each intraday if volume exceed limits TODO: add limit parameter
            i.forEach(s -> {
                Map<String, Object> stat = statsSeries.stream().filter(stringObjectMap -> stringObjectMap.get("symbol").equals(s.getSymbol()))
                        .findFirst()
                        .get();

                log.debug("Intraday for Symbol: {} => {}", s.getSymbol(), s);
                log.debug("Stats for Symbol: {} => {}", s.getSymbol(), stat);

                // Add to new list of StockExceeds
                if (s.getVolume() > Double.parseDouble(stat.get("mean").toString()) + Double.parseDouble(stat.get("std dev").toString()) ||
                        s.getVolume() < Double.parseDouble(stat.get("mean").toString()) - Double.parseDouble(stat.get("std dev").toString())) {
                    breachLimitsStock.add(s);
                }
            });

        }
        return breachLimitsStock;
    }


    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public List<StockData> getHistoryData(@RequestParam(value = "symbol", defaultValue = "", required = false) String symbol) {

        Date date = Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Fetch stock data for given date range
        List<StockData> l = stockDataRepository.findAllByDateAfterOrderBySymbol(date);
        // Compute statistics
        log.debug("Working out descriptive statistics for StockData since date: {}", date);
        List<Map<String, Object>> statsSeries = statisticsService.getDataSeries(l);
        // Fetch Daily
        Date today = Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        log.debug("Fetching Daily for StockData on date: {}", today);

        List<StockData> daily = stockDataRepository.findAllByDateAfterOrderByDate(today);

        List<StockData> breachLimitsStock = new ArrayList<>();

        if(daily.size() > 0) {

            // For each intraday if volume exceed limits TODO: add limit parameter
            daily.forEach(s -> {
                Map<String, Object> stat = statsSeries.stream().filter(stringObjectMap -> stringObjectMap.get("symbol").equals(s.getSymbol()))
                        .findFirst()
                        .get();

                log.debug("Daily for Symbol: {} => {}", s.getSymbol(), s);
                log.debug("Stats for Symbol: {} => {}", s.getSymbol(), stat);

                // Add to new list of StockExceeds
                if (s.getVolume() > Double.parseDouble(stat.get("mean").toString()) + Double.parseDouble(stat.get("std dev").toString()) ||
                        s.getVolume() < Double.parseDouble(stat.get("mean").toString()) - Double.parseDouble(stat.get("std dev").toString())) {
                    breachLimitsStock.add(s);
                }
            });
        }
        return breachLimitsStock;
    }

}
