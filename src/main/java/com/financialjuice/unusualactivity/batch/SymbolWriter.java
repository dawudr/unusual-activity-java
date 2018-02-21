package com.financialjuice.unusualactivity.batch;

import com.financialjuice.unusualactivity.model.Symbol;
import com.financialjuice.unusualactivity.repository.SymbolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SymbolWriter implements ItemWriter<Symbol> {

    private static final Logger log = LoggerFactory.getLogger(SymbolWriter.class);

    @Autowired
    private SymbolRepository symbolRepository;

    @Override
    public void write(List<? extends Symbol> list) throws Exception {
        list.forEach( item -> {
            log.debug("Writing symbol {}", item);
            symbolRepository.save(item);
        });
    }
}
