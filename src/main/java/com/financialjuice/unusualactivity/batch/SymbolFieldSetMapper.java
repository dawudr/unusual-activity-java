package com.financialjuice.unusualactivity.batch;

import com.financialjuice.unusualactivity.model.SymbolData;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class SymbolFieldSetMapper implements FieldSetMapper<SymbolData> {
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
    public SymbolData mapFieldSet(FieldSet fieldSet) throws BindException {
        if (fieldSet == null) {
            throw new IllegalStateException("Exception in Field Set Mapper. Field Set Mapper must not be null...");
        }

        final SymbolData symbolData = new SymbolData();
        symbolData.setSymbol(fieldSet.readString(SYMBOL));
        symbolData.setName(fieldSet.readString(NAME));
        symbolData.setInstrumentname(fieldSet.readString(INSTRUMENTNAME));
        symbolData.setIndustry(fieldSet.readString(INDUSTRY));
        symbolData.setSector(fieldSet.readString(SECTOR));
        symbolData.setStartdate(fieldSet.readString(STARTDATE));
        symbolData.setCountry(fieldSet.readString(COUNTRY));
        symbolData.setCurrency(fieldSet.readString(CURRENCY));
        symbolData.setMarketcap(fieldSet.readString(MARKETCAP));
        symbolData.setMarket(fieldSet.readString(MARKET));
        symbolData.setFcacategory(fieldSet.readString(FCACATEGORY));
        symbolData.setExchange("LSE");
        symbolData.setDatatype("Equities");
        return symbolData;
    }
}
