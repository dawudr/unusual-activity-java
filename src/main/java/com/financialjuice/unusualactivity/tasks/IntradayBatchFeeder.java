package com.financialjuice.unusualactivity.tasks;

import com.financialjuice.unusualactivity.model.StockData;
import com.financialjuice.unusualactivity.model.SymbolData;
import com.financialjuice.unusualactivity.repository.StockDataDTORepository;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.groupingBy;

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
    private StockDataDTORepository stockDataDTORepository;

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
        log.debug("Importing Intraday Batch Stockdata for {} Symbols", ls.size());
        importFeed(ls);

    }


    /**
     * Removes bad fields and zero volume
     * Removes duplicates that are already in database
     * @param input
     * @return
     */
    private List<StockData> cleanFeed(List<StockData> input) {
        long startTime = System.currentTimeMillis();
        List<StockData> output = new ArrayList<>();

        if (input != null && !input.isEmpty()) {
            // Group by Symbols as Stock data is per symbol with several data point per minute for day
            Map<String, List<StockData>> grouped =
                    input.stream()
                    .collect(groupingBy(StockData::getSymbol));

            List<List<StockData>> deduped =
                grouped.entrySet()
                    .stream()
                    .map(s -> {
                        LocalDateTime lastUpdate = getLastUpdate(s.getKey());
                        List<StockData> cleaned = new ArrayList<>();
                        s.getValue()
                                .stream()
                                .filter(stockData -> lastUpdate == null ||
                                        lastUpdate.isBefore(stockData.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()))
                                .collect(Collectors.toList())
                                .forEach(cleaned::add);
                        return cleaned;
                    }).collect(Collectors.toList());

            output = deduped.get(0);
        }
        log.debug("Cleaned StockData. Input size: [{}] -> Output size [{}]. Elapsed time: {}ms", input.size(), output.size(), (System.currentTimeMillis() - startTime));
        return output;
    }


    /**
     * Import stock feeder
     *
     * @param symbols
     */
    private void importFeed(List<SymbolData> symbols) {
        log.info("Importing StockData for [{}] symbols", symbols.size());
        long startTime0 = System.currentTimeMillis();

        // Split entire list of stocks into partition batch of 100 (due to API limit)
        List<List<SymbolData>> partitions = getSymbolPartitions(symbols);

        // Process each partition
        int batchNo = 0;
        for(List<SymbolData> p : partitions) {
            String batchName =  String.format("BatchNo: [%s of %s] with Symbols [%s -> %s]", batchNo, partitions.size(), p.get(0).getSymbol(), p.get(partitions.size()).getSymbol());
            log.info("Processing {}", batchName);
            batchNo++;

            Thread thread = new Thread(new Runnable() {
                public void run()
                {
                    long startTime1 = System.currentTimeMillis();
                    // Convert ArrayList<Symbol> to a csv parameter list of symbols for HTTP Rest request
                    String csv = p.stream()
                            .map(SymbolData::getSymbol)
                            .collect(Collectors.joining(","));

                    // Get data points for each batch of 100 symbols
                    List<StockData> intradayRaw = intradayBatchRestClient.getIntradayData(csv);

                    // Clean and de-duplicate
                    //List<StockData> intraday = cleanFeed(intradayRaw);
                    //log.debug("{} - Cleaned StockData. Before [{}] -> After [{}] records", batchName, intradayRaw.size(), intraday.size());

                    // Save to database as batch
                    stockDataDTORepository.batchUpdate(intradayRaw);

                    log.debug("{} - Completed import. Elapsed time: {}ms", batchName, (System.currentTimeMillis() - startTime1));
                }
            });
            thread.start();

        }
        log.debug("Finished Intraday Import. Elapsed time: {}ms", (System.currentTimeMillis() - startTime0));
    }


    private LocalDateTime getLastUpdate(String symbol) {
        Date dateToConvert = stockDataRepository.getLastUpdated(symbol);
        LocalDateTime lastUpdate = null;
        if(dateToConvert != null) {
            lastUpdate = dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
//        log.debug("LastUpdate for Symbol [{}] was {}. Elapsed time: {}", symbol, dateToConvert, (stopTime1 - startTime1));
        return lastUpdate;
    }


    private List<List<SymbolData>> getSymbolPartitions(List<SymbolData> symbols) {
        int batchSize = 100;
        // Limit of API batch symbols is 100 so divide
        List<List<SymbolData>> partitions = IntStream.range(0, ((symbols.size() -1) / batchSize))
                .mapToObj(n -> symbols.subList(n * batchSize, Math.min((n + 1) * batchSize, symbols.size())))
                .collect(Collectors.toList());
        log.info("Partitioning StockData feed into [{}] batches of batch size: {}", partitions.size(), batchSize);
        return partitions;
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
