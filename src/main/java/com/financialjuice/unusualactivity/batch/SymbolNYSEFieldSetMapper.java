package com.financialjuice.unusualactivity.batch;

import com.financialjuice.unusualactivity.model.SymbolData;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class SymbolNYSEFieldSetMapper implements FieldSetMapper<SymbolData> {
    private static final int SYMBOL = 0;
    private static final int NAME = 1;
    private static final int INDUSTRY = 7;
    private static final int SECTOR = 6;
    private static final int STARTDATE = 5;
    private static final int MARKETCAP = 3;


    @Override
    public SymbolData mapFieldSet(FieldSet fieldSet) throws BindException {
        if (fieldSet == null) {
            throw new IllegalStateException("Exception in Field Set Mapper. Field Set Mapper must not be null...");
        }

        final SymbolData symbolData = new SymbolData();
        symbolData.setSymbol(fieldSet.readString(SYMBOL));
        symbolData.setName(fieldSet.readString(NAME));
        symbolData.setInstrumentname("");
        symbolData.setIndustry(fieldSet.readString(INDUSTRY));
        symbolData.setSector(fieldSet.readString(SECTOR));
        symbolData.setStartdate(fieldSet.readString(STARTDATE));
        symbolData.setCountry("US");
        symbolData.setCurrency("USD");
        symbolData.setMarketcap(fieldSet.readString(MARKETCAP));
        symbolData.setMarket("");
        symbolData.setFcacategory("");
        symbolData.setExchange("NYSE");
        symbolData.setDatatype("Equities");
        return symbolData;
    }
}
