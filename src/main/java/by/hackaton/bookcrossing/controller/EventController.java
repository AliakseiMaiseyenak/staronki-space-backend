package by.hackaton.bookcrossing.controller;

import by.hackaton.bookcrossing.dto.EventDto;
import by.hackaton.bookcrossing.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/events")
public class EventController {

    private EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<EventDto>> getEvents(@RequestParam(required = false) LocalDate date) {
        List<EventDto> events;
        if (date != null) {
            events = eventService.getEventsByDate(date);
        } else {
            events = eventService.getEvents();
        }
        return ok(events);
    }

    @GetMapping("/today")
    public ResponseEntity<List<EventDto>> getTodayEvent() {
        return ok(eventService.getEventsByDate(LocalDate.now()));
    }

    @PostMapping
    public ResponseEntity<Void> createEvent(@RequestBody @Valid EventDto dto) {
        eventService.createEvent(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
