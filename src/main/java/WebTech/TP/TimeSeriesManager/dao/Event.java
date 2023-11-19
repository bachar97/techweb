package WebTech.TP.TimeSeriesManager.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import WebTech.TP.TimeSeriesManager.dto.EventDTO;
import jakarta.persistence.*;
import lombok.Builder;
import org.hibernate.annotations.UpdateTimestamp;
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
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String timeSeriesId;
    private LocalDateTime date;
    private String comment;
    @OneToMany
    @JoinColumn(name = "eventId")
    private List<Tag> tags;
    @UpdateTimestamp
    private LocalDateTime updateTimestamp;

    public EventDTO toDTO() {
        List<String> tagsList = new ArrayList<>();
        for (Tag tag : tags) {
            tagsList.add(tag.getTagName());
        }
        return new EventDTO(id, timeSeriesId, date, comment, tagsList, updateTimestamp);
    }
}
