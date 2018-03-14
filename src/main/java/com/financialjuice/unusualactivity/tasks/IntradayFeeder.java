package com.financialjuice.unusualactivity.tasks;

import com.financialjuice.unusualactivity.model.StockData;
import com.financialjuice.unusualactivity.model.SymbolData;
import com.financialjuice.unusualactivity.repository.IntradayRestClient;
import com.financialjuice.unusualactivity.repository.StockDataRepository;
import com.financialjuice.unusualactivity.repository.SymbolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

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

        List<SymbolData> ls = symbolRepository.findSymbolsByExchange("NYSE");
        ls.forEach(s -> {

                        importFeed(s.getSymbol());
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            log.info("Error reducing frequency for HTTP request for symbol:[{}] Error:[{}]", s.getSymbol(), e.getMessage());
//                        }
                }
        );
    }

    /**
     * Import stock feeder
     *
     * @param symbol
     */
    private void importFeed(String symbol) {
        log.info("Importing StockData SymbolData [{}]", symbol);

        List<StockData> intraday = intradayRestClient.getIntradayData(symbol);
        if (intraday != null && !intraday.isEmpty()) {

            intraday.forEach(s -> {
                LocalDateTime lastUpdate = stockDataRepository.getLastUpdated(symbol).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();;
                if (lastUpdate == null || lastUpdate.compareTo(s.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()) < 0) {

                    if (stockDataRepository.updateStock(s.getSymbol(), s.getDate(),
                            s.getOpen(), s.getClose(),
                            s.getHigh(), s.getLow(),
                            s.getVolume()) == 0) {
                        stockDataRepository.save(s);
                    }

                } else {
                    log.info("Skipping StockData import feed. Already up-to-date for StockData SymbolData [{}] Last update date: [{}]", s.getSymbol(), lastUpdate);
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
