package com.financialjuice.unusualactivity.controller;

import com.financialjuice.unusualactivity.tasks.IntradayFeeder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// @CrossOrigin(origins = "http://localhost:4200")
@CrossOrigin("*")
@RestController    // This means that this class is a Controller
@RequestMapping(path="api/intraday") // This means URL's start with /stock (after Application path)
public class IntradayController {

    private static final Logger log = LoggerFactory.getLogger(IntradayController.class);

    @Autowired
    private IntradayFeeder intradayFeeder;

    @RequestMapping("/import/start")
    public String executeAsync() {
        log.debug("Started Intraday Feeder from HTTP request");
        intradayFeeder.executeAsynchronously();

        return "OK";
    }

}
