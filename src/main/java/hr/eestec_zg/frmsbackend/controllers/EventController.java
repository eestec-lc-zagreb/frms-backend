package hr.eestec_zg.frmsbackend.controllers;

import hr.eestec_zg.frmscore.domain.models.Company;
import hr.eestec_zg.frmscore.domain.models.CompanyType;
import hr.eestec_zg.frmscore.domain.models.Event;
import hr.eestec_zg.frmscore.domain.models.Task;
import hr.eestec_zg.frmscore.domain.models.User;
import hr.eestec_zg.frmscore.services.EventService;
import hr.eestec_zg.frmscore.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class EventController {

    @Autowired
    private EventService eventService;
    @Autowired
    private TaskService taskService;


    @RequestMapping(value = "/events/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Event getEvent(@PathVariable("id") Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null value");
        }

        return eventService.getEventById(id);
    }

    @RequestMapping(value = "/events", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<Event> getEvents() {
        return eventService.getEvents();
    }

    @RequestMapping(value = "/events", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Event createEvent(@RequestBody Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event must not be null value");
        }

        eventService.createEvent(event);
        return event;
    }

    @RequestMapping(value = "/events/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void updateEvents(@PathVariable("id") Long id, @RequestBody Event event) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null value");
        }

        Event oldEvent = this.eventService.getEventById(id);

        oldEvent.setName(event.getName());
        oldEvent.setShortName(event.getShortName());
        oldEvent.setYear(event.getYear());

        eventService.updateEvent(oldEvent);
    }

    @RequestMapping(value = "/events/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteEvent(@PathVariable("id") Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null value");
        }

        eventService.deleteEvent(id);
    }

    @RequestMapping(value = "/events/{id}/tasks", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<Task> getTasksByEvent(@PathVariable("id") Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null value");
        }

        return taskService.getTasksByEvent(id);
    }

    @RequestMapping(value = "/events/{id}/tasks/assign", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<Company> getUnassignedCompaniesForEvent(@PathVariable("id") Long id, String name, CompanyType type) {
        List<Company> companies = taskService.getCompaniesForWhichThereAreNoTasksForEvent(id);

        Stream<Company> companyStream = companies.stream();

        if (name != null) {
            companyStream = companyStream
                    .filter(c -> {
                        final String searchTerm = name.toLowerCase();

                        return c.getName().toLowerCase().contains(searchTerm) ||
                                c.getShortName().toLowerCase().contains(searchTerm);
                    });
        }

        if (type != null) {
            companyStream = companyStream.filter(c -> c.getType().equals(type));
        }

        return companyStream.collect(Collectors.toList());
    }

    @RequestMapping(value = "/events/{id}/users", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Set<User> getUsersByEvent(@PathVariable("id") Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null value");
        }

        Set<User> users = new HashSet<>();
        List<Task> tasks = taskService.getTasksByEvent(id);

        tasks.stream()
                .filter(task -> task.getAssignee() != null)
                .forEach(filteredTask -> users.add(filteredTask.getAssignee()));

        return users;
    }
}