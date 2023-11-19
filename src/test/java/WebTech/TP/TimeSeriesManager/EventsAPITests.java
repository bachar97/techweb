package WebTech.TP.TimeSeriesManager;

import WebTech.TP.TimeSeriesManager.api.controller.EventAPIController;
import WebTech.TP.TimeSeriesManager.dao.Event;
import WebTech.TP.TimeSeriesManager.dao.Tag;
import WebTech.TP.TimeSeriesManager.dao.TimeSeries;
import WebTech.TP.TimeSeriesManager.dao.UserTimeSeriesAccess;
import WebTech.TP.TimeSeriesManager.service.EventService;
import WebTech.TP.TimeSeriesManager.service.TimeSeriesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EventAPIController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class EventsAPITests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService service;

    @MockBean
    private TimeSeriesService timeSeriesService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    private TimeSeries timeSeries_Main;
    private Event event_Main;
    private List<Event> eventList_GET;

    private final String API_URL = "/app/api/timeseries/1/events";

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    public void init() {
        List<Tag> list = new ArrayList<>();
        list.add(new Tag("tag 1", "1"));
        list.add(new Tag("tag 2", "1"));
        event_Main = new Event("1", "1", LocalDateTime.now(), "Comment1", list, LocalDateTime.now());
        eventList_GET = new ArrayList<Event>();
        eventList_GET.add(new Event("1", "1", LocalDateTime.now(), "Comment1", list, LocalDateTime.now()));
        eventList_GET.add(new Event("2", "1", LocalDateTime.now(), "Comment2", list, LocalDateTime.now()));
        eventList_GET.add(new Event("3", "1", LocalDateTime.now(), "Comment2", list, LocalDateTime.now()));

        List<UserTimeSeriesAccess> accessList = new ArrayList<>();
        accessList.add(new UserTimeSeriesAccess("readonly", "1", false));
        accessList.add(new UserTimeSeriesAccess("canedit", "1", true));
        timeSeries_Main = new TimeSeries("1", "Test1", "Description1", "admin", accessList, LocalDateTime.now());
    }

    @Test
    @WithMockUser(username="admin", roles="ADMIN")
    void TestGetAllEvents() throws Exception {
        when(timeSeriesService.findById(timeSeries_Main.getId())).thenReturn(Optional.of(timeSeries_Main));
        when(service.findByTimeSeriesId("1")).thenReturn(eventList_GET);

        ResultActions resp = mockMvc.perform(get(API_URL)
                .contentType(MediaType.APPLICATION_JSON));

        resp.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", CoreMatchers.is(3)));
    }

    @Test
    @WithMockUser(username="user1", roles="USER")
    void TestGetAllEventsForbidden() throws Exception {
        when(timeSeriesService.findById(timeSeries_Main.getId())).thenReturn(Optional.of(timeSeries_Main));
        when(service.findByTimeSeriesId("1")).thenReturn(eventList_GET);

        ResultActions resp = mockMvc.perform(get(API_URL)
                .contentType(MediaType.APPLICATION_JSON));

        resp.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username="admin", roles="ADMIN")
    void TestGetAllEventsNotFound() throws Exception {
        when(timeSeriesService.findById(timeSeries_Main.getId())).thenReturn(Optional.of(timeSeries_Main));
        when(service.findByTimeSeriesId("1")).thenReturn(eventList_GET);

        ResultActions resp = mockMvc.perform(get(API_URL.replace('1', '2'))
                .contentType(MediaType.APPLICATION_JSON));

        resp.andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username="admin", roles="ADMIN")
    void TestGetAllEventsNotModified() throws Exception {
        when(timeSeriesService.findById(timeSeries_Main.getId())).thenReturn(Optional.of(timeSeries_Main));
        when(service.findByTimeSeriesId("1")).thenReturn(eventList_GET);

        ResultActions resp = mockMvc.perform(get(API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header("If-Modified-Since", "Fri, 31 Dec 2123 00:00:00 GMT"));

        resp.andExpect(status().isNotModified());
    }

    @Test
    @WithMockUser(username="admin", roles="ADMIN")
    void TestGetEvent() throws Exception {
        when(timeSeriesService.findById(timeSeries_Main.getId())).thenReturn(Optional.of(timeSeries_Main));
        when(service.findById(event_Main.getId())).thenReturn(Optional.of(event_Main));

        ResultActions resp = mockMvc.perform(get(API_URL + "/" + event_Main.getId())
                .contentType(MediaType.APPLICATION_JSON));

        resp.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.date", CoreMatchers.is(objectMapper.writeValueAsString(event_Main.getDate()).substring(1, 28))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.comment", CoreMatchers.is(event_Main.getComment())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tags[0]", CoreMatchers.is(event_Main.getTags().get(0).getTagName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tags[1]", CoreMatchers.is(event_Main.getTags().get(1).getTagName())));
    }

    @Test
    @WithMockUser(username="user1", roles="USER")
    void TestGetEventForbidden() throws Exception {
        when(timeSeriesService.findById(timeSeries_Main.getId())).thenReturn(Optional.of(timeSeries_Main));
        when(service.findById(event_Main.getId())).thenReturn(Optional.of(event_Main));

        ResultActions resp = mockMvc.perform(get(API_URL + "/" + event_Main.getId())
                .contentType(MediaType.APPLICATION_JSON));

        resp.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username="admin", roles="ADMIN")
    void TestGetEventNotFound() throws Exception {
        when(timeSeriesService.findById(timeSeries_Main.getId())).thenReturn(Optional.of(timeSeries_Main));
        when(service.findAll()).thenReturn(eventList_GET);

        ResultActions resp = mockMvc.perform(get(API_URL + "/notfound")
                .contentType(MediaType.APPLICATION_JSON));

        resp.andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username="admin", roles="ADMIN")
    void TestGetEventNotModified() throws Exception {
        when(timeSeriesService.findById(timeSeries_Main.getId())).thenReturn(Optional.of(timeSeries_Main));
        when(service.findById(event_Main.getId())).thenReturn(Optional.of(event_Main));

        ResultActions resp = mockMvc.perform(get(API_URL + "/" + event_Main.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("If-Modified-Since", "Fri, 31 Dec 2123 00:00:00 GMT"));

        resp.andExpect(status().isNotModified());
    }

    @Test
    @WithMockUser(username="admin", roles="ADMIN")
    public void TestPostEvent() throws Exception {
        when(timeSeriesService.findById(timeSeries_Main.getId())).thenReturn(Optional.of(timeSeries_Main));

        event_Main.setId(null);
        event_Main.setTimeSeriesId(null);

        ResultActions resp = mockMvc.perform(post(API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(event_Main.toDTO())));

        resp.andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username="admin", roles="ADMIN")
    public void TestPostEventWithId() throws Exception {
        when(timeSeriesService.findById(timeSeries_Main.getId())).thenReturn(Optional.of(timeSeries_Main));

        event_Main.setTimeSeriesId(null);

        ResultActions resp = mockMvc.perform(post(API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(event_Main.toDTO())));

        resp.andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username="admin", roles="ADMIN")
    public void TestPostEventWithTimeSeriesId() throws Exception {
        when(timeSeriesService.findById(timeSeries_Main.getId())).thenReturn(Optional.of(timeSeries_Main));

        event_Main.setId(null);

        ResultActions resp = mockMvc.perform(post(API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(event_Main.toDTO())));

        resp.andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username="readonly", roles="USER")
    public void TestPostEventForbidden() throws Exception {
        when(timeSeriesService.findById(timeSeries_Main.getId())).thenReturn(Optional.of(timeSeries_Main));

        event_Main.setId(null);
        event_Main.setTimeSeriesId(null);

        ResultActions resp = mockMvc.perform(post(API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(event_Main.toDTO())));

        resp.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username="admin", roles="ADMIN")
    public void TestPostEventNotFound() throws Exception {
        when(service.findAll()).thenReturn(eventList_GET);

        event_Main.setId(null);
        event_Main.setTimeSeriesId(null);

        ResultActions resp = mockMvc.perform(post(API_URL.replace('1', '2'))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(event_Main.toDTO())));

        resp.andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username="admin", roles="ADMIN")
    public void TestPutEvent() throws Exception {
        when(timeSeriesService.findById(timeSeries_Main.getId())).thenReturn(Optional.of(timeSeries_Main));
        when(service.findById(event_Main.getId())).thenReturn(Optional.of(event_Main));
        when(service.existsByTimeSeriesIdAndId(event_Main.getTimeSeriesId(), event_Main.getId())).thenReturn(true);

        event_Main.setId(null);
        event_Main.setTimeSeriesId(null);

        ResultActions resp = mockMvc.perform(put(API_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(event_Main.toDTO())));

        resp.andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username="admin", roles="ADMIN")
    public void TestPutEventWithId() throws Exception {
        when(timeSeriesService.findById(timeSeries_Main.getId())).thenReturn(Optional.of(timeSeries_Main));
        when(service.findById(event_Main.getId())).thenReturn(Optional.of(event_Main));
        when(service.existsByTimeSeriesIdAndId(event_Main.getTimeSeriesId(), event_Main.getId())).thenReturn(true);

        event_Main.setTimeSeriesId(null);

        ResultActions resp = mockMvc.perform(put(API_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(event_Main.toDTO())));

        resp.andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username="admin", roles="ADMIN")
    public void TestPutEventWithTimeSeriesId() throws Exception {
        when(timeSeriesService.findById(timeSeries_Main.getId())).thenReturn(Optional.of(timeSeries_Main));
        when(service.findById(event_Main.getId())).thenReturn(Optional.of(event_Main));
        when(service.existsByTimeSeriesIdAndId(event_Main.getTimeSeriesId(), event_Main.getId())).thenReturn(true);

        event_Main.setId(null);

        ResultActions resp = mockMvc.perform(put(API_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(event_Main.toDTO())));

        resp.andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username="admin", roles="ADMIN")
    public void TestPutEventNotFound() throws Exception {
        when(timeSeriesService.findById(timeSeries_Main.getId())).thenReturn(Optional.of(timeSeries_Main));
        when(service.findAll()).thenReturn(eventList_GET);

        event_Main.setId(null);
        event_Main.setTimeSeriesId(null);

        ResultActions resp = mockMvc.perform(put(API_URL + "/notfound")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(event_Main.toDTO())));

        resp.andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username="readonly", roles="USER")
    public void TestPutEventForbidden() throws Exception {
        when(timeSeriesService.findById(timeSeries_Main.getId())).thenReturn(Optional.of(timeSeries_Main));
        when(service.findById(event_Main.getId())).thenReturn(Optional.of(event_Main));
        when(service.existsByTimeSeriesIdAndId(event_Main.getTimeSeriesId(), event_Main.getId())).thenReturn(true);

        event_Main.setId(null);
        event_Main.setTimeSeriesId(null);

        ResultActions resp = mockMvc.perform(put(API_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(event_Main.toDTO())));

        resp.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username="admin", roles="ADMIN")
    void TestDeleteEvent() throws Exception {
        when(timeSeriesService.findById(timeSeries_Main.getId())).thenReturn(Optional.of(timeSeries_Main));
        when(service.findById(event_Main.getId())).thenReturn(Optional.of(event_Main));
        when(service.existsByTimeSeriesIdAndId(event_Main.getTimeSeriesId(), event_Main.getId())).thenReturn(true);

        ResultActions resp = mockMvc.perform(delete(API_URL + "/" + event_Main.getId())
                .contentType(MediaType.APPLICATION_JSON));

        resp.andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username="readonly", roles="USER")
    void TestDeleteEventForbidden() throws Exception {
        when(timeSeriesService.findById(timeSeries_Main.getId())).thenReturn(Optional.of(timeSeries_Main));
        when(service.findById(event_Main.getId())).thenReturn(Optional.of(event_Main));
        when(service.existsByTimeSeriesIdAndId(event_Main.getTimeSeriesId(), event_Main.getId())).thenReturn(true);

        ResultActions resp = mockMvc.perform(delete(API_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON));

        resp.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username="admin", roles="ADMIN")
    void TestDeleteEventNotFound() throws Exception {
        when(timeSeriesService.findById(timeSeries_Main.getId())).thenReturn(Optional.of(timeSeries_Main));
        when(service.findAll()).thenReturn(eventList_GET);

        ResultActions resp = mockMvc.perform(delete(API_URL + "/notfound")
                .contentType(MediaType.APPLICATION_JSON));

        resp.andExpect(status().isOk());
    }
}