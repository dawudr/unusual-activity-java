package com.financialjuice.unusualactivity.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financialjuice.unusualactivity.model.SymbolData;
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

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
public class SymbolRestClient {

    private static final Logger log = LoggerFactory.getLogger(SymbolRestClient.class);

    public SymbolRestClient() {
    }

    /**
     * https://api.iextrading.com/1.0/ref-data/symbols
     *
     * @return
     */
    private URI getRequestURL() {

        return new HttpUrl.Builder()
                .scheme("https")
                .host("api.iextrading.com")
                .addPathSegments("1.0")
                .addPathSegments("ref-data")
                .addPathSegments("symbols")
                .build().uri();
    }




    public enum StockType {
        AD ("ADR"),
        RE ("REIT"),
        CE ("Closed end fund"),
        SI ("Secondary Issue"),
        LP ("Limited Partnerships"),
        CS ("Common Stock"),
        ET ("ETF");

        private String stockType;

        StockType(String stockType) {
            this.stockType = stockType;
        }

        public String getStockType() {
            return stockType;
        }
    }

    @Retryable(maxAttempts = 5, value = {RuntimeException.class, HttpServerErrorException.class}, backoff = @Backoff(delay = 2000, multiplier = 2))
    public List<SymbolData> getSymbols() {
        URI url = getRequestURL();
        log.debug("Sending HTTP GET Request to URI [{}]", url.toString());

        List<SymbolData> data = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            switch (response.getStatusCode().value()) {
                case 200:
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode root = mapper.readTree(response.getBody());

                    if (!root.isNull() && root.isArray()) {
                        log.debug("Response Body as JSON: " + root.toString());

                        for(JsonNode symbolNode : root) {
                            String symbol = symbolNode.get("symbol").asText();
                            String name = symbolNode.get("name").asText();
                            String dateStr = symbolNode.get("date").asText();
                            String type = symbolNode.get("type").asText();
                            try {
                                data.add(new SymbolData(symbol, name, dateStr, StockType.valueOf(type.toUpperCase()).getStockType()));
                            } catch (IllegalArgumentException ex) {
                                log.error("Bad data error for Symbol {} , Exception: {} ", symbol , ex.getMessage());
                            }
                        }

                    } else {
                        log.error("Fetched failed, response body was empty for HTTP request to Url:[{}]", url);
                        log.error("Response Body as JSON: " + root.toString());
                        return null;
                    }
                    log.debug("Found {} Symbols from feed.", data.size());
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
        } catch (IOException e) {
            log.error("Server Error with no response. Url:[{}] Exception:[{}]", url, e.getMessage());
            throw new RuntimeException(e);
        }
//        assertThat(name.asText(), notNullValue());
    }


    public static void main(String[] args) {
        SymbolRestClient s = new SymbolRestClient();
        List<SymbolData> l = s.getSymbols();
        l.stream().forEach(System.out::println);
        System.out.println(l.stream().count());


        System.out.println((StockType.valueOf("cs".toUpperCase()).getStockType()));

    }
}
