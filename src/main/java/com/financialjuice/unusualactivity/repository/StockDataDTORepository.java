package com.financialjuice.unusualactivity.repository;

import com.financialjuice.unusualactivity.dto.StockDataDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
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

        log.debug("Fetching Stock ON date {}", date);
//        String sql = "SELECT sd, sy.name FROM StockData sd JOIN SymbolData sy WHERE sd.date = " + date.toString() + " ORDER BY sd.symbol";
        String sql = "SELECT *, sy.name FROM stockdata sd INNER JOIN symboldata sy ON sd.symbol = sy.symbol WHERE sd.date = '" + date.toString() + "' ORDER BY sd.symbol";
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
                log.debug("Found [{}] stocks on date {}", stocks.size(), date);
                return stocks;
            }
        });
        return stocks;
    };




    // AlertsController - For Historic daily data points per Symbol
    @Transactional(readOnly = true)
    public List<StockDataDTO> findHistoricStockData(Date date) {
        log.debug("Fetching Stock AFTER date {}", date);

        String sql = "SELECT *, sy.name FROM stockdata sd INNER JOIN symboldata sy ON sd.symbol = sy.symbol WHERE sd.date > '" + date.toString() + "' ORDER BY sd.date";
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
}