package com.financialjuice.unusualactivity.repository;

import com.financialjuice.unusualactivity.model.StockData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Date;
import java.util.List;

/**
 * That is all you have to do in the repository layer. You will now be able to use JpaRepository’s methods like save(), findOne(), findAll(), count(), delete() etc.
 * You don’t need to implement these methods. They are already implemented by Spring Data JPA’s SimpleJpaRepository. This implementation is plugged in by Spring automatically at runtime.
 *
 * JpaRepository – which extends PagingAndSortingRepository and, in turn, the CrudRepository.
 *
 * Each of these defines its own functionality:
 *
 * CrudRepository provides CRUD functions
 * PagingAndSortingRepository provides methods to do pagination and sort records
 * JpaRepository provides JPA related methods such as flushing the persistence context and delete records in a batch
 *
 * @Modifying annotation above the repository method because it modifies the state of the database and does not select data.
 *
 */

@CrossOrigin
@RepositoryRestResource (path = "stock")
public interface StockDataRepository extends JpaRepository<StockData, Long> {

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE StockData s " +
            "SET " +
            "s.open = :open , " +
            "s.close = :close , " +
            "s.high = :high , " +
            "s.low = :low , " +
            "s.volume = :volume " +
            "WHERE s.symbol = :symbol " +
            "AND s.date = :date")
    public int updateStock(@Param("symbol") String symbol, @Param("date") Date date,
                    @Param("open") double open, @Param("close") double close,
                    @Param("high") double high, @Param("low") double low,
                    @Param("volume") long volume);

    @Query("SELECT MAX(s.date) " +
            "FROM StockData s " +
            "WHERE s.symbol = :symbol")
    public Date getLastUpdated(@Param("symbol") String symbol);

    public List<StockData> findAllBySymbol(String s);

}