package com.financialjuice.unusualactivity.controller;

import com.financialjuice.unusualactivity.model.ChartSeries;
import com.financialjuice.unusualactivity.model.StatsGrid;
import com.financialjuice.unusualactivity.model.StockData;
import com.financialjuice.unusualactivity.repository.StatsGridCacheRepository;
import com.financialjuice.unusualactivity.repository.StockDataRepository;
import com.financialjuice.unusualactivity.services.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@CrossOrigin("*")
@RestController    // This means that this class is a Controller
@RequestMapping(path = "api/stats") // This means URL's start with /stock (after Application path)
public class StockStatsController {

    private static final Logger log = LoggerFactory.getLogger(StockStatsController.class);

    @Autowired
    private StockDataRepository stockDataRepository;

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private StatsGridCacheRepository statsGridCacheRepository;

    @RequestMapping(value = "/olddata", method = RequestMethod.GET)
    public List<Map<String, Object>> getOldData(@RequestParam(value = "date", defaultValue = "", required = false)
                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> dateParam) {

        Date date = Date.from(dateParam.orElse(LocalDate.now().minusMonths(1)).atStartOfDay(ZoneId.systemDefault()).toInstant());

        log.debug("Working out descriptive statistics for StockData since date: {}", date);

        List<StockData> l = stockDataRepository.findAllByDateAfterOrderBySymbol(date);
        List<Map<String, Object>> chartSeries = statisticsService.getDataSeries(l);
        return chartSeries;
    }

    @RequestMapping(value = "/data", method = RequestMethod.GET)
    public List<StatsGrid> getData(@RequestParam(value = "time", defaultValue = "20", required = false) int time,
                                   @RequestParam(value = "ndvol", defaultValue = "0", required = false) double ndvol,
                                   @RequestParam(value = "ndprange", defaultValue = "0", required = false) double ndprange) {

        log.debug("Parameters:- time: {}, ndvol: {}, ndprange: {}", time, ndvol, ndprange);

        ZoneId zoneUS = ZoneId.of("America/New_York");
        ZonedDateTime timePast = ZonedDateTime.now().withZoneSameInstant(zoneUS).minusMinutes(time);
        Date datePastUS = Date.from(timePast.toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant());
        Date fromDate = Date.from(timePast.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());

        Iterable<StatsGrid> iterator = statsGridCacheRepository.findAllByDateAfter(datePastUS);
        List<StatsGrid> statsGrids = StreamSupport
                .stream(iterator.spliterator(), true)
                .collect(Collectors.toList());

        // Filter out ND
        if (ndvol > 0) {
            statsGrids = statsGrids.stream().filter(statsGrid -> statsGrid.getNormalDist_Volume() > ndvol / 100).collect(Collectors.toList());
        }
        if (ndprange > 0) {
            statsGrids = statsGrids.stream().filter(statsGrid -> statsGrid.getNormalDist_PRange() > ndprange / 100).collect(Collectors.toList());
        }

        // Add Sparklines volumes
        statsGrids = statsGrids.stream()
                .map(statsGrid -> {
                    List<ChartSeries> l = getVolumesDataBySymbol(statsGrid.getSymbol(), fromDate);
                    statsGrid.setChartSeries(l);
                    return statsGrid;
                }).collect(Collectors.toList());

        log.debug("Retrieved: {} statsGrids results from time {} mins ago at:{}", statsGrids.size(), time, datePastUS);
        return statsGrids;
    }

    @RequestMapping(value = "/data/{symbol}", method = RequestMethod.GET)
    public List<StatsGrid> getDataBySymbol(@PathVariable(value = "symbol", required = false) String symbol,
                                           @RequestParam(value = "time", defaultValue = "20", required = false) int time,
                                           @RequestParam(value = "ndvol", defaultValue = "0", required = false) double ndvol,
                                           @RequestParam(value = "ndprange", defaultValue = "0", required = false) double ndprange) {

        log.debug("Symbol: {} Parameters:- time: {}, ndvol: {}, ndprange: {}", symbol, time, ndvol, ndprange);

        ZoneId zoneUS = ZoneId.of("America/New_York");
        ZonedDateTime timePast = ZonedDateTime.now().withZoneSameInstant(zoneUS).minusMinutes(time);
        Date datePastUS = Date.from(timePast.toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant());
        Date fromDate = Date.from(timePast.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());

        Iterable<StatsGrid> iterator = statsGridCacheRepository.findAllBySymbolAndDateAfter(symbol, datePastUS);
        List<StatsGrid> statsGrids = StreamSupport
                .stream(iterator.spliterator(), true)
                .collect(Collectors.toList());

        if (ndvol > 0) {
            statsGrids = statsGrids.stream().filter(statsGrid -> statsGrid.getNormalDist_Volume() > ndvol / 100).collect(Collectors.toList());
        }
        if (ndprange > 0) {
            statsGrids = statsGrids.stream().filter(statsGrid -> statsGrid.getNormalDist_PRange() > ndprange / 100).collect(Collectors.toList());
        }

        // Add Sparklines volumes
        statsGrids = statsGrids.stream()
                .map(statsGrid -> {
                    List<ChartSeries> l = getVolumesDataBySymbol(statsGrid.getSymbol(), fromDate);
                    statsGrid.setChartSeries(l);
                    return statsGrid;
                }).collect(Collectors.toList());
        log.debug("Retrieved: {} statsGrids results for symbol: {} from time {} mins ago at:{}", symbol, statsGrids.size(), time, datePastUS);

        return statsGrids;
    }

    @RequestMapping(value = "/volumestoday/{symbol}", method = RequestMethod.GET)
    public List<ChartSeries> getVolumesBySymbol(@PathVariable(value = "symbol", required = false) String symbol) {
        Date fromDate = Date.from(LocalDateTime.now().toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
        return getVolumesDataBySymbol(symbol, fromDate);
    }

    public List<ChartSeries> getVolumesDataBySymbol(String symbol, Date fromDate) {
        Iterable<StatsGrid> iterator = statsGridCacheRepository.findAllBySymbolAndDateAfter(symbol, fromDate);
        List<StatsGrid> statsGrids = StreamSupport
                .stream(iterator.spliterator(), true)
                .collect(Collectors.toList());

        List<ChartSeries> chartSeriesList = statsGrids.stream()
                        .map(statsGrid -> {
                            ChartSeries chartSeries = new ChartSeries(statsGrid.getTime_part(), statsGrid.getLatestVolume(), statsGrid.getpRange());
                            return chartSeries;
                        })
                        .collect(Collectors.toList());
        return chartSeriesList;
    }


    @RequestMapping(value = "/chartseries", method = RequestMethod.GET)
    public List<Map<String, Object>> getBoxPlotChartSeries(@RequestParam(value = "symbol", defaultValue = "", required = false) String symbol,
                                                           @RequestParam(value = "sector", defaultValue = "", required = false) String sector) {

        Date start = Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        log.debug("Working out Boxplot chart series using StockData for symbol: {} Start date: {} End date: {}", symbol, start, end);


        List<StockData> l = new ArrayList<>();

        if (!symbol.isEmpty()) {
            l = stockDataRepository.findAllBySymbolAndDateBetweenAndDate(symbol, start, end);
        } else if (!sector.isEmpty()) {

        } else {
            l = stockDataRepository.findAllByDateAfterOrderBySymbol(start);
        }

        List<Map<String, Object>> chartSeries = statisticsService.getBoxPlotChartSeries(l);
        return chartSeries;
    }

}
