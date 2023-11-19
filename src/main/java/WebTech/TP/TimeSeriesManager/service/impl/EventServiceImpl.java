package WebTech.TP.TimeSeriesManager.service.impl;

import WebTech.TP.TimeSeriesManager.dao.Event;
import WebTech.TP.TimeSeriesManager.dao.Tag;
import WebTech.TP.TimeSeriesManager.repo.EventRepository;
import WebTech.TP.TimeSeriesManager.repo.TagRepository;
import WebTech.TP.TimeSeriesManager.service.EventService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {
    private final EventRepository repository;
    private final TagRepository tagRepository;

    @Autowired
    public EventServiceImpl(EventRepository repository, TagRepository tagRepository) {
        this.repository = repository;
        this.tagRepository = tagRepository;
    }

    @Override
    public List<Event> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Event> findByTimeSeriesId(String timeSeriesId) {
        return repository.findByTimeSeriesId(timeSeriesId);
    }

    @Override
    public Optional<Event> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public boolean existsById(String id) {
        return repository.existsById(id);
    }

    @Override
    public boolean existsByTimeSeriesIdAndId(String timeSeriesId, String id) {
        return repository.existsByTimeSeriesIdAndId(timeSeriesId, id);
    }

    @Override
    @Transactional
    public void save(Event event) {
        List<Tag> tagsList = event.getTags();
        if (event.getId() != null) {
            tagRepository.deleteByEventId(event.getId());
        }
        event.setTags(new ArrayList<>());
        repository.save(event);
        for (Tag tag : tagsList) {
            tag.setEventId(event.getId());
            tagRepository.save(tag);
        }
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        tagRepository.deleteByEventId(id);
        repository.deleteById(id);
    }
}
