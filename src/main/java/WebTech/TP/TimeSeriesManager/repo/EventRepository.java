package WebTech.TP.TimeSeriesManager.repo;

import WebTech.TP.TimeSeriesManager.dao.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, String> {

    List<Event> findByTimeSeriesId(String timeSeriesId);

    boolean existsByTimeSeriesIdAndId(String timeSeriesId, String id);

    Optional<Event> findByTimeSeriesIdAndId(String timeSeriesId, String id);
}