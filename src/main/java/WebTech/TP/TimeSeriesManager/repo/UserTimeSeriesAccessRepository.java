package WebTech.TP.TimeSeriesManager.repo;

import WebTech.TP.TimeSeriesManager.dao.UserTimeSeriesAccess;
import WebTech.TP.TimeSeriesManager.dao.UserTimeSeriesAccessKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserTimeSeriesAccessRepository extends JpaRepository<UserTimeSeriesAccess, UserTimeSeriesAccessKey> {
    @Modifying
    @Query(value = "delete from user_time_series_access where time_series_id= :timeSeriesId", nativeQuery = true)
    void deleteByTimeSeriesId(@Param("timeSeriesId") String timeSeriesId);
}
