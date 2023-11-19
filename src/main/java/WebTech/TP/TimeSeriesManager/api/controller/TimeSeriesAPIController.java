package WebTech.TP.TimeSeriesManager.api.controller;

import WebTech.TP.TimeSeriesManager.dao.TimeSeries;

import WebTech.TP.TimeSeriesManager.dao.UserAccessType;
import WebTech.TP.TimeSeriesManager.dto.TimeSeriesDTO;
import WebTech.TP.TimeSeriesManager.service.TimeSeriesService;
import WebTech.TP.TimeSeriesManager.util.StringUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/app/api/timeseries")
public class TimeSeriesAPIController {

    private final TimeSeriesService service;

    CacheControl cacheControl = CacheControl.maxAge(5, TimeUnit.MINUTES)
            .noTransform()
            .mustRevalidate();

    @Autowired
    public TimeSeriesAPIController(TimeSeriesService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Object> getAllTimeSeries() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<TimeSeries> list = service.findAll();
        List<TimeSeriesDTO> listDTO = new ArrayList<>();
        for(TimeSeries timeSeries : list) {
            if(timeSeries.getUserAccessType(authentication.getName()) != UserAccessType.NOT_AUTHORIZED) {
                listDTO.add(timeSeries.toDTO());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(listDTO);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity<Object> getTimeSeries(@PathVariable String id, WebRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<TimeSeries> timeSeries = service.findById(id);
        if(timeSeries.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Time series %s does not exist", id));
        }

        if(timeSeries.get().getUserAccessType(authentication.getName()) == UserAccessType.NOT_AUTHORIZED) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(String.format("You are not authorized to view time series %s", id));
        }

        if (request.checkNotModified(timeSeries.get().getUpdateTimestamp()
                .atZone(ZoneId.of("GMT")).toInstant().toEpochMilli())) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }

        return ResponseEntity.status(HttpStatus.OK).cacheControl(cacheControl).body(timeSeries.get().toDTO());
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Object> postTimeSeries(@RequestBody TimeSeriesDTO timeseries) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!StringUtilities.isBlankOrNull(timeseries.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The ID is automatically generated");
        }

        timeseries.setCreator(authentication.getName());

        TimeSeries savedTimeSeries = timeseries.toEntity();
        service.save(savedTimeSeries);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTimeSeries.toDTO());
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    public ResponseEntity<Object> putTimeSeries(@PathVariable String id, @RequestBody TimeSeriesDTO timeseries) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<TimeSeries> oldTimeSeries = service.findById(id);
        if(oldTimeSeries.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Time series %s does not exist", id));
        }

        if(oldTimeSeries.get().getUserAccessType(authentication.getName()) != UserAccessType.FULL) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(String.format("You are not authorized to edit time series %s", id));
        }

        if(!StringUtilities.isBlankOrNull(timeseries.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Updating the ID is not allowed");
        }
        timeseries.setId(id);

        if(!StringUtilities.isBlankOrNull(timeseries.getCreator())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Updating the creator is not allowed");
        }
        timeseries.setCreator(oldTimeSeries.get().getCreator());

        TimeSeries savedTimeSeries = timeseries.toEntity();
        service.save(savedTimeSeries);
        return ResponseEntity.status(HttpStatus.OK).body(savedTimeSeries.toDTO());
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public ResponseEntity<Object> deleteTimeSeries(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<TimeSeries> timeSeries = service.findById(id);
        if(timeSeries.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(String.format("Time series %s does not exist or was already deleted", id));
        }

        if(timeSeries.get().getUserAccessType(authentication.getName()) != UserAccessType.FULL) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(String.format("You are not authorized to delete time series %s", id));
        }
        service.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body(String.format("Time series %s was deleted", id));
    }
}
