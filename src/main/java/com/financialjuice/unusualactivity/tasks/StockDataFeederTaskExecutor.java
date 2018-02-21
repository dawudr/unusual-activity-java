package com.financialjuice.unusualactivity.tasks;


import com.financialjuice.unusualactivity.repository.StockDataRestClient;
import org.springframework.context.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * ThreadPoolTaskExecutor This implementation is the most commonly used one. It exposes bean properties for configuring a java.util.concurrent.ThreadPoolExecutor and wraps it in a TaskExecutor. If you need to adapt to a different kind of java.util.concurrent.Executor, it is recommended that you use a ConcurrentTaskExecutor instead.
 */

@Service
public class StockDataFeederTaskExecutor {

    private static final Logger log = LoggerFactory.getLogger(StockDataFeederTaskExecutor.class);

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private ApplicationContext applicationContext;

    @Component
    private class StockDataFeederTask implements Runnable {
        
        @Autowired
        StockDataFeeder stockDataFeeder;

        @Override
        public void run() {

            log.info("Starting StockDataFeederTask Thread");
            stockDataFeeder.start(StockDataRestClient.TimeSeries.DAILY);
        }
    }    
    
    public void executeAsynchronously() {
        log.debug("Starting StockDataFeederTaskExecutor");
        StockDataFeederTask stockDataFeederTask = applicationContext.getBean(StockDataFeederTask.class);
        taskExecutor.execute(stockDataFeederTask);
    }


}
