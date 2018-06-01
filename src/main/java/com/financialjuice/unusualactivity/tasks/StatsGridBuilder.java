package com.financialjuice.unusualactivity.tasks;

import com.financialjuice.unusualactivity.model.StatsGrid;
import com.financialjuice.unusualactivity.model.StockData;
import com.financialjuice.unusualactivity.model.SymbolData;
import com.financialjuice.unusualactivity.repository.StatsGridCacheRepository;
import com.financialjuice.unusualactivity.repository.StockDataRepository;
import com.financialjuice.unusualactivity.repository.SymbolRepository;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class StatsGridBuilder implements Runnable {

    // For each Symbol
    //  For time_part or current time_part or max time_part of date
    //      Select all volumes and prange
    //      Insert into StatGrid

    private static final Logger log = LoggerFactory.getLogger(StatsGridBuilder.class);

    @Autowired
    private StatsGridCacheRepository statsGridRepository;

    @Autowired
    private SymbolRepository symbolRepository;

    @Autowired
    private StockDataRepository stockDataRepository;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private ApplicationContext applicationContext;

    public static void main(String[] args) {
        StatsGridBuilder f = new StatsGridBuilder();
        f.start();
    }

    @Override
    public void run() {
        log.info("Running StatsGridBuilder Thread");
        this.start();
    }

    public void start() {
        runStatsGridBuilder();
    }

    public void executeAsynchronously() {
        StatsGridBuilder stats = applicationContext.getBean(StatsGridBuilder.class);
        taskExecutor.execute(stats);
    }

    /**
     * StatsGridBuilder
     */
    public void runStatsGridBuilder() {

        log.debug("Started StatsGridBuilder");
        long startTime = System.currentTimeMillis();
        Iterable<SymbolData> iterator = symbolRepository.findAll();
        List<SymbolData> ls = StreamSupport
                .stream(iterator.spliterator(), true)
                .collect(Collectors.toList());

        log.debug("Checking realtime feed for {} Symbols", ls.size());
        AtomicInteger refreshedCount = new AtomicInteger();

        ls.forEach(symbolData -> {
                    StockData realtimeStock = stockDataRepository.findTopBySymbolOrderByDateDesc(symbolData.getSymbol());
                    boolean success = refreshStatsGrid(symbolData, realtimeStock);
                    if (success) {
                        refreshedCount.getAndIncrement();
                    }
                }
        );
        log.debug("End StatsGridBuilder: Records refreshed: {} Time elasped: {}ms", refreshedCount, System.currentTimeMillis() - startTime);
    }


    public boolean refreshStatsGrid(SymbolData symbolData, StockData realtimeStock) {
        long startTime = System.currentTimeMillis();
//                    Time lastupdated = statsGridRepository.getLastUpdated(symbolData.getSymbol());
//--        Date lastStatsGridRefreshed = statsGridRepository.getTopLastUpdatedDate();
//                    Date lastupdateDate = statsGridRepository.getLastUpdatedDate();
        boolean refreshed = false;

        // Check if stats has an entry for the time
//        if (lastStatsGridRefreshed == null || lastStatsGridRefreshed.before(realtimeStock.getDate())) {
            log.debug("Refreshing StatsGrid - symbol: {} realtimeStock: {}", realtimeStock.getSymbol(), realtimeStock.getDate());

            // Get Time part slice
            List<StockData> stocksByTimePart = stockDataRepository.getSymbolTimeSlice(realtimeStock.getSymbol(), realtimeStock.getTime_part());

            // Get Mean and st dev
            DescriptiveStatistics volStats = new DescriptiveStatistics();
            stocksByTimePart.forEach(stockData -> {
                volStats.addValue(stockData.getVolume());
            });

            DescriptiveStatistics prRangeStats = new DescriptiveStatistics();
            stocksByTimePart.forEach(stockData -> {
                prRangeStats.addValue(stockData.getVolume());
            });

            if (volStats.getStandardDeviation() > 0 && prRangeStats.getStandardDeviation() > 0) {
                NormalDistribution ndVol = new NormalDistribution(volStats.getMean(), volStats.getStandardDeviation());
                NormalDistribution prRangeVol = new NormalDistribution(prRangeStats.getMean(), prRangeStats.getStandardDeviation());

                // Calculate
                StatsGrid statsGrid = new StatsGrid(realtimeStock.getDate(), realtimeStock.getSymbol(), ndVol.cumulativeProbability(realtimeStock.getVolume()), prRangeVol.cumulativeProbability(realtimeStock.getHigh() - realtimeStock.getLow()), realtimeStock.getVolume(), realtimeStock.getHigh() - realtimeStock.getLow(), symbolData.getName(), realtimeStock.getNews(), realtimeStock.getDate_part(), realtimeStock.getTime_part());

                // Save
                statsGridRepository.save(statsGrid);
                log.debug("Saved symbol: {} statGrid: {}", realtimeStock.getSymbol(), statsGrid);
                refreshed = true;
            }
//        }
        log.debug("End Refresh StatsGridBuilder: Symbol refreshed: {} Time elasped: {}ms", realtimeStock.getSymbol(), System.currentTimeMillis() - startTime);
        return refreshed;
    }
}
