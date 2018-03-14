package com.financialjuice.unusualactivity.batch;

import com.financialjuice.unusualactivity.model.SymbolData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class SymbolItemProcessor implements ItemProcessor<SymbolData, SymbolData> {

    private static final Logger log = LoggerFactory.getLogger(SymbolItemProcessor.class);

    @Override
    public SymbolData process(final SymbolData symbolData){
        log.debug("Process transform on SymbolData input {}", symbolData.toString());

        final String transformedSymbolName;

        // TODO: Parametise
        String exchange = "";
        String inSymbol = symbolData.getSymbol();
        switch(exchange) {
            case "LSE":
                if(inSymbol.endsWith(".")) {
                    transformedSymbolName = symbolData.getSymbol() + "L";
                } else if (inSymbol.contains(".")) {
                    transformedSymbolName = inSymbol.replace(".", "-") + ".L";
                } else {
                    transformedSymbolName = symbolData.getSymbol() + ".L";
                }
                exchange = "LSE";
                break;
            case "NYSE":
                if(inSymbol.endsWith(".")) {
                    transformedSymbolName = symbolData.getSymbol().replace(".", "");
                } else {
                    transformedSymbolName = symbolData.getSymbol();
                }
                exchange = "NYSE";
                break;
            default:
                transformedSymbolName = symbolData.getSymbol();
        }

        SymbolData transformedSymbolData = symbolData;
        log.info("Converting symbolData (" + symbolData.getSymbol() + ") into (" + transformedSymbolName + ")");
        transformedSymbolData.setSymbol(transformedSymbolName);
        return transformedSymbolData;

    }
}
