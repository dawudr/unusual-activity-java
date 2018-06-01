package com.financialjuice.unusualactivity.repository;

import com.financialjuice.unusualactivity.model.StatsCompositeKey;
import com.financialjuice.unusualactivity.model.StatsGrid;
import org.springframework.data.hazelcast.repository.HazelcastRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.sql.Time;
import java.util.Date;
import java.util.List;

@Repository
@CrossOrigin
@RepositoryRestResource( path = "statsgrid")
public interface StatsGridCacheRepository extends HazelcastRepository<StatsGrid, StatsCompositeKey> {

    @Query("SELECT s.time_part " +
            "FROM StatsGrid s " +
            "WHERE s.symbol = :symbol AND s.date = " +
            "(SELECT MAX(t.date) FROM StatsGrid t)")
    public Time getLastUpdatedTime(@Param("symbol") String symbol);

    @Query("SELECT s.date " +
            "FROM StatsGrid s " +
            "WHERE s.symbol = :symbol AND s.date = " +
            "(SELECT MAX(t.date) FROM StatsGrid t)")
    public Time getLastUpdatedDate(@Param("symbol") String symbol);

    @Query("SELECT MAX(date) FROM StatsGrid")
    public Date getTopLastUpdatedDate();

    public List<StatsGrid> findAllByDateAfter(Date date);

    public List<StatsGrid> findAllBySymbolAndDateAfter(String symbol, Date date);
}
