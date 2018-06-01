package com.financialjuice.unusualactivity.batch;

import com.financialjuice.unusualactivity.model.SymbolData;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.util.Date;

public class SymbolUSVolumeLeadersFieldSetMapper implements FieldSetMapper<SymbolData> {
    private static final int SYMBOL = 0;
    private static final int NAME = 1;


    @Override
    public SymbolData mapFieldSet(FieldSet fieldSet) throws BindException {
        if (fieldSet == null) {
            throw new IllegalStateException("Exception in Field Set Mapper. Field Set Mapper must not be null...");
        }

        final SymbolData symbolData = new SymbolData();
        symbolData.setSymbol(fieldSet.readString(SYMBOL));
        symbolData.setName(fieldSet.readString(NAME));
        symbolData.setInstrumentname("");
        symbolData.setStartdate(new Date().toString());
        symbolData.setCountry("US");
        symbolData.setCurrency("USD");
        symbolData.setMarket("");
        symbolData.setFcacategory("");
        symbolData.setExchange("US");
        symbolData.setDatatype("Common Stock");
        return symbolData;
    }
}
