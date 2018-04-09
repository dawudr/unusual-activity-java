package com.financialjuice.unusualactivity.tasks;

import com.financialjuice.unusualactivity.model.SymbolData;
import com.financialjuice.unusualactivity.repository.SymbolRepository;
import com.financialjuice.unusualactivity.rest.SymbolRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Javax’s @PostConstruct annotation can be used for annotating a method that should be run once immediately after the bean’s initialization. Keep in mind that the annotated method will be executed by Spring even if there is nothing to inject.
 * See http://www.baeldung.com/running-setup-logic-on-startup-in-spring
 */

@Component
public class SymbolFeeder implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(SymbolFeeder.class);

    @Autowired
    private SymbolRestClient symbolRestClient;

    @Autowired
    private SymbolRepository symbolRepository;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private ApplicationContext applicationContext;


    @Override
    public void run() {
        log.info("Running SymbolFeeder Thread");
        this.start();
    }

    public void executeAsynchronously() {
        SymbolFeeder symbolFeeder = applicationContext.getBean(SymbolFeeder.class);
        taskExecutor.execute(symbolFeeder);

    }

    public void start() {

        log.info("Started SymbolFeeder");
        importFeed();
    }

    /**
     * Import symbols
     *
     */
    private void importFeed() {
        log.info("Importing Symbols");

        List<SymbolData> symbols = symbolRestClient.getSymbols();
        if (symbols != null && !symbols.isEmpty()) {

            symbols.forEach(s -> {
                log.debug("Adding Symbol: {}", s.toString());
                symbolRepository.save(s);
            });
        }

    }


//    @PostConstruct
//    public void init() {
//        log.info("Starting StockDataFeeder");
//        start(StockDataRestClient.TimeSeries.DAILY);
//    }

        public static void main (String[]args){
            SymbolFeeder f = new SymbolFeeder();
            f.start();
        }

    }
