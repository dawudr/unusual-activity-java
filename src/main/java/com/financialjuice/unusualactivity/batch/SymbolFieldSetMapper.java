package com.financialjuice.unusualactivity.batch;

import com.financialjuice.unusualactivity.model.Symbol;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class SymbolFieldSetMapper implements FieldSetMapper<Symbol> {
    private static final int SYMBOL = 0;
    private static final int NAME = 1;
    private static final int INSTRUMENTNAME = 2;
    private static final int INDUSTRY = 5;
    private static final int SECTOR = 6;
    private static final int STARTDATE = 7;
    private static final int COUNTRY = 8;
    private static final int CURRENCY = 9;
    private static final int MARKETCAP = 10;
    private static final int MARKET = 11;
    private static final int FCACATEGORY = 12;


    @Override
    public Symbol mapFieldSet(FieldSet fieldSet) throws BindException {
        if (fieldSet == null) {
            throw new IllegalStateException("Exception in Field Set Mapper. Field Set Mapper must not be null...");
        }

        final Symbol symbol = new Symbol();
        symbol.setSymbol(fieldSet.readString(SYMBOL));
        symbol.setName(fieldSet.readString(NAME));
        symbol.setInstrumentname(fieldSet.readString(INSTRUMENTNAME));
        symbol.setIndustry(fieldSet.readString(INDUSTRY));
        symbol.setSector(fieldSet.readString(SECTOR));
        symbol.setStartdate(fieldSet.readString(STARTDATE));
        symbol.setCountry(fieldSet.readString(COUNTRY));
        symbol.setCurrency(fieldSet.readString(CURRENCY));
        symbol.setMarketcap(fieldSet.readString(MARKETCAP));
        symbol.setMarket(fieldSet.readString(MARKET));
        symbol.setFcacategory(fieldSet.readString(FCACATEGORY));
        return symbol;
    }
}
