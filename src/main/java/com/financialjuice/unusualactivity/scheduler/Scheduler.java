package com.financialjuice.unusualactivity.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.financialjuice.unusualactivity.tasks.IntradayBatchFeeder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class IntradayScheduler {

    private static final Logger log = LoggerFactory.getLogger(IntradayScheduler.class);

    @Autowired
    private IntradayBatchFeeder intradayBatchFeeder;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Scheduled(cron = "${intraday.batch.cron.expression}")
    public void runIntradayBatchFeederTask() {
        log.debug("Scheduled run: IntradayBatch Feeder on {}", dateFormat.format(new Date()));
        intradayBatchFeeder.executeAsynchronously();
    }
}