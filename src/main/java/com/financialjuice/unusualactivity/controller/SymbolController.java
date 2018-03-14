package com.financialjuice.unusualactivity.controller;

import com.financialjuice.unusualactivity.batch.SymbolImporterJobConfiguration;
import com.financialjuice.unusualactivity.batch.SymbolNYSEImporterJobConfiguration;
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

@CrossOrigin("*")
@RestController    // This means that this class is a Controller
@RequestMapping(path="api/symbol") // This means URL's start with /stock (after Application path)
public class SymbolController {

    private static final Logger log = LoggerFactory.getLogger(SymbolController.class);

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

    @RequestMapping("/import/lse")
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

    @RequestMapping("/import/nyse")
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
}