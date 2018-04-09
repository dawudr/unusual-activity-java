package com.financialjuice.unusualactivity.controller;

import com.financialjuice.unusualactivity.batch.SymbolImporterJobConfiguration;
import com.financialjuice.unusualactivity.batch.SymbolNYSEImporterJobConfiguration;
import com.financialjuice.unusualactivity.rest.StockDataRestClient;
import com.financialjuice.unusualactivity.tasks.IntradayBatchFeeder;
import com.financialjuice.unusualactivity.tasks.IntradayFeeder;
import com.financialjuice.unusualactivity.tasks.StockDataFeeder;
import com.financialjuice.unusualactivity.tasks.SymbolFeeder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// @CrossOrigin(origins = "http://localhost:4200")
@RestController    // This means that this class is a Controller
@CrossOrigin("*")
@RequestMapping(path="api/import") // This means URL's start with /stock (after Application path)
public class ImportController {

    private static final Logger log = LoggerFactory.getLogger(ImportController.class);

    @Value("${file.import.companies.lse}")
    private String lse_file_csv;

    @Value("${file.import.companies.nyse}")
    private String lse_file_nyse;

    @Value("${file.import.companies.nasdaq}")
    private String lse_file_nasdaq;

    @Autowired
    SymbolImporterJobConfiguration symbolImporterJobConfiguration;

    @Autowired
    SymbolNYSEImporterJobConfiguration symbolNYSEImporterJobConfiguration;

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    private StockDataFeeder stockDataFeeder;

    @Autowired
    private IntradayFeeder intradayFeeder;

    @Autowired
    private IntradayBatchFeeder intradayBatchFeeder;

    @Autowired
    private SymbolFeeder symbolFeeder;

    @RequestMapping("/lse")
    public String importLSE() throws Exception{
        log.debug("Started importLSE from HTTP request");
        JobParameters jobParameters =
                new JobParametersBuilder()
                        .addLong("time",System.currentTimeMillis())
                        .addString("exchange", "LSE")
                        .addString("fileName", lse_file_csv)
                        .toJobParameters();
        JobExecution execution_lse = jobLauncher.run(symbolImporterJobConfiguration.job_SymbolImporter(), jobParameters);

        StringBuilder executionStatus = new StringBuilder();
        executionStatus.append(("Importing Companies. Exchange: LSE ExitStatus : " + execution_lse.getStatus()));
        return executionStatus.toString();
    }

    @RequestMapping("/nyse")
    public String importNYSE() throws Exception{
        log.debug("Started importNYSE from HTTP request");
        JobParameters jobParameters =
                new JobParametersBuilder()
                        .addLong("time",System.currentTimeMillis())
                        .addString("exchange", "NYSE")
                        .addString("fileName", lse_file_nyse)
                        .toJobParameters();
        JobExecution execution_nyse = jobLauncher.run(symbolNYSEImporterJobConfiguration.job_SymbolImporter(), jobParameters);

        StringBuilder executionStatus = new StringBuilder();
        executionStatus.append(("Importing Companies. Exchange: NYSE ExitStatus : " + execution_nyse.getStatus()));
        return executionStatus.toString();
    }

    @RequestMapping("/symbols")
    public String importSymbols() {
        log.debug("Started Symbols Feeder from HTTP request");
        symbolFeeder.executeAsynchronously();
        return "OK";
    }

    @RequestMapping("/daily")
    public String importDailyQuotes() {
        log.debug("Started StockFeeder from HTTP request");
        stockDataFeeder.executeAsynchronously(StockDataRestClient.TimeSeries.DAILY);

        return "OK";
    }

    @RequestMapping("/intraday")
    public String importIntradayQuotes() {
        log.debug("Started Intraday Feeder from HTTP request");
        intradayFeeder.executeAsynchronously();

        return "OK";
    }

    @RequestMapping("/realtime")
    public String importRealTimeQuotes() {
        log.debug("Started IntradayBatch Feeder from HTTP request");
        intradayBatchFeeder.executeAsynchronously();

        return "OK";
    }
}