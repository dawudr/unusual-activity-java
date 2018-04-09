package com.financialjuice.unusualactivity.tasks;

import com.financialjuice.unusualactivity.model.StockData;
import com.financialjuice.unusualactivity.model.SymbolData;
import com.financialjuice.unusualactivity.repository.StockDataRepository;
import com.financialjuice.unusualactivity.repository.SymbolRepository;
import com.financialjuice.unusualactivity.rest.IntradayBatchRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Javax’s @PostConstruct annotation can be used for annotating a method that should be run once immediately after the bean’s initialization. Keep in mind that the annotated method will be executed by Spring even if there is nothing to inject.
 * See http://www.baeldung.com/running-setup-logic-on-startup-in-spring
 */

@Component
public class IntradayBatchFeeder implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(IntradayBatchFeeder.class);

    @Autowired
    private IntradayBatchRestClient intradayBatchRestClient;

    @Autowired
    private StockDataRepository stockDataRepository;

    @Autowired
    private SymbolRepository symbolRepository;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private ApplicationContext applicationContext;


    @Override
    public void run() {
        log.info("Running IntradayFeeder Thread");
        this.start();
    }

    public void executeAsynchronously() {
        IntradayBatchFeeder intradayFeeder = applicationContext.getBean(IntradayBatchFeeder.class);
        taskExecutor.execute(intradayFeeder);
    }

    /**
     * IntradayFeeder
     */
    public void start() {

        log.info("Started IntradayFeeder");

        List<SymbolData> ls = symbolRepository.findAll();
        log.debug("Importing IntradayBatch Stockdata for {} Symbols", ls.size());
        importFeed(ls);

    }

    /**
     * Import stock feeder
     *
     * @param symbols
     */
    private void importFeed(List<SymbolData> symbols) {
        log.info("Importing StockData for [{}] symbols", symbols.size());
        long startTime0 = System.currentTimeMillis();

        int batchSize = 100;
        // Limit of API batch symbols is 100 so divide
        List<List<SymbolData>> partitions = IntStream.range(0, ((symbols.size() -1) / batchSize))
                .mapToObj(n -> symbols.subList(n * batchSize, Math.min((n + 1) * batchSize, symbols.size())))
                .collect(Collectors.toList());
        log.info("Partitioning StockData feed into [{}] batches of batch size: {}", partitions.size(), batchSize);

        int batchNo = 0;
        for(List<SymbolData> p : partitions) {
            log.info("Processing batchNo: [{} of {}] with Symbols [{} -> {}]", batchNo, partitions.size(), p.get(0).getSymbol(), p.get(partitions.size()).getSymbol());
            batchNo++;

            // Convert ArrayList<Symbol> to a csv list of symbols
            String csv = p.stream()
                    .map(SymbolData::getSymbol)
                    .collect(Collectors.joining(","));

            long startTime = System.currentTimeMillis();
            List<StockData> intraday = intradayBatchRestClient.getIntradayData(csv);
            long stopTime = System.currentTimeMillis();
            log.debug("Elapsed time: {}ms", (stopTime - startTime));

            if (intraday != null && !intraday.isEmpty()) {
                intraday.forEach(s -> {
                    long startTime1 = System.currentTimeMillis();
                    Date dateToConvert = stockDataRepository.getLastUpdated(s.getSymbol());
                    long stopTime1 = System.currentTimeMillis();
                    log.debug("LastUpdate for Symbol [{}] was {}. Elapsed time: {}", s.getSymbol(), dateToConvert, (stopTime1 - startTime1));
                    LocalDateTime lastUpdate = null;
                    if(dateToConvert != null) {
                        lastUpdate = dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    }

                    if(lastUpdate == null || lastUpdate.isBefore(LocalDateTime.now())) {
                        long startTime2 = System.currentTimeMillis();
                        if (stockDataRepository.updateStock(s.getSymbol(), s.getDate(),
                                s.getOpen(), s.getClose(),
                                s.getHigh(), s.getLow(),
                                s.getVolume()) == 0) {
                            stockDataRepository.save(s);
                            long stopTime2 = System.currentTimeMillis();
                            log.debug("Saving StockData {}. Elapsed time: {}ms",  s.toString(),(stopTime2 - startTime2));
                        }
                    }
                    else {
                        log.info("Skipping StockData import feed. Already up-to-date for StockData SymbolData [{}] Last update date: [{}]", s.getSymbol(), lastUpdate);
                    }
                });
            }
        }
        long stopTime0 = System.currentTimeMillis();
        log.debug("Finished Intraday Import. Elapsed time: {}ms",(stopTime0 - startTime0));
    }


//    @PostConstruct
//    public void init() {
//        log.info("Starting StockDataFeeder");
//        start(StockDataRestClient.TimeSeries.DAILY);
//    }

    public static void main (String[]args){
            IntradayBatchFeeder f = new IntradayBatchFeeder();
            f.start();
        }

    }
