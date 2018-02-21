package com.financialjuice.unusualactivity.controller;

import com.financialjuice.unusualactivity.model.StockData;
import com.financialjuice.unusualactivity.repository.StockDataRepository;
import com.financialjuice.unusualactivity.tasks.StockDataFeederTaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @RestController annotation is a combination of Spring’s @Controller and @ResponseBody annotations.
 * The @Controller annotation is used to define a controller and the @ResponseBody annotation is used to indicate that the return value of a method should be used as the response body of the request.
 *
 * @RequestBody annotation is used to bind the request body with a method parameter.
 *
 * @Valid annotation makes sure that the request body is valid. If the request body doesn’t have a symbol and date, then spring will return a 400 BadRequest error to the client.
 *
 * @PathVariable annotation, as the name suggests, is used to bind a path variable with a method parameter.
 *
 * ResponseEntity class gives us more flexibility while returning a response from the api. For example, in the above api, If a note doesn’t exist with the given id, then we’re returning a 404 Not Found error with the help of ResponseEntity.
 */

// @CrossOrigin(origins = "http://localhost:4200")
@CrossOrigin("*")
@RestController    // This means that this class is a Controller
@RequestMapping(path="api/stock") // This means URL's start with /stock (after Application path)
public class StockDataController {

    private static final Logger log = LoggerFactory.getLogger(StockDataController.class);
    private List<StockData> stockDatas = new ArrayList();

    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private StockDataRepository stockDataRepository;

    @Autowired
    private StockDataFeederTaskExecutor stockDataFeederTaskExecutor;

    // Get All StockData
    @RequestMapping(value="", method = RequestMethod.GET)
    public List<StockData> getAllStockData() {
        // This returns a JSON or XML
        return stockDataRepository.findAll();
    }

    StockDataController() {
        //this.stockDatas = buildStockDatas();
    }

    // Get a Single StockData
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<StockData> getStockData(@PathVariable("id") Long id) {
        StockData stockData = stockDataRepository.findOne(id);
        if(stockData == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(stockData);
        //return this.stockDatas.stream().filter(user -> user.getId() == id).findFirst().orElse(null);
    }

    // Create a new StockData
    @RequestMapping(value = "", method = RequestMethod.POST)
    public StockData createStockData(@Valid @RequestBody StockData stockData) {
        return stockDataRepository.save(stockData);
    }

    // Update a StockData
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<StockData> updateStockData(@PathVariable(value = "id") Long id,
                                     @Valid @RequestBody StockData stockDataUpdate) {
        StockData stockData = stockDataRepository.findOne(id);
        if(stockData == null) {
            return ResponseEntity.notFound().build();
        }

        stockData.setSymbol(stockDataUpdate.getSymbol());
        stockData.setDate(stockDataUpdate.getDate());
        stockData.setOpen(stockDataUpdate.getOpen());
        stockData.setClose(stockDataUpdate.getClose());
        stockData.setHigh(stockDataUpdate.getHigh());
        stockData.setLow(stockDataUpdate.getLow());
        stockData.setVolume(stockDataUpdate.getVolume());

        StockData updatedNote = stockDataRepository.save(stockData);
        return ResponseEntity.ok(updatedNote);
    }

    // Delete a StockData
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<StockData> deleteStockData(@PathVariable(value = "id") Long id) {
        StockData stockData = stockDataRepository.findOne(id);
        if(stockData == null) {
            return ResponseEntity.notFound().build();
        }

        stockDataRepository.delete(stockData);
        return ResponseEntity.ok().build();
    }

    @RequestMapping("/import/start")
    public String executeAsync() {
        log.debug("Started StockFeeder from HTTP request");
        stockDataFeederTaskExecutor.executeAsynchronously();

        return "OK";
    }

}
