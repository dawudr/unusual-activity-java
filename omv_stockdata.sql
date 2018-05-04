USE UNUSUALACTIVITY; 

-- Get 30 Day View
DROP VIEW IF EXISTS omv_stockdata;
CREATE VIEW omv_stockdata AS
    SELECT 
        st.symbol,
        sy.name,
        date,
        DATE_FORMAT(date, '%Y-%m-%d') AS date_part,
        DATE_FORMAT(date, '%H:%i:%s') AS time_part,
        volume,
        (high - low) AS p_range
    FROM
        unusualactivity.stockdata st
            JOIN
        unusualactivity.symboldata sy ON st.symbol = sy.symbol
    WHERE
        date BETWEEN DATE_SUB(NOW(), INTERVAL 30 DAY) AND NOW()
            AND sy.datatype = 'Common Stock'
            AND sy.symbol NOT IN (SELECT distinct(symbol) FROM stockdata WHERE open = close);    
    
-- Aggregate 30 days view by Time and show only Todays Stats ONLY
DROP VIEW IF EXISTS omv_stats;
CREATE VIEW omv_stats AS
SELECT 
MAX(date_part) AS date, 
time_part AS time,  
symbol,
fn_gauss_cdf(AVG(volume), STD(volume), 
	 (SELECT volume FROM unusualactivity.omv_stockdata WHERE symbol = st.symbol ORDER BY date DESC LIMIT 1)) 
     AS vol_NORMALDIST,
fn_gauss_cdf(AVG(p_range), STD(p_range), 
	 (SELECT p_range FROM unusualactivity.omv_stockdata WHERE symbol = st.symbol ORDER BY date DESC LIMIT 1)) 
     AS p_range_NORMALDIST   
FROM unusualactivity.omv_stockdata st
GROUP BY symbol, time_part;


-- TESTS
select * from omv_stockdata WHERE symbol = 'AAPL';
select COUNT(*) from omv_stockdata;

SELECT * FROM omv_stats WHERE symbol = 'AAPL';
SELECT * FROM omv_stats WHERE symbol = 'AAPL' AND date = '2018-04-24' AND time = '09:42:00';
select COUNT(*) from omv_stats;


select
count(volume)
from omv_stockdata;





SELECT 
MAX(date_part) AS date, 
time_part AS time,  
symbol
-- fn_gauss_cdf(AVG(volume), STD(volume), 
-- 	 (SELECT volume FROM unusualactivity.omv_stockdata WHERE symbol = st.symbol ORDER BY date DESC LIMIT 1)) 
--      AS vol_NORMALDIST,
-- fn_gauss_cdf(AVG(p_range), STD(p_range), 
-- 	 (SELECT p_range FROM unusualactivity.omv_stockdata WHERE symbol = st.symbol ORDER BY date DESC LIMIT 1)) 
--      AS p_range_NORMALDIST   
FROM unusualactivity.omv_stockdata st
GROUP BY symbol, time_part;