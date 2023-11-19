package WebTech.TP.TimeSeriesManager.service;

import WebTech.TP.TimeSeriesManager.dao.Event;

import java.util.List;
import java.util.Optional;

public interface EventService {
    List<Event> findAll();

    Optional<Event> findById(String id);

    List<Event> findByTimeSeriesId(String timeSeriesId);

    boolean existsById(String id);

    boolean existsByTimeSeriesIdAndId(String timeSeriesId, String id);

    void save(Event event);

    void deleteById(String id);
}
