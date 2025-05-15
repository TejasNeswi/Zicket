package com.zicket.zicket.controller;

import com.zicket.zicket.entity.Ticket;
import com.zicket.zicket.service.TicketService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ticket")
public class TicketController {


    @Autowired
    private TicketService ticketService;

    @GetMapping
    public List<Ticket> getAllTickets()
    {
        return ticketService.getAllTickets();
    }
    @PostMapping
    public boolean addTicket(@RequestBody Ticket ticket)
    {
        ticketService.saveTicketDetails(ticket);
        return true;
    }
    @PutMapping("/{ticketId}")
    public void updateTicket(@RequestBody Ticket ticket, @PathVariable ObjectId ticketId)
    {
        Optional<Ticket> ticketById = ticketService.getTicketById(ticketId);
        if(ticketById.isPresent())
        {
            Ticket old=ticketById.get();
            old.setStand(ticket.getStand()!=null && !ticket.getStand().equals("")?ticket.getStand(): old.getStand());
            old.setLocation(ticket.getLocation()!=null && !ticket.getLocation().equals("")?ticket.getLocation(): old.getLocation());
            old.setEventName(ticket.getEventName()!=null && !ticket.getEventName().equals("")?ticket.getEventName(): old.getEventName());
            old.setEventType(ticket.getEventType()!=null && !ticket.getEventType().equals("")?ticket.getEventType(): old.getEventType());
            ticketService.saveTicketDetails(old);
        }
    }
    @DeleteMapping("/{ticketId}")
    public void deleteTicket(@PathVariable ObjectId ticketId)
    {
        ticketService.deleteTicket(ticketId);
    }
}
