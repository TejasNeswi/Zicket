package com.zicket.zicket.controller;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.zicket.zicket.utils.JwtUtil;
import com.zicket.zicket.cache.AppCache;
import com.zicket.zicket.entity.Ticket;
import com.zicket.zicket.entity.User;
import com.zicket.zicket.enums.EventType;
import com.zicket.zicket.service.TicketService;
import com.zicket.zicket.service.UserDetailsServiceImplementation;
import com.zicket.zicket.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/public")
@Slf4j
public class PublicController {


    @Autowired
    private UserService userService;

    @Autowired
    private TicketService ticketService;

    @Value("${eventsKey}")
    private String eventsKey;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AppCache appCache;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFsOperations gridFsOperations;

    @Autowired
    private UserDetailsServiceImplementation userDetailsServiceImplementation;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user)
    {
        userService.saveNewUser(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user)
    {
        try
        {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
            UserDetails userDetails=userDetailsServiceImplementation.loadUserByUsername(user.getUsername());
            String jwt=jwtUtil.generateToken(userDetails.getUsername());
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        }
        catch (Exception e)
        {
            log.error(String.valueOf(e));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/health-check")
    public String healthCheck()
    {
        return "Health is fine";
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
