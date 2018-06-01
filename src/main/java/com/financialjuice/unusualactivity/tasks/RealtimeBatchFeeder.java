package com.financialjuice.unusualactivity.tasks;

import com.financialjuice.unusualactivity.model.StockData;
import com.financialjuice.unusualactivity.model.SymbolData;
import com.financialjuice.unusualactivity.repository.StockDataDTORepository;
import com.financialjuice.unusualactivity.repository.StockDataRepository;
import com.financialjuice.unusualactivity.repository.SymbolRepository;
import com.financialjuice.unusualactivity.rest.IntradayBatchRestClient;
import com.financialjuice.unusualactivity.rest.RealtimeBatchRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.groupingBy;

/**
 * Javax’s @PostConstruct annotation can be used for annotating a method that should be run once immediately after the bean’s initialization. Keep in mind that the annotated method will be executed by Spring even if there is nothing to inject.
 * See http://www.baeldung.com/running-setup-logic-on-startup-in-spring
 */

@Component
public class RealtimeBatchFeeder implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RealtimeBatchFeeder.class);

    @Autowired
    private RealtimeBatchRestClient restClient;

    @Autowired
    private StockDataRepository stockDataRepository;

    @Autowired
    private StatsGridBuilder statsGridBuilder;

    @Autowired
    private SymbolRepository symbolRepository;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private ApplicationContext applicationContext;

    public static final int batchSize = 100;

    @Override
    public void run() {
        log.info("Running RealtimeBatchFeeder Thread");
        this.start();
    }

    public void executeAsynchronously() {
        RealtimeBatchFeeder feeder = applicationContext.getBean(RealtimeBatchFeeder.class);
        taskExecutor.execute(feeder);
    }

    /**
     * RealtimeBatchFeeder
     */
    public void start() {

        log.info("Started RealtimeBatchFeeder");

        Iterable<SymbolData> iterator = symbolRepository.findAll();
        List<SymbolData> ls = StreamSupport
                .stream(iterator.spliterator(), true)
                .collect(Collectors.toList());
        log.debug("Importing Intraday Batch Stockdata for {} Symbols", ls.size());
        importFeed(ls);

    }

    /**
     * Import stock feeder
     *
     * @param symbols
     */
    public void importFeed(List<SymbolData> symbols) {
        log.info("Importing StockData for [{}] symbols", symbols.size());
        long startTime0 = System.currentTimeMillis();

        // Split entire list of stocks into partition batch of 100 (due to API limit)
        List<List<SymbolData>> partitions = getSymbolPartitions(symbols);

        // Process each partition
        int batchNo = 0;
        for (List<SymbolData> p : partitions) {

            String batchName = String.format("BatchNo: [%s of %s] with Symbols [%s -> %s]", batchNo + 1, partitions.size(), p.get(0).getSymbol(), p.get(p.size() - 1).getSymbol());
            log.info("Processing {}", batchName);
            batchNo++;

//            Thread thread = new Thread(new Runnable() {
//                public void run()
//                {
            // Convert ArrayList<Symbol> to a csv parameter list of symbols for HTTP Rest request
            String csv = p.stream()
                    .map(SymbolData::getSymbol)
                    .collect(Collectors.joining(","));

            // Get data points for each batch of 100 symbols
            List<StockData> intradayRaw = restClient.getIntradayData(csv);
            log.info("Processing {} symbols from stock feed", intradayRaw.size());
            intradayRaw.forEach(stockData -> {
                // Save new quote
//                log.info("Saving latest quote: {}", stockData);
                stockDataRepository.save(stockData);

                // Refresh StatsGrid
                // Merge to get Stock Name from symbol
                Optional<SymbolData> symbolData = symbols.stream()
                        .filter(s -> s.getSymbol().equals(stockData.getSymbol()))
                        .findFirst();
                statsGridBuilder.refreshStatsGrid(symbolData.get(), stockData);
            });
        }
//            });
//            thread.start();
//        }
        log.info("Finished Realtime Batch Import. Elapsed time: {}ms", (System.currentTimeMillis() - startTime0));
    }


    private LocalDateTime getLastUpdate(String symbol) {
        Date dateToConvert = stockDataRepository.getLastUpdated(symbol);
        LocalDateTime lastUpdate = null;
        if (dateToConvert != null) {
            lastUpdate = dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        log.debug("LastUpdate for Symbol [{}] was {}", symbol, dateToConvert);
        return lastUpdate;
    }


    public List<List<SymbolData>> getSymbolPartitions(List<SymbolData> symbols) {
        // Limit of API batch symbols is 100 so divi
        // 1. Define the partition range, n plus 1 if MOD has remainder.
        // 2. Map sublists starting with n=0 -> Minimum of n * batchsize (if not last batch) or list size (if last batch)
        List<List<SymbolData>> partitions = IntStream.range(0, ((symbols.size()) / batchSize) + ((symbols.size() % batchSize == 0) ? 0 : 1))
                .mapToObj(n -> symbols.subList(n * batchSize, Math.min((n * batchSize) + batchSize, symbols.size())))
                .collect(Collectors.toList());
        log.info("Partitioning StockData feed into [{}] batches of batch size: {}", partitions.size(), batchSize);
        return partitions;
    }


//    @PostConstruct
//    public void init() {
//        log.info("Starting StockDataFeeder");
//        start(StockDataRestClient.TimeSeries.DAILY);
//    }

    public static void main(String[] args) {
        RealtimeBatchFeeder f = new RealtimeBatchFeeder();
        f.start();
    }

}
