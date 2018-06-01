package com.financialjuice.unusualactivity.batch;

import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

/**
 Symbol,Name,Last,Change,%Chg,High,Low,Volume,Time
 */
public class SymbolUSVolumeLeadersDelimitedLineTokenizer extends DelimitedLineTokenizer {

    public SymbolUSVolumeLeadersDelimitedLineTokenizer() {
        setDelimiter(DELIMITER_COMMA);
        setNames(new String[]{
                "Symbol",
                "Name",
                "",
                "",
                "",
                "",
                "",
                "",
                ""
        });
    }
}

