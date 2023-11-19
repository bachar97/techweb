package WebTech.TP.TimeSeriesManager.repo;

import WebTech.TP.TimeSeriesManager.dao.Tag;
import WebTech.TP.TimeSeriesManager.dao.TagKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, TagKey> {
    @Modifying
    @Query(value = "delete from tag where event_id= :eventId", nativeQuery = true)
    void deleteByEventId(@Param("eventId") String eventId);
}
