package com.financialjuice.unusualactivity.controller;

import com.financialjuice.unusualactivity.dto.StockDataDTO;
import com.financialjuice.unusualactivity.model.AlertData;
import com.financialjuice.unusualactivity.model.StockData;
import com.financialjuice.unusualactivity.repository.StockDataDTORepository;
import com.financialjuice.unusualactivity.repository.StockDataRepository;
import com.financialjuice.unusualactivity.services.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController    // This means that this class is a Controller
@RequestMapping(path="api/alerts") // This means URL's start with /stock (after Application path)
public class AlertsController {
    private static final Logger log = LoggerFactory.getLogger(AlertsController.class);
    private static DecimalFormat df2 = new DecimalFormat(".##");

    @Autowired
    private StockDataRepository stockDataRepository;

    @Autowired
    private StockDataDTORepository stockDataDTORepository;

    @Autowired
    private StatisticsService statisticsService;

    @RequestMapping(value = "/data", method = RequestMethod.GET)
    public List<AlertData> getData(@RequestParam(value = "symbol", defaultValue = "", required = false) String symbol) {

        // TODO: Take last month volume values to work out mean. Check if we can use intraday range.
        // Fetch stock data for given date range
        Date date = Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Fetch Intraday
        Date lastUpdateDate = stockDataRepository.getLastUpdatedAll();

        // TODO: Take latest volume within Last 5 mins. This should be the real time volume. - Check!!!!
        List<StockDataDTO> i = stockDataDTORepository.findRealtimeStockData(lastUpdateDate);

        return getAlerts(i, date);
    }


    /**
     * History lists comparision of daily datapoints with Mean
     * @param symbol
     * @return
     */
    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public List<AlertData> getHistoryData(@RequestParam(value = "symbol", defaultValue = "", required = false) String symbol) {

        // Fetch for stats Calculation over sample size period
        Date date = Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Fetch Daily data point to compare with Mean
        LocalDate today = LocalDate.now().minusDays(1);

        List<StockDataDTO> daily = stockDataDTORepository.findHistoricStockData(java.sql.Date.valueOf(today));

        return getAlerts(daily, date);
    }


    private List<AlertData> getAlerts(List<StockDataDTO> i, Date date) {
        long startTime = System.currentTimeMillis();
        List<StockData> l = stockDataRepository.findAllByDateAfterOrderBySymbol(date);
        // Compute statistics
        log.debug("Working out descriptive statistics for StockData since date: {}", date);
        List<Map<String, Object>> statsSeries = statisticsService.getDataSeries(l);

        List<AlertData> alerts = new ArrayList<>();

        if(i.size() > 0) {
            // For each StockData if volume exceed limits TODO: add limit parameter
            i.forEach(s -> {
                        log.debug("StockData Symbol: {} => {}", s.getSymbol(), s);
                        Map<String, Object> stat = statsSeries.stream().filter(stringObjectMap -> stringObjectMap.get("symbol").equals(s.getSymbol()))
                                .findFirst()
                                .get();
                        log.debug("Stats for Symbol: {} => {}", s.getSymbol(), stat);

                        // define upper and lower limits for the trigger points
                        long upperLimit = Math.round(Double.parseDouble(stat.get("mean").toString()) + Double.parseDouble(stat.get("std dev").toString()));
                        long lowerLimit = Math.round(Double.parseDouble(stat.get("mean").toString()) - Double.parseDouble(stat.get("std dev").toString()));
                        log.debug("Range Lower [{}] - Upper [{}]", lowerLimit, upperLimit);

                        // compare latest volume with limits
                        if (s.getVolume() > upperLimit) {
                            // Add to new list of Stock volume Exceeds mean + std dev -> Percentage increase
                            long volumeChange = Long.valueOf(String.valueOf(s.getVolume())) - Math.round(Double.parseDouble(stat.get("mean").toString()));
                            long volumeChangePct = Math.round(((Double.valueOf(String.valueOf(s.getVolume())) / Double.parseDouble(stat.get("mean").toString())) -1) * 100);
                            log.debug("Change increase for {} => +{}% ({} units)", s.getSymbol(), volumeChangePct, volumeChange);
                            AlertData alertData = new AlertData(s, volumeChange, volumeChangePct,null);
                            alerts.add(alertData);

                        } else if (s.getVolume() < lowerLimit) {
                            // Add to new list of Stock volume is below mean - std dev -> Percentage decrease
                            long volumeChange = Long.valueOf(String.valueOf(s.getVolume())) - Math.round(Double.parseDouble(stat.get("mean").toString()));
                            long volumeChangePct = 0 - Math.round((1 - (Double.valueOf(String.valueOf(s.getVolume())) / Double.parseDouble(stat.get("mean").toString()))) * 100);
                            log.debug("Change decrease for {} => {}% ({} units)", s.getSymbol(), volumeChangePct, volumeChange);
                            AlertData alertData = new AlertData(s, volumeChange, volumeChangePct,null);
                            alerts.add(alertData);
                        }
                    }
            );
        }
        long stopTime = System.currentTimeMillis();
        log.debug("Elapsed time: {}ms",(stopTime - startTime));
        return alerts;
    }

}
