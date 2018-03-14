package com.financialjuice.unusualactivity.repository;

import com.financialjuice.unusualactivity.model.SymbolData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@CrossOrigin
@RepositoryRestResource( path = "symbol")
public interface SymbolRepository  extends JpaRepository<SymbolData, String> {

    /**
     * Finds a Symbols of
     * datatype:Equities on
     * exchange:LSE,
     * market:MAIN MARKET
     * country: United Kingdom
     * currency: GBX
     * fcacategory : Premium Equity Commercial Companies
     * as a search criteria.
     */
    @Query("SELECT s FROM SymbolData s WHERE datatype='Equities' AND exchange= 'LSE' AND market='MAIN MARKET' AND country='United Kingdom' AND currency='GBX' AND fcacategory='Premium Equity Commercial Companies'")
    public List<SymbolData> findSymbolsByLSE();

    public List<SymbolData> findSymbolsByExchange(@Param("exchange") String exchange);
}
