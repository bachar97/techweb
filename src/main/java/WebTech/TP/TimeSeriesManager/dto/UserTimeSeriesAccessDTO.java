package WebTech.TP.TimeSeriesManager.dto;

import WebTech.TP.TimeSeriesManager.dao.UserTimeSeriesAccess;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserTimeSeriesAccessDTO {
    private String username;
    private Boolean canEdit;

    public UserTimeSeriesAccess toEntity(String timeSeriesId) {
        return new UserTimeSeriesAccess(username, timeSeriesId, canEdit);
    }
}
