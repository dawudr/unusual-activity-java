package com.financialjuice.unusualactivity.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financialjuice.unusualactivity.model.StockData;
import okhttp3.HttpUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class IntradayRestClient {

    private static final Logger log = LoggerFactory.getLogger(IntradayRestClient.class);

    public IntradayRestClient() {
    }

    /**
     * https://api.iextrading.com/1.0/stock/bp/quote
     *
     * @param symbol
     * @return
     */
    private URI getRequestURL(String symbol) {

        return new HttpUrl.Builder()
                .scheme("https")
                .host("api.iextrading.com")
                .addPathSegments("1.0")
                .addPathSegments("stock")
                .addPathSegments(symbol)
                .addPathSegments("quote")
                .build().uri();
    }

    @Retryable(maxAttempts = 5, value = {RuntimeException.class, HttpServerErrorException.class}, backoff = @Backoff(delay = 2000, multiplier = 2))
    public List<StockData> getIntradayData(String symbol) {
        URI url = getRequestURL(symbol);
        log.debug("Sending HTTP GET Request to URI [{}]", url.toString());

        List<StockData> data = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            switch (response.getStatusCode().value()) {
                case 200:
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode root = mapper.readTree(response.getBody());

                    if (!root.isNull() && root.has("symbol")) {
                        log.debug("Response Body as JSON: " + root.toString());

                        log.debug("Fetched stockdata for SymbolData:[{}]", symbol);
                        Date date = new Date();
                        date.setTime(root.get("latestUpdate").asLong());
                        double open = root.get("open").asDouble();
                        double high = root.get("high").asDouble();
                        double low = root.get("low").asDouble();
                        double close = root.get("close").asDouble();
                        long volume = root.get("latestVolume").asLong();
                        // clean up data errors
                        if (open > 0 && close > 0 && high > 0 && close > 0 && volume > 0) {
                            data.add(new StockData(date, symbol, open, close, high, low, volume));
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
        } catch (HttpClientErrorException e) {
            log.error("Fetched failed, response body was empty for HTTP request to Url:[{}]", url);
            log.error("Server Error with no response. Url:[{}] Exception:[{}]", url, e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Server Error with no response. Url:[{}] Exception:[{}]", url, e.getMessage());
            throw new RuntimeException(e);
        }
//        assertThat(name.asText(), notNullValue());
    }


    public static void main(String[] args) {
        IntradayRestClient s = new IntradayRestClient();
        List<StockData> l = s.getIntradayData("BP");
        l.stream().forEach(System.out::println);
        System.out.println(l.stream().count());

    }
}
