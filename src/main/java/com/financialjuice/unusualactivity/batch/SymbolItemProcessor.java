package com.financialjuice.unusualactivity.batch;

import com.financialjuice.unusualactivity.model.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class SymbolItemProcessor implements ItemProcessor<Symbol, Symbol> {

    private static final Logger log = LoggerFactory.getLogger(SymbolItemProcessor.class);

    @Override
    public Symbol process(final Symbol symbol){
        log.debug("Process transform on Symbol input {}", symbol.toString());

        final String transformedSymbolName;

        // TODO: Parametise
        String exchange = "LSE";
        String datatype = "Equities";
        switch(exchange) {
            case "LSE":
                String inSymbol = symbol.getSymbol();
                if(inSymbol.endsWith(".")) {
                    transformedSymbolName = symbol.getSymbol() + "L";
                } else if (inSymbol.contains(".")) {
                    transformedSymbolName = inSymbol.replace(".", "-") + ".L";
                } else {
                    transformedSymbolName = symbol.getSymbol() + ".L";
                }
                break;
            case "NYSE":
                transformedSymbolName = symbol.getSymbol();
                break;
            default:
                transformedSymbolName = symbol.getSymbol();
        }

        Symbol transformedSymbol = symbol;
        log.info("Converting symbol (" + symbol.getSymbol() + ") into (" + transformedSymbolName + ")");
        transformedSymbol.setSymbol(transformedSymbolName);
        transformedSymbol.setExchange(exchange);
        transformedSymbol.setDatatype(datatype);
        return transformedSymbol;

    }
}
