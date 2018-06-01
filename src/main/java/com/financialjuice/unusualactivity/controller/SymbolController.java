package com.financialjuice.unusualactivity.controller;

import com.financialjuice.unusualactivity.model.StatsGrid;
import com.financialjuice.unusualactivity.model.StockData;
import com.financialjuice.unusualactivity.model.SymbolData;
import com.financialjuice.unusualactivity.repository.StatsGridCacheRepository;
import com.financialjuice.unusualactivity.repository.StockDataRepository;
import com.financialjuice.unusualactivity.repository.SymbolRepository;
import com.financialjuice.unusualactivity.services.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@CrossOrigin("*")
@RestController    // This means that this class is a Controller
@RequestMapping(path = "api/symbol") // This means URL's start with /stock (after Application path)
public class SymbolController {

    private static final Logger log = LoggerFactory.getLogger(SymbolController.class);

    @Autowired
    private SymbolRepository symbolRepository;

    @RequestMapping(value="", method = RequestMethod.GET)
    public List<SymbolData> getAllSymbolData() {
        // This returns a JSON or XML
        Iterable<SymbolData> iterator = symbolRepository.findAll();
        List<SymbolData> ls = StreamSupport
                .stream(iterator.spliterator(), true)
                .collect(Collectors.toList());
        return ls;
    }

    // Get a Single StockData
    @RequestMapping(value = "/{symbol}", method = RequestMethod.GET)
    public SymbolData getStockDataBySymbol(@PathVariable("symbol") String symbol) {
        return symbolRepository.findOne(symbol);
    }





}
