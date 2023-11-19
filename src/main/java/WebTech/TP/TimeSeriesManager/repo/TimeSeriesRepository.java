package WebTech.TP.TimeSeriesManager.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import WebTech.TP.TimeSeriesManager.dao.TimeSeries;

public interface TimeSeriesRepository extends JpaRepository<TimeSeries, String> {
    
}