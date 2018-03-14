package com.financialjuice.unusualactivity.tasks;

import com.financialjuice.unusualactivity.model.StockData;
import com.financialjuice.unusualactivity.model.SymbolData;
import com.financialjuice.unusualactivity.repository.StockDataRepository;
import com.financialjuice.unusualactivity.repository.StockDataRestClient;
import com.financialjuice.unusualactivity.repository.SymbolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Javax’s @PostConstruct annotation can be used for annotating a method that should be run once immediately after the bean’s initialization. Keep in mind that the annotated method will be executed by Spring even if there is nothing to inject.
 * See http://www.baeldung.com/running-setup-logic-on-startup-in-spring
 */

@Component
public class StockDataFeeder implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(StockDataFeeder.class);

    @Autowired
    private StockDataRestClient stockDataRestClient;

    @Autowired
    private StockDataRepository stockDataRepository;

    @Autowired
    private SymbolRepository symbolRepository;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private ApplicationContext applicationContext;

    private StockDataRestClient.TimeSeries timeSeries;

    //TODO:
    /**
     * Parameter 0 of constructor in com.financialjuice.unusualactivity.tasks.StockDataFeeder required a bean of type 'com.financialjuice.unusualactivity.repository.StockDataRestClient$TimeSeries' that could not be found.
     Action:
     Consider defining a bean of type 'com.financialjuice.unusualactivity.repository.StockDataRestClient$TimeSeries' in your configuration.
     */
//    public StockDataFeeder(StockDataRestClient.TimeSeries timeSeries) {
//        this.timeSeries = timeSeries;
//    }

    @Override
    public void run() {
        log.info("Running StockDataFeeder Thread");
        this.start();
    }

    public void executeAsynchronously(StockDataRestClient.TimeSeries t) {
        StockDataFeeder stockDataFeeder = applicationContext.getBean(StockDataFeeder.class);
        stockDataFeeder.setTimeSeries(t);
        taskExecutor.execute(stockDataFeeder);

    }

    /**
     * Stock feeder
     *
     */
    public void start() {

        log.info("Started StockDataFeeder with timeseries[{}]", this.timeSeries);

        List<SymbolData> ls = symbolRepository.findSymbolsByExchange("NYSE");
        ls.forEach(s -> {

            Date dateToConvert = stockDataRepository.getLastUpdated(s.getSymbol());

            // Import if database is empty or if up-to-date
            if (dateToConvert == null) {
                log.info("Importing feed. New data");
                importFeed(s.getSymbol(), this.timeSeries);

                try {
                    Thread.sleep(1050);
                } catch (InterruptedException e) {
                    log.info("Error reducing frequency for HTTP request for symbol:[{}] Error:[{}]", s.getSymbol(), e.getMessage());
                }
            } else {
                LocalDate lastUpdate = dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate today = LocalDate.now();

                if(lastUpdate.isBefore(today)) {
                    log.info("Importing feed. LastUpdated: {} Current date: {}", lastUpdate, today);
                    importFeed(s.getSymbol(), this.timeSeries);

                    try {
                        Thread.sleep(1050);
                    } catch (InterruptedException e) {
                        log.info("Error reducing frequency for HTTP request for symbol:[{}] Error:[{}]", s.getSymbol(), e.getMessage());
                    }
                } else {
                    log.info("Skipping StockData import feed. Already up-to-date for StockData SymbolData [{}] Last update date: [{}]", s.getSymbol(), lastUpdate);
                }
            }

        }
        );
    }

    /**
     * Import stock feeder
     *
     * @param symbol
     * @param t
     */
    private void importFeed(String symbol, StockDataRestClient.TimeSeries t) {
        log.info("Importing StockData SymbolData [{}] over timeseries[{}]", symbol, t);

            switch (t) {
                case DAILY:
                    List<StockData> daily = stockDataRestClient.getStockData(symbol, t);
                    if(daily !=null && !daily.isEmpty()) {
                        daily.forEach(s -> {
                                    if (stockDataRepository.updateStock(s.getSymbol(), s.getDate(),
                                            s.getOpen(), s.getClose(),
                                            s.getHigh(), s.getLow(),
                                            s.getVolume()) == 0) {
                                        stockDataRepository.save(s);
                                    }
                                }
                        );
                    }
                    break;
                case INTRADAY:
                    List<StockData> intraday = stockDataRestClient.getStockData(symbol, t);
                    if(intraday !=null && !intraday.isEmpty()) {
                        intraday.forEach(s -> {
                                    if (stockDataRepository.updateStock(s.getSymbol(), s.getDate(),
                                            s.getOpen(), s.getClose(),
                                            s.getHigh(), s.getLow(),
                                            s.getVolume()) == 0) {
                                        stockDataRepository.save(s);
                                    }
                                }
                        );
                    }
                    break;
                default:
                    List<StockData> other = stockDataRestClient.getStockData(symbol, t);
                    other.forEach(s -> {
                                if (stockDataRepository.updateStock(s.getSymbol(), s.getDate(),
                                        s.getOpen(), s.getClose(),
                                        s.getHigh(), s.getLow(),
                                        s.getVolume()) == 0) {
                                    stockDataRepository.save(s);
                                }
                            }
                    );
            }
    }


//    @PostConstruct
//    public void init() {
//        log.info("Starting StockDataFeeder");
//        start(StockDataRestClient.TimeSeries.DAILY);
//    }


    public StockDataRestClient.TimeSeries getTimeSeries() {
        return timeSeries;
    }

    public void setTimeSeries(StockDataRestClient.TimeSeries timeSeries) {
        this.timeSeries = timeSeries;
    }

    public static void main(String[] args) {
        StockDataFeeder f = new StockDataFeeder();
        f.setTimeSeries(StockDataRestClient.TimeSeries.DAILY);
        f.start();
    }

}
