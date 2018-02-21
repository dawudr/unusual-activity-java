package com.financialjuice.unusualactivity.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financialjuice.unusualactivity.model.StockData;
import okhttp3.HttpUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class StockDataRestClient {

    private static final Logger log = LoggerFactory.getLogger(StockDataRestClient.class);
    @Value("${stockdata.rest.apikey}")
    private String apikey;
    private String outputsize = "compact"; // ""&outputsize=full"

    public static enum TimeSeries {

        INTRADAY("TIME_SERIES_INTRADAY"),
        DAILY("TIME_SERIES_DAILY"),
        WEEKLY("TIME_SERIES_WEEKLY"),
        MONTHLY("TIME_SERIES_MONTHLY");

        private final String name;

        private TimeSeries(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            // (otherName == null) check is not needed because name.equals(null) returns false
            return name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }

    public StockDataRestClient() {
    }

    private URI getRequestURL(String symbol, TimeSeries t) {
        return new HttpUrl.Builder()
                .scheme("https")
                .host("www.alphavantage.co")
                .addPathSegments("query")
                .addQueryParameter("function", t.toString())
                .addQueryParameter("symbol", symbol)
                .addQueryParameter("outputsize", outputsize)
                .addQueryParameter("apikey", apikey)
                .build().uri();
    }


    @Retryable(maxAttempts = 5, value = {RuntimeException.class, HttpServerErrorException.class} , backoff = @Backoff(delay = 10000, multiplier = 2))
    public List<StockData> getStockData(String symbol, TimeSeries function) {
        URI url = getRequestURL(symbol, function);
        log.debug("Sending HTTP GET Request to URI [{}]", url.toString());

        List<StockData> data = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            switch (response.getStatusCode().value()) {
                case 200:
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode root = mapper.readTree(response.getBody());

                    if (!root.isNull() && root.has("Meta Data") && root.has("Time Series (Daily)")) {
                        log.debug("Response Body as JSON: " + root.toString());

                        JsonNode timeSeriesNode_l0 = root.get("Time Series (Daily)");
                        if (!timeSeriesNode_l0.isNull()) {
                            Iterator<Map.Entry<String, JsonNode>> elements = timeSeriesNode_l0.fields();
                            log.debug("Fetched: [{}] stockdata elements from request for Symbol:[{}]", timeSeriesNode_l0.size(), symbol);
                            while (elements.hasNext()) {
                                Map.Entry<String, JsonNode> entry = elements.next();
                                String dateStr = entry.getKey();
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                Date date = formatter.parse(dateStr);
                                JsonNode dataNode_l1 = entry.getValue();
                                double open = dataNode_l1.get("1. open").asDouble();
                                double high = dataNode_l1.get("2. high").asDouble();
                                double low = dataNode_l1.get("3. low").asDouble();
                                double close = dataNode_l1.get("4. close").asDouble();
                                long volume = dataNode_l1.get("5. volume").asLong();
                                // clean up data errors
                                if (open > 0 && close > 0 && high > 0 && close > 0 && volume > 0) {
                                    data.add(new StockData(date, symbol, open, close, high, low, volume));
                                }
                            }
                        } else {
                            log.error("Fetched data format mismatch: root is empty or should contain an array of element. Url:[{}] Response body:[{}]", url, response.getBody());
                        }
                    } else {
                        log.error("Fetched failed, response body was empty for HTTP request to Url:[{}]", url);
                        log.error("Response Body as JSON: " + root.toString());
                        return null;
                    }
                    return data;

                case 503:
                    throw new RuntimeException("Server Response: " + response.getStatusCode());
                default:
                    throw new IllegalStateException("Server not ready");
            }
        } catch (Exception e) {
            log.error("Server Error with no response. Url:[{}] Exception:[{}]", url, e.getMessage());
            throw new RuntimeException(e);
        }
//        assertThat(name.asText(), notNullValue());
    }


    public static void main(String[] args) {
        StockDataRestClient s = new StockDataRestClient();
        List<StockData> l = s.getStockData("BAKK.L", TimeSeries.DAILY);
        l.stream().forEach(System.out::println);
        System.out.println(l.stream().count());

    }
}
