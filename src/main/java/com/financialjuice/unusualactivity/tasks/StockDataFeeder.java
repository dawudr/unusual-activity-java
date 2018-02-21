package com.financialjuice.unusualactivity.tasks;

import com.financialjuice.unusualactivity.model.StockData;
import com.financialjuice.unusualactivity.model.Symbol;
import com.financialjuice.unusualactivity.repository.StockDataRepository;
import com.financialjuice.unusualactivity.repository.StockDataRestClient;
import com.financialjuice.unusualactivity.repository.SymbolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Javax’s @PostConstruct annotation can be used for annotating a method that should be run once immediately after the bean’s initialization. Keep in mind that the annotated method will be executed by Spring even if there is nothing to inject.
 * See http://www.baeldung.com/running-setup-logic-on-startup-in-spring
 */

@Component
public class StockDataFeeder {

    private static final Logger log = LoggerFactory.getLogger(StockDataFeeder.class);

    @Autowired
    private StockDataRestClient stockDataRestClient;

    @Autowired
    private StockDataRepository stockDataRepository;

    @Autowired
    private SymbolRepository symbolRepository;


    /**
     * Stock feeder
     *
     * @param t
     */
    public void start(StockDataRestClient.TimeSeries t) {
        log.info("Importing StockData over timeseries[{}]", t);

        List<Symbol> ls = symbolRepository.findLSESymbols();
        ls.forEach(s -> {

                    Date lastUpdate = stockDataRepository.getLastUpdated(s.getSymbol());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    if (lastUpdate == null || !sdf.format(lastUpdate).equals(sdf.format(new Date()))) {
                        importFeed(s.getSymbol(), t);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            log.info("Error reducing frequency for HTTP request for symbol:[{}] Error:[{}]", s.getSymbol(), e.getMessage());
                        }
                    } else {
                        log.info("Skipping StockData import feed. Already up-to-date for StockData Symbol [{}] Last update date: [{}]", s.getSymbol(), lastUpdate);
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
        log.info("Importing StockData Symbol [{}] over timeseries[{}]", symbol, t);

        List<StockData> l = stockDataRestClient.getStockData(symbol, t);
        if(l !=null && !l.isEmpty()) {
            l.forEach(s -> {
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

    public static void main(String[] args) {
        StockDataFeeder f = new StockDataFeeder();
        f.start(StockDataRestClient.TimeSeries.DAILY);
    }

}
