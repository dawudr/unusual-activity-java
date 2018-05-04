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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class IntradayBatchRestClient {

    private static final Logger log = LoggerFactory.getLogger(IntradayBatchRestClient.class);
    private static final DateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm");


    public IntradayBatchRestClient() {
    }

    /**
     * https://api.iextrading.com/1.0/stock/market/batch?symbols=aapl,bp&types=quote,chart&range=1d&last=5
     *
     * @param symbolBatch
     * @return
     */
    private URI getRequestURL(String symbolBatch) {
        return new HttpUrl.Builder()
                .scheme("https")
                .host("api.iextrading.com")
                .addPathSegments("1.0")
                .addPathSegments("stock")
                .addPathSegments("market")
                .addPathSegments("batch")
                .addQueryParameter("symbols", symbolBatch)
                .addQueryParameter("types", "quote,chart")
                .addQueryParameter("range", "1d")
//                .addQueryParameter("last", "5") // For News
                .build().uri();
    }

    @Retryable(maxAttempts = 5, value = {RuntimeException.class, HttpServerErrorException.class}, backoff = @Backoff(delay = 5000, multiplier = 2))
    public synchronized List<StockData> getIntradayData(String symbolBatch) {
        long startTime = System.currentTimeMillis();
        URI url = getRequestURL(symbolBatch);
        log.info("Sending HTTP GET Request to URI [{}]", url.toString());

        List<StockData> data = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            switch (response.getStatusCode().value()) {
                case 200:
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode root = mapper.readTree(response.getBody());

                    if (!root.isNull()) {
                        log.debug("Response Body as JSON: " + root.toString());

                        Iterator<Map.Entry<String, JsonNode>> elements = root.fields();
                        log.info("Fetched: [{}] stocks from request", root.size());

                        while (elements.hasNext()) {
                            Map.Entry<String, JsonNode> entry = elements.next();
                            String symbol = entry.getKey();
//                            log.debug("Parsing JSON Response for SymbolData:[{}]", symbol);

                            // Parse child of JSON response
                            JsonNode dataNode_l1 = entry.getValue();
                            JsonNode dataNode_l2_quote = dataNode_l1.get("quote");
                            double open = dataNode_l2_quote.get("open").asDouble();
                            double close = dataNode_l2_quote.get("close").asDouble();

                            JsonNode dataNode_l2_chart = dataNode_l1.get("chart");
                            if(dataNode_l2_chart.isArray()) {
                                Iterator<JsonNode> jsonNodeIterator = dataNode_l2_chart.elements();
                                while (jsonNodeIterator.hasNext()) {
                                    JsonNode itemNode = jsonNodeIterator.next();

                                    log.debug("Chart Node for Symbol[{}] -> {}", symbol, itemNode.toString());

                                    // Clean bad data
                                    if(itemNode.get("date") != null &&
                                            itemNode.get("minute") != null &&
                                            itemNode.get("marketVolume") != null &&
                                            itemNode.get("marketHigh") != null && itemNode.get("marketHigh").asDouble() > 0 &&
                                            itemNode.get("marketLow") != null && itemNode.get("marketLow").asDouble() > 0) {

                                        if(itemNode.get("marketVolume").asLong() == 0L ) {
                                            log.error("Symbol {} Volume was zero", symbol);
                                        }
                                        //TODO: Do check and use Realtime when market open
                                        String dateStr = itemNode.get("date").asText();
                                        String timeStr = itemNode.get("minute").asText();
//                                        Date date = df.parse(dateStr + " " + timeStr);

                                        String dateTimeStrRaw = dateStr + " " + timeStr;
                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm");
                                        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStrRaw, formatter);
                                        Date date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());

                                        double high = itemNode.get("marketHigh").asDouble();
                                        double low = itemNode.get("marketLow").asDouble();
                                        long volume = itemNode.get("marketVolume").asLong();
                                        data.add(new StockData(date, symbol, open, close, high, low, volume));
                                    }
                                }
                            }
                        }
                    } else {
                        log.error("Fetched failed, response body was empty for HTTP request to Url:[{}]", url);
                        log.error("Response Body as JSON: " + root.toString());
                        return null;
                    }
                    log.info("Elapsed time: {}ms", (System.currentTimeMillis() - startTime));
                    return data;

                case 503:
                    throw new RuntimeException("Server Response: " + response.getStatusCode());
                default:
                    throw new IllegalStateException("Server not ready");
            }
        } catch (HttpClientErrorException e) {
            log.error("Fetched failed, response body was empty for HTTP request to Url:[{}] Exception:[{}]", url, e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Server Error with no response. Url:[{}] Exception:[{}]", url, e.getStackTrace());
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
        IntradayBatchRestClient s = new IntradayBatchRestClient();
        List<StockData> l = s.getIntradayData("AAPL,BP,RDS.A,RDS.B,CNA,GOOG,FB,MSFT");
        l.stream().forEach(System.out::println);
        System.out.println(l.stream().count());

    }
}
