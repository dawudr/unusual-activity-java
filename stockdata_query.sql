SELECT * FROM unusualactivity.stockdata_bk WHERE symbol = 'AAPL';

SHOW CREATE TABLE unusualactivity.stockdata;


SELECT DATE_FORMAT(date, '%Y-%m-%d') AS DATEONLY, DATE_FORMAT(date,'%H:%i:%s') AS TIMEONLY 
FROM unusualactivity.stockdata_bk WHERE symbol = 'AAPL';


-- sample size
SELECT count(volume), DATE_FORMAT(date,'%H:%i:%s') AS TIMEONLY 
FROM unusualactivity.stockdata_bk WHERE symbol = 'AAPL'
GROUP BY TIMEONLY;

-- mean
SELECT AVG(volume), DATE_FORMAT(date,'%H:%i:%s') AS TIMEONLY 
FROM unusualactivity.stockdata_bk WHERE symbol = 'AAPL'
GROUP BY TIMEONLY;

-- stdev
SELECT STD(volume), DATE_FORMAT(date,'%H:%i:%s') AS TIMEONLY 
FROM unusualactivity.stockdata_bk WHERE symbol = 'AAPL'
GROUP BY TIMEONLY;

-- min
SELECT MIN(volume), DATE_FORMAT(date,'%H:%i:%s') AS TIMEONLY 
FROM unusualactivity.stockdata_bk WHERE symbol = 'AAPL'
GROUP BY TIMEONLY;

-- max
SELECT MAX(volume), DATE_FORMAT(date,'%H:%i:%s') AS TIMEONLY 
FROM unusualactivity.stockdata_bk WHERE symbol = 'AAPL'
GROUP BY TIMEONLY;

USE unusualactivity;

-- Aggregate
-- Loop over Time
-- Replace volume value at time
SELECT 
MAX(DATE_FORMAT(date, '%Y-%m-%d')) AS DATEONLY, 
DATE_FORMAT(date,'%H:%i:%s') AS TIMEONLY,  
symbol,
(SELECT volume FROM unusualactivity.stockdata WHERE symbol = 'AAPL' ORDER BY date DESC LIMIT 1) AS latest_volume,
AVG(volume) AS MEAN,
STD(volume) AS STDEV, 
MIN(volume) AS MIN,
MAX(volume) AS MAX,
fn_gauss_cdf(AVG(volume), STD(volume), 
	 (SELECT volume FROM unusualactivity.stockdata WHERE symbol = 'AAPL' ORDER BY date DESC LIMIT 1)) 
     AS NORMALDIST,
COUNT(volume) 
FROM unusualactivity.stockdata WHERE symbol = 'AAPL'
GROUP BY TIMEONLY;



INSERT INTO statsgrid (date, time, symbol, volume, mean, stdev, min, max, normaldist, samplesize)
VALUES ( 
DATE_FORMAT(date, '%Y-%m-%d'), 
DATE_FORMAT(date,'%H:%i:%s'), 
symbol, 
volume,
(SELECT AVG(volume), DATE_FORMAT(date,'%H:%i:%s') AS TIMEONLY 
FROM unusualactivity.stockdata_bk WHERE symbol = 'AAPL'
GROUP BY TIMEONLY),
(SELECT STD(volume), DATE_FORMAT(date,'%H:%i:%s') AS TIMEONLY 
FROM unusualactivity.stockdata_bk WHERE symbol = 'AAPL'
GROUP BY TIMEONLY),
(SELECT MIN(volume), DATE_FORMAT(date,'%H:%i:%s') AS TIMEONLY 
FROM unusualactivity.stockdata_bk WHERE symbol = 'AAPL'
GROUP BY TIMEONLY),
(SELECT MAX(volume), DATE_FORMAT(date,'%H:%i:%s') AS TIMEONLY 
FROM unusualactivity.stockdata_bk WHERE symbol = 'AAPL'
GROUP BY TIMEONLY),
NULL,
(SELECT count(volume), DATE_FORMAT(date,'%H:%i:%s') AS TIMEONLY 
FROM unusualactivity.stockdata_bk WHERE symbol = 'AAPL'
GROUP BY TIMEONLY)),
(SELECT MIN(volume), DATE_FORMAT(date,'%H:%i:%s') AS TIMEONLY 
FROM unusualactivity.stockdata WHERE symbol = 'AAPL'
GROUP BY TIMEONLY))