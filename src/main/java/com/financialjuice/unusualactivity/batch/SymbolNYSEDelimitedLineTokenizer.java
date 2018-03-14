package com.financialjuice.unusualactivity.batch;

import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

/**
 * "Symbol","Name","LastSale","MarketCap","ADR TSO","IPOyear","Sector","Industry","Summary Quote",
 */
public class SymbolNYSEDelimitedLineTokenizer extends DelimitedLineTokenizer {

    public SymbolNYSEDelimitedLineTokenizer() {
        setDelimiter(DELIMITER_COMMA);
        setNames(new String[]{
                "symbol",
                "name",
                "",
                "marketcap",
                "",
                "startdate",
                "sector",
                "industry",
                "",
                ""
        });
    }
}

