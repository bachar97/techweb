package WebTech.TP.TimeSeriesManager.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EnableAutoConfiguration
@Builder
@IdClass(TagKey.class)
public class Tag {
    @Id
    private String tagName;
    @Id
    private String eventId;
}
