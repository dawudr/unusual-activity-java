package com.financialjuice.unusualactivity.tasks;

import com.financialjuice.unusualactivity.model.StockData;
import com.financialjuice.unusualactivity.model.SymbolData;
import com.financialjuice.unusualactivity.repository.StockDataRepository;
import com.financialjuice.unusualactivity.repository.SymbolRepository;
import com.financialjuice.unusualactivity.rest.IntradayRestClient;
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
import java.util.stream.StreamSupport;

/**
 * Javax’s @PostConstruct annotation can be used for annotating a method that should be run once immediately after the bean’s initialization. Keep in mind that the annotated method will be executed by Spring even if there is nothing to inject.
 * See http://www.baeldung.com/running-setup-logic-on-startup-in-spring
 */

@Component
public class IntradayFeeder implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(IntradayFeeder.class);

    @Autowired
    private IntradayRestClient intradayRestClient;

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
        IntradayFeeder intradayFeeder = applicationContext.getBean(IntradayFeeder.class);
        taskExecutor.execute(intradayFeeder);
    }

    /**
     * IntradayFeeder
     */
    public void start() {

        log.info("Started IntradayFeeder");

        Iterable<SymbolData> iterator = symbolRepository.findAll();
        List<SymbolData> ls = StreamSupport
                .stream(iterator.spliterator(), true)
                .collect(Collectors.toList());
        log.debug("Importing Intraday Stockdata for {} Symbols", ls.size());
        ls.forEach(s -> {
//                    Thread thread = new Thread(new Runnable() {
//                        public void run()
//                        {
//                            importFeed(s.getSymbol());
//                        }
//                    });
//                    thread.start();

                    importFeed(s.getSymbol());

                }
        );
    }

    /**
     * Import stock feeder
     *
     * @param symbol
     */
    private void importFeed(String symbol) {
        log.debug("Importing StockData SymbolData [{}]", symbol);

        List<StockData> intraday = intradayRestClient.getIntradayData(symbol);
        if (intraday != null && !intraday.isEmpty()) {

            intraday.forEach(s -> {
                Date dateToConvert = stockDataRepository.getLastUpdated(s.getSymbol());
                LocalDateTime lastUpdate = null;
                if(dateToConvert != null) {
                    lastUpdate = dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                }

                if(lastUpdate == null || lastUpdate.isBefore(LocalDateTime.now())) {

                    if (stockDataRepository.updateStock(s.getSymbol(), s.getDate(),
                            s.getOpen(), s.getClose(),
                            s.getHigh(), s.getLow(),
                            s.getVolume()) == 0) {
                        stockDataRepository.save(s);
                    }

                } else {
                    log.debug("Skipping StockData import feed. Already up-to-date for StockData SymbolData [{}] Last update date: [{}]", s.getSymbol(), lastUpdate);
                }
            });
        }
    }


//    @PostConstruct
//    public void init() {
//        log.info("Starting StockDataFeeder");
//        start(StockDataRestClient.TimeSeries.DAILY);
//    }

        public static void main (String[]args){
            IntradayFeeder f = new IntradayFeeder();
            f.start();
        }

    }
