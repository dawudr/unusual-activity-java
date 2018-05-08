package com.financialjuice.unusualactivity.repository;

import com.financialjuice.unusualactivity.dto.StatsDTO;
import com.financialjuice.unusualactivity.dto.StockDataDTO;
import com.financialjuice.unusualactivity.model.StockData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class StockDataDTORepository {
    private static final Logger log = LoggerFactory.getLogger(StockDataDTORepository.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // AlertsController - For Latest realtime intraday data point per Symbol.
    @Transactional(readOnly = true)
    public List<StockDataDTO> findRealtimeStockData(Date date) {
        long startTime = System.currentTimeMillis();

        log.debug("Fetching Stock ON date {}", date);
//        String sql = "SELECT sd, sy.name FROM StockData sd JOIN SymbolData sy WHERE sd.date = " + date.toString() + " ORDER BY sd.symbol";
        String sql = "SELECT *, sy.name FROM dbo.stockdata sd INNER JOIN symboldata sy ON sd.symbol = sy.symbol WHERE sd.date = '" + date.toString() + "' ORDER BY sd.symbol";
        List <StockDataDTO> stocks = new ArrayList();

        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.query(sql, new ResultSetExtractor() {
            public List extractData(ResultSet rs) throws SQLException {

                while (rs.next()) {
                    StockDataDTO dto = new StockDataDTO(
                            rs.getDate("date"),
                            rs.getString("symbol"),
                            rs.getDouble("open"),
                            rs.getDouble("close"),
                            rs.getDouble("high"),
                            rs.getDouble("low"),
                            rs.getLong("volume"),
                            rs.getString("name")
                    );
                    stocks.add(dto);
                }
                log.debug("Found [{}] stocks on date {} Elapsed time: {}ms", stocks.size(), date, (System.currentTimeMillis() - startTime));
                return stocks;
            }
        });

        return stocks;
    };


    @Transactional(readOnly = true)
    public List<StatsDTO> findRealtimeStats(int minutesAgo, String symbol, double ndvol, double ndprange, boolean realtime) {


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime timeNow = LocalDateTime.now();
        LocalDateTime timePast = timeNow.minusMinutes(minutesAgo);
        ZonedDateTime timeZoneLocal = timeNow.atZone(ZoneId.systemDefault());
        ZonedDateTime pastZoneLocal = timePast.atZone(ZoneId.systemDefault());

        ZoneId timeZoneUS= ZoneId.of("America/New_York");
        ZonedDateTime timeNowUS = timeZoneLocal.withZoneSameInstant(timeZoneUS);
        ZonedDateTime timePastUS = pastZoneLocal.withZoneSameInstant(timeZoneUS);
        String timeNowStr = timeNowUS.format(formatter);
        String timePastStr = timePastUS.format(formatter);

        log.debug("Fetching Stats for {} minutes ago at time[{}] to time now[{}] for date[{}] for Symbol:[{}] and NormalDistVol>[{}] and NormalDist_PRange>[{}] and Realtime [{}]", minutesAgo, timePastStr, timeNowStr, timeNow, symbol, ndvol, ndprange, realtime);


        String sql = null;
        if(symbol != null && !symbol.isEmpty()) {
            if(!realtime) {
                sql = "SELECT *, sy.name FROM UA.dbo.SymbolStats ss INNER JOIN UA.dbo.symboldata sy ON ss.symbol = sy.symbol WHERE ss.Time_Part > '" + timePastStr + "' AND ss.symbol = '" + symbol + "' AND ss.NormalDist > " + ndvol + " AND ss.NormalDist_PRange > " + ndprange + " ORDER BY ss.DateCreated";
            } else {
                sql = "SELECT *, sy.name FROM UA.dbo.SymbolStats ss INNER JOIN UA.dbo.symboldata sy ON ss.symbol = sy.symbol WHERE ss.Time_Part BETWEEN '" + timePastStr + "' AND '" + timeNowStr + "' AND ss.symbol = '" + symbol + "' AND ss.NormalDist > " + ndvol + " AND ss.NormalDist_PRange > " + ndprange + " ORDER BY ss.DateCreated";
            }
        } else if (!realtime) {
            sql = "SELECT *, sy.name FROM UA.dbo.SymbolStats ss INNER JOIN UA.dbo.symboldata sy ON ss.symbol = sy.symbol WHERE ss.Time_Part > '" + timePastStr + "' AND ss.NormalDist > " + ndvol + " AND ss.NormalDist_PRange > " + ndprange + " ORDER BY ss.DateCreated";
        } else {
            sql = "SELECT *, sy.name FROM UA.dbo.SymbolStats ss INNER JOIN UA.dbo.symboldata sy ON ss.symbol = sy.symbol WHERE ss.Time_Part BETWEEN '" + timePastStr + "' AND '" + timeNowStr + "' AND ss.NormalDist > " + ndvol + " AND ss.NormalDist_PRange > " + ndprange + " ORDER BY ss.DateCreated";
        }


        log.debug("Alert SQL [{}]", sql);

        List <StatsDTO> stats = new ArrayList();
        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.query(sql, new ResultSetExtractor() {
            public List extractData(ResultSet rs) throws SQLException {

                while (rs.next()) {
                    StatsDTO dto = new StatsDTO(
                            rs.getDate("DateCreated"),
                            rs.getString("Symbol"),
                            rs.getDouble("NormalDist"),
                            rs.getTime("Time_Part"),
                            rs.getDouble("NormalDist_PRange"),
                            rs.getDouble("PRange"),
                            rs.getLong("LatestVolume"),
                            rs.getString("name")
                    );
                    stats.add(dto);
                }
                log.debug("Found [{}] stats for minutes ago: {} for Symbol:[{}]", stats.size(), timePastStr, symbol);
                return stats;
            }
        });
        return stats;
    };



    // AlertsController - For Historic daily data points per Symbol
    @Deprecated
    @Transactional(readOnly = true)
    public List<StockDataDTO> findHistoricStockData(Date date) {
        log.debug("Fetching Stock AFTER date {}", date);

        String sql = "SELECT *, sy.name FROM dbo.stockdata sd INNER JOIN dbo.symboldata sy ON sd.symbol = sy.symbol WHERE sd.date > '" + date.toString() + "' ORDER BY sd.date";
        List<StockDataDTO> stocks = new ArrayList();

        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.query(sql, new ResultSetExtractor() {
            public List extractData(ResultSet rs) throws SQLException {

                while (rs.next()) {
                    StockDataDTO dto = new StockDataDTO(
                            rs.getDate("date"),
                            rs.getString("symbol"),
                            rs.getDouble("open"),
                            rs.getDouble("close"),
                            rs.getDouble("high"),
                            rs.getDouble("low"),
                            rs.getLong("volume"),
                            rs.getString("name")
                    );
                    stocks.add(dto);
                }
                log.debug("Found [{}] stocks after date {}", stocks.size(), date);
                return stocks;
            }
        });
        log.debug("Found [{}] stocks on date {}", stocks.size(), date);
        return stocks;
    }





    public int[] batchUpdate(final List<StockData> stocks) {
        log.debug("BatchUpdate [{}] stockdata feeds", stocks.size());
        long startTime = System.currentTimeMillis();

// TODO: Mysql Insert Make compatible
//        String sql = "INSERT INTO stockdata " +
//                "(date, symbol, open, close, high, low, volume ) VALUES (?, ?, ?, ?, ?, ?, ?)";
//        String sql = "INSERT INTO stockdata " +
//                "([date], [symbol], [open], [close], [high], [low], [volume], [date_part], [time_part]) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";


        String sql = "{call AddStockData(?, ?, ?, ?, ?, ?, ?)}";


        int[] updateCounts = jdbcTemplate.batchUpdate(sql,
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        log.debug("Batch Insert Stockdata: " + stocks.get(i).toString());
                        ps.setTimestamp(2, new Timestamp(stocks.get(i).getDate().getTime()));
                        ps.setString(1, stocks.get(i).getSymbol());
                        ps.setDouble(3, stocks.get(i).getOpen());
                        ps.setDouble(4, stocks.get(i).getClose());
                        ps.setDouble(6, stocks.get(i).getHigh());
                        ps.setDouble(5, stocks.get(i).getLow());
                        ps.setLong(7, stocks.get(i).getVolume());
//                        ps.setDate(8, new java.sql.Date(stocks.get(i).getDate_part().getTime()));
//                        ps.setTime(9, stocks.get(i).getTime_part());
                    }

                    public int getBatchSize() {
                        return stocks.size();
                    }
                } );

        log.debug("BatchUpdate Success for [{}] stocks. Elapsed time: {}ms", updateCounts.length, (System.currentTimeMillis() - startTime));
        return updateCounts;
    }
}