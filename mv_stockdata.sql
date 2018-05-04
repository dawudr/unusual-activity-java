USE UNUSUALACTIVITY; 
SELECT * FROM stockdata WHERE symbol = 'AAPL';
SELECT * FROM stockdata WHERE symbol = 'AAPL' AND 
date > '2018-04-13 09:30:00' AND date < '2018-04-13 10:00:00';

SELECT AVG(volume) FROM stockdata WHERE symbol = 'AAPL' 
AND date > '2018-04-13 09:30:00' AND date < '2018-04-13 10:00:00';


SET @top = (SELECT MAX(volume) FROM stockdata WHERE symbol = 'AAPL');
SET @mean = (SELECT AVG(volume) FROM stockdata WHERE symbol = 'AAPL');
SET @stdev = (SELECT STD(volume) FROM stockdata WHERE symbol = 'AAPL');
SELECT *, FN_GAUSS_CDF(@mean, @stdev, volume) * 100 AS nd_percentage FROM stockdata WHERE symbol = 'AAPL' 
AND date > '2018-04-13 09:30:00' AND date < '2018-04-13 10:00:00'
ORDER BY date DESC;


SELECT FN_GAUSS_CDF(@mean, @stdev, @top);


SELECT 
    volume, date, DATE_FORMAT(date, '%H:%i:%s') AS TIMEONLY
FROM
    stockdata
WHERE
    symbol = 'AAPL'
        AND TIMEONLY = '09:46:00';


-- Get 30 Day View
DROP VIEW IF EXISTS mv_stockdata;
CREATE VIEW mv_stockdata AS
SELECT 
    st.symbol,
    date,
    DATE_FORMAT(date, '%Y-%m-%d') AS date_part,
    DATE_FORMAT(date, '%H:%i:%s') AS time_part,
    volume,
    high,
    low,
    (high - low) AS p_range
FROM
    unusualactivity.stockdata st
JOIN unusualactivity.symboldata sy ON st.symbol = sy.symbol
WHERE 
    date BETWEEN DATE_SUB(NOW(), INTERVAL 30 DAY) AND NOW()
    AND sy.datatype = 'Common Stock'
    AND sy.symbol NOT IN (SELECT distinct(symbol) FROM stockdata WHERE open = close);

    
-- Aggregate 30 days view by Time and show only Todays Stats ONLY
DROP VIEW IF EXISTS mv_stats;
CREATE VIEW mv_stats AS
    SELECT 
        MAX(date_part) AS date,
        time_part AS time,
        symbol,
        AVG(volume) AS vol_MEAN,
        STD(volume) AS vol_STDEV,
        MIN(volume) AS vol_MIN,
        MAX(volume) AS vol_MAX,
        FN_GAUSS_CDF(AVG(volume),
                STD(volume),
                (SELECT 
                        volume
                    FROM
                        unusualactivity.mv_stockdata
                    WHERE
                        symbol = st.symbol
                    ORDER BY date_part DESC
                    LIMIT 1)) AS vol_NORMALDIST,
        AVG(p_range) AS p_range_MEAN,
        STD(p_range) AS p_range_STDEV,
        MIN(p_range) AS p_range_MIN,
        MAX(p_range) AS p_range_MAX,
        FN_GAUSS_CDF(AVG(p_range),
                STD(p_range),
                (SELECT 
                        p_range
                    FROM
                        unusualactivity.mv_stockdata
                    WHERE
                        symbol = st.symbol
                    ORDER BY date_part DESC
                    LIMIT 1)) AS p_range_NORMALDIST,
        COUNT(volume) AS SAMPLE_SIZE
    FROM
        unusualactivity.mv_stockdata st
    GROUP BY symbol , time_part;



-- TESTS
select * from mv_stockdata WHERE symbol = 'AAPL';
select COUNT(*) from mv_stockdata;

SELECT * FROM mv_stats WHERE symbol = 'AAPL';
SELECT * FROM mv_stats WHERE symbol = 'AAPL' AND date = '2018-04-24' AND time = '09:42:00';
select COUNT(*) from mv_stats;
