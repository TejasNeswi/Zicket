package com.zicket.zicket.controller;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.zicket.zicket.entity.Ticket;
import com.zicket.zicket.entity.User;
import com.zicket.zicket.service.TicketService;
import com.zicket.zicket.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/ticket")
@Slf4j
public class TicketController {


    @Autowired
    private TicketService ticketService;
    @Autowired
    private UserService userService;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFsOperations gridFsOperations;

    @GetMapping("/get-my-tickets")
    public ResponseEntity<?> getAllTicketsOfUser()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        User userInDb=userService.findByUsername(username);
        List<Ticket> allTickets=userInDb.getMyTickets();
        if(allTickets!=null && !allTickets.isEmpty())
            return new ResponseEntity<>(allTickets, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/get-purchased-tickets")
    public ResponseEntity<?> getPurchasedTicketsOfUser()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        User userInDb=userService.findByUsername(username);
        List<Ticket> allTickets=userInDb.getPurchasedTickets();
        if(allTickets!=null && !allTickets.isEmpty())
            return new ResponseEntity<>(allTickets, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/get-event-info/{ticketId}")
    public ResponseEntity<?> getTicketInfo(@PathVariable String ticketId)
    {
        Optional<Ticket> clicked=ticketService.getTicketById(ticketId);
        if(clicked.isPresent())
        {
            Ticket ticket=clicked.get();
            return new ResponseEntity<>(ticket, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @PostMapping
    public ResponseEntity<?> createTicket(@RequestBody Ticket ticket)
    {
        try
        {
            Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
            String username=authentication.getName();
            ticketService.saveTicketDetails(username, ticket);
            return new ResponseEntity<>(ticket, HttpStatus.CREATED);
        }
        catch (Exception e)
        {
            log.error(String.valueOf(e));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/upload-ticket/{id}")
    public ResponseEntity<?> uploadTicketFile(@PathVariable String id, @RequestBody MultipartFile file)
    {
        try
        {
            Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
            String username=authentication.getName();
            Optional<Ticket> ticket=ticketService.getTicketById(id);
            if(ticket.get()!=null)
            {
                ticketService.saveTicketFile(ticket.get(), file);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        catch (Exception e)
        {
            log.error(String.valueOf(e));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/view-ticket/{ticketid}")
    public ResponseEntity<?> viewTicketFile(@PathVariable String ticketid) throws IOException
    {
        try
        {
            Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
            String username=authentication.getName();
            if(ticketService.hasTicketFile(username, ticketid))
            {
                InputStreamResource body=ticketService.getFileBody(ticketid);
                HttpHeaders headers=ticketService.getHeaders(ticketid);
                return new ResponseEntity<>(body, headers, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch (NoSuchElementException e)
        {
            log.error(String.valueOf(e));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/id/{ticketId}")
    public ResponseEntity<?> updateTicket(@RequestBody Ticket ticket, @PathVariable String ticketId)
    {
        try
        {
            Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
            String username=authentication.getName();
            User user=userService.findByUsername(username);
            List<Ticket> collect = user.getMyTickets().stream().filter(x -> x.getTicketId().equals(ticketId)).collect(Collectors.toList());
            if(!collect.isEmpty())
            {
                Optional<Ticket> ticketOfUser=ticketService.getTicketById(ticketId);
                if(ticketOfUser.isPresent())
                {
                    Ticket oldTicket=ticketOfUser.get();
                    oldTicket.setLocation(ticket.getLocation()!=null && !ticket.getLocation().equals("")? ticket.getLocation(): oldTicket.getLocation());
                    oldTicket.setStand(ticket.getStand()!=null && !ticket.getStand().equals("")? ticket.getStand(): oldTicket.getStand());
                    oldTicket.setEventName(ticket.getEventName()!=null && !ticket.getEventName().equals("")? ticket.getEventName(): oldTicket.getEventName());
                    oldTicket.setPrice(ticket.getPrice()!=null && !ticket.getPrice().equals("")? ticket.getPrice(): oldTicket.getPrice());
                    oldTicket.setDate(ticket.getDate()!=null && !ticket.getDate().equals("")? ticket.getDate(): oldTicket.getDate());
                    ticketService.saveTicketDetails(username, oldTicket);
                    return new ResponseEntity<>(HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            return new ResponseEntity<>((HttpStatus.BAD_REQUEST));
        }
    }

    @PutMapping("/update-ticketfile/{id}")
    public ResponseEntity<?> updateTicketfile(@PathVariable String id, @RequestBody MultipartFile file) throws IOException {
        Optional<Ticket> ticketById = ticketService.getTicketById(id);
        if(ticketById==null)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ticketService.updateTicketFile(ticketById.get(), file);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @DeleteMapping("/id/{ticketId}")
    public ResponseEntity<?> deleteTicket(@PathVariable String ticketId)
    {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        Ticket removed=ticketService.deleteTicket(username, ticketId);
        if(removed!=null)
            return new ResponseEntity<>(removed, HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
