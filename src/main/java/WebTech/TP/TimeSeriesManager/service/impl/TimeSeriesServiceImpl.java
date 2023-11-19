package WebTech.TP.TimeSeriesManager.service.impl;

import WebTech.TP.TimeSeriesManager.dao.Event;
import WebTech.TP.TimeSeriesManager.dao.TimeSeries;
import WebTech.TP.TimeSeriesManager.dao.UserTimeSeriesAccess;
import WebTech.TP.TimeSeriesManager.repo.TimeSeriesRepository;
import WebTech.TP.TimeSeriesManager.repo.UserTimeSeriesAccessRepository;
import WebTech.TP.TimeSeriesManager.service.EventService;
import WebTech.TP.TimeSeriesManager.service.TimeSeriesService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TimeSeriesServiceImpl implements TimeSeriesService {
    private final TimeSeriesRepository repository;
    private final UserTimeSeriesAccessRepository accessRepository;
    private final EventService eventService;

    @Autowired
    public TimeSeriesServiceImpl(TimeSeriesRepository repository, UserTimeSeriesAccessRepository accessRepository, EventService eventService) {
        this.repository = repository;
        this.accessRepository = accessRepository;
        this.eventService = eventService;
    }

    @Override
    public List<TimeSeries> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<TimeSeries> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public boolean existsById(String id) {
        return repository.existsById(id);
    }

    @Override
    @Transactional
    public void save(TimeSeries timeSeries) {
        List<UserTimeSeriesAccess> accessList = timeSeries.getUserAccess();
        if (timeSeries.getId() != null) {
            accessRepository.deleteByTimeSeriesId(timeSeries.getId());
        }
        timeSeries.setUserAccess(new ArrayList<>());
        repository.save(timeSeries);
        for (UserTimeSeriesAccess access : accessList) {
            access.setTimeSeriesId(timeSeries.getId());
            accessRepository.save(access);
        }
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        accessRepository.deleteByTimeSeriesId(id);
        List<Event> events = eventService.findByTimeSeriesId(id);
        for(Event event : events) {
            eventService.deleteById(event.getId());
        }
        repository.deleteById(id);
    }
}
