package com.financialjuice.unusualactivity.batch;

import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

public class SymbolDelimitedLineTokenizer extends DelimitedLineTokenizer {

    public SymbolDelimitedLineTokenizer() {
        setDelimiter(DELIMITER_COMMA);
        setNames(new String[]{
                "symbol",
                "name",
                "instrumentname",
                "",
                "",
                "industry",
                "sector",
                "startdate",
                "country",
                "currency",
                "marketcap",
                "market",
                "fcacategory",
                "",
                ""
        });
    }
}
