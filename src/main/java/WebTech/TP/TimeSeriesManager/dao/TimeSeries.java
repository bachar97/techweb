package WebTech.TP.TimeSeriesManager.dao;

import WebTech.TP.TimeSeriesManager.dto.TimeSeriesDTO;
import WebTech.TP.TimeSeriesManager.dto.UserTimeSeriesAccessDTO;
import jakarta.persistence.*;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EnableAutoConfiguration
@Builder
public class TimeSeries {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String description;
    private String creator;
    @OneToMany
    @JoinColumn(name = "timeSeriesId")
    private List<UserTimeSeriesAccess> userAccess;
    @UpdateTimestamp
    private LocalDateTime updateTimestamp;

    public UserAccessType getUserAccessType(String username) {
        if (username.equals(creator) || username.equals("admin")) {
            return UserAccessType.FULL;
        }

        Optional<UserTimeSeriesAccess> userTimeSeriesAccess = userAccess.stream().filter(a -> a.getUsername().equals(username)).findFirst();
        if (userTimeSeriesAccess.isPresent()) {
            if (userTimeSeriesAccess.get().getCanEdit()) {
                return UserAccessType.FULL;
            } else {
                return UserAccessType.READ_ONLY;
            }
        }

        return UserAccessType.NOT_AUTHORIZED;
    }

    public TimeSeriesDTO toDTO() {
        List<UserTimeSeriesAccessDTO> userAccessEntity = new ArrayList<>();
        for (UserTimeSeriesAccess access : userAccess) {
            userAccessEntity.add(access.toDTO());
        }
        return new TimeSeriesDTO(id, name, description, creator, userAccessEntity, updateTimestamp);
    }
}
