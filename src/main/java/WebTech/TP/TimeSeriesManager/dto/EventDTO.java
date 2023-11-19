package WebTech.TP.TimeSeriesManager.dto;

import WebTech.TP.TimeSeriesManager.dao.Event;
import WebTech.TP.TimeSeriesManager.dao.Tag;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class EventDTO {
    private String id;
    private String timeSeriesId;
    private LocalDateTime date;
    private String comment;
    private List<String> tags;
    private LocalDateTime updateTimestamp;

    public Event toEntity() {
        List<Tag> tagsList = new ArrayList<>();
        for (String tag : tags) {
            tagsList.add(new Tag(tag, id));
        }
        return new Event(id, timeSeriesId, date, comment, tagsList, updateTimestamp);
    }
}
