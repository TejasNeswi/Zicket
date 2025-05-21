package com.zicket.zicket.controller;

import com.zicket.zicket.cache.AppCache;
import com.zicket.zicket.entity.Ticket;
import com.zicket.zicket.entity.User;
import com.zicket.zicket.enums.EventType;
import com.zicket.zicket.service.TicketService;
import com.zicket.zicket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/public")
public class PublicController {


    @Autowired
    private UserService userService;

    @Autowired
    private TicketService ticketService;

    @Value("${eventsKey}")
    private String eventsKey;

    @Autowired
    private AppCache appCache;

    @PostMapping("/signup")
    public ResponseEntity<?> createNewUser(@RequestBody User user)
    {
        userService.saveNewUser(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public List<Ticket> getAllEvents()
    {
        return ticketService.getAllEvents(eventsKey);
    }

    @GetMapping("/get-concert-tickets")
    public List<Ticket> getConcertTickets()
    {
        List<Ticket> tickets=(List<Ticket>) appCache.ticketCache.get(eventsKey);
        return tickets.stream().filter(x->x.getEventType().equals(EventType.CONCERT)).collect(Collectors.toList());
    }

    @GetMapping("/get-sports-tickets")
    public List<Ticket> getSportsTickets()
    {
        List<Ticket> tickets=(List<Ticket>) appCache.ticketCache.get(eventsKey);
        return tickets.stream().filter(x->x.getEventType().equals(EventType.SPORTS)).collect(Collectors.toList());
    }

    @GetMapping("/get-comedy-tickets")
    public List<Ticket> getComedyTickets()
    {
        List<Ticket> tickets=(List<Ticket>) appCache.ticketCache.get(eventsKey);
        return tickets.stream().filter(x->x.getEventType().equals(EventType.COMEDY)).collect(Collectors.toList());
    }

    @GetMapping("/get-theater-tickets")
    public List<Ticket> getTheaterTickets()
    {
        List<Ticket> tickets=(List<Ticket>) appCache.ticketCache.get(eventsKey);
        return tickets.stream().filter(x->x.getEventType().equals(EventType.THEATER)).collect(Collectors.toList());
    }
}
