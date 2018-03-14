package com.financialjuice.unusualactivity.services;


import com.financialjuice.unusualactivity.model.StockData;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * For each stock work mean and std deviation
 */
@Component
public class StatisticsService {

    private static DecimalFormat df2 = new DecimalFormat(".##");


    public List<Map<String, Object>> getDataSeries(List<StockData> l) {

        // Group list by SymbolData
        Map<String, List<StockData>> grouped = l
                .stream()
                .collect(Collectors.groupingBy(StockData::getSymbol));

        // For Each Map of Group by SymbolData
        List<Map<String, Object>> computation = grouped
                .entrySet()
                .stream()
                .map(s -> {
                    // Get a new DescriptiveStatistics instance
                    DescriptiveStatistics stats = new DescriptiveStatistics();
                    s.getValue()
                            .stream()
                            .map(StockData::getVolume)
                            .collect(Collectors.toList())
                            .forEach(stats::addValue);
                    // Compute some statistics
                    return compute(s.getKey(), stats);
                })
                .collect(Collectors.toList());
        return computation;
    }


    private Map<String, Object> compute(String symbol, DescriptiveStatistics stats) {

        // Compute some statistics
        Map<String, Object> map = new HashMap();
        map.put("symbol", symbol);
        map.put("mean", stats.getMean());
        map.put("n", stats.getN());
        map.put("min", stats.getMin());
        map.put("max", stats.getMax());
        map.put("std dev", stats.getStandardDeviation());
        map.put("median", stats.getPercentile(50.0D));
        map.put("skewness", stats.getSkewness());
        map.put("kurtosis", stats.getKurtosis());
        return map;
    }



    /**
     *
     *         label: "Sample A",
     values: {
     Q1: 180,
     Q2: 200,
     Q3: 290,
     Q4: 270,
     whisker_low: 115,
     whisker_high: 400,
     outliers: [50, 100, 425]
     }
     * @return
     */

    public List<Map<String, Object>> getBoxPlotChartSeries(List<StockData> l) {

        Map<String, List<StockData>> grouped = l
                .stream()
                .collect(Collectors.groupingBy(StockData::getSymbol));

        List<Map<String, Object>> boxplot = grouped
                .entrySet()
                .stream()
                .map(s -> {
                    // Get a new DescriptiveStatistics instance
                    DescriptiveStatistics stats = new DescriptiveStatistics();
                    s.getValue()
                            .stream()
                            .map(StockData::getVolume)
                            .collect(Collectors.toList())
                            .forEach(stats::addValue);
                    return
                            // Compute some statistics
                            computeBoxPlotStats(s.getKey(), stats);
                })
                .collect(Collectors.toList());

        return boxplot;
    }


    private Map<String, Object> computeBoxPlotStats(String symbol, DescriptiveStatistics stats) {

        Map<String, Object> map = new HashMap();

        // Compute some statistics
        Map<String, Double> mapValues = new HashMap();
        mapValues.put("Q1", stats.getPercentile(25.0D));
        mapValues.put("Q2", stats.getPercentile(50.0D));
        mapValues.put("Q3", stats.getPercentile(75.0D));
        mapValues.put("whisker_low", stats.getMin());
        mapValues.put("whisker_high", stats.getMax());
        //TODO:
//        mapValues.put("outliers", "[]");
        map.put("values", mapValues);
        map.put("label", symbol);
        return map;
    }
}
