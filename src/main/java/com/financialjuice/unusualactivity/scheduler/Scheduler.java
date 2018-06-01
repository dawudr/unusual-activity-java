package com.financialjuice.unusualactivity.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.financialjuice.unusualactivity.tasks.IntradayBatchFeeder;
import com.financialjuice.unusualactivity.tasks.RealtimeBatchFeeder;
import com.financialjuice.unusualactivity.tasks.StatsGridBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {

    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

    @Autowired
    private IntradayBatchFeeder intradayBatchFeeder;

    @Autowired
    private RealtimeBatchFeeder realtimeBatchFeeder;

    @Autowired
    private StatsGridBuilder statsGridBuilder;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Scheduled(cron = "${intraday.batch.cron.expression}")
    public void runIntradayBatchFeederTask() {
        log.debug("Scheduled run: IntradayBatch Feeder on {}", dateFormat.format(new Date()));
        intradayBatchFeeder.executeAsynchronously();
    }

    @Scheduled(cron = "${realtime.batch.cron.expression}")
    public void runRealtimeBatchFeederTask() {
        log.debug("Scheduled run: RealtimeBatch Feeder on {}", dateFormat.format(new Date()));
        realtimeBatchFeeder.executeAsynchronously();
    }

/*    @Scheduled(cron = "${statsgrid.batch.cron.expression}")
    public void runStatsGridBuilderTask() {
        if(!runStatsGridAndRealtimeTogether) {
            log.debug("Scheduled run: StatsGridBuilder Feeder on {}", dateFormat.format(new Date()));
            statsGridBuilder.executeAsynchronously();
        }
    }*/
}