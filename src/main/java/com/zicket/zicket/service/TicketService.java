package com.zicket.zicket.service;

import com.zicket.zicket.entity.Ticket;
import com.zicket.zicket.repository.TicketRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public void saveTicketDetails(Ticket ticket)
    {
        ticketRepository.save(ticket);
    }

    public List<Ticket> getAllTickets()
    {
        return ticketRepository.findAll();
    }

    public Optional<Ticket> getTicketById(ObjectId id)
    {
        return ticketRepository.findById(id);
    }

    public void deleteTicket(ObjectId id)
    {
        ticketRepository.deleteById(id);
    }
}
