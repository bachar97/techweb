package WebTech.TP.TimeSeriesManager.dao;

import WebTech.TP.TimeSeriesManager.dto.UserTimeSeriesAccessDTO;
import jakarta.persistence.*;
import lombok.Builder;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EnableAutoConfiguration
@Builder
@IdClass(UserTimeSeriesAccessKey.class)
public class UserTimeSeriesAccess {
    @Id
    private String username;
    @Id
    private String timeSeriesId;
    private Boolean canEdit;

    public UserTimeSeriesAccessDTO toDTO() {
        return new UserTimeSeriesAccessDTO(username, canEdit);
    }
}
