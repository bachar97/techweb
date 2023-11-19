package WebTech.TP.TimeSeriesManager.dto;
import WebTech.TP.TimeSeriesManager.dao.TimeSeries;
import WebTech.TP.TimeSeriesManager.dao.UserTimeSeriesAccess;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class TimeSeriesDTO {
    private String id;
    private String name;
    private String description;
    private String creator;
    private List<UserTimeSeriesAccessDTO> userAccess;
    private LocalDateTime updateTimestamp;

    public TimeSeries toEntity() {
        List<UserTimeSeriesAccess> userAccessEntity = new ArrayList<>();
        for (UserTimeSeriesAccessDTO access : userAccess) {
            userAccessEntity.add(access.toEntity(id));
        }
        return new TimeSeries(id, name, description, creator, userAccessEntity, updateTimestamp);
    }
}
