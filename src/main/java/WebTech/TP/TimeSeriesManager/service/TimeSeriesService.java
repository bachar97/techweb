package WebTech.TP.TimeSeriesManager.service;

import WebTech.TP.TimeSeriesManager.dao.TimeSeries;

import java.util.List;
import java.util.Optional;

public interface TimeSeriesService {
    List<TimeSeries> findAll();

    Optional<TimeSeries> findById(String id);

    boolean existsById(String id);

    void save(TimeSeries timeseries);

    void deleteById(String id);
}
