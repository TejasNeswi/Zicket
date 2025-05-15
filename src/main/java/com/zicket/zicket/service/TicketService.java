package com.zicket.zicket.service;

import com.zicket.zicket.entity.Ticket;
import com.zicket.zicket.entity.User;
import com.zicket.zicket.repository.TicketRepository;
import com.zicket.zicket.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public void saveTicketDetails(String username, Ticket ticket)
    {
        try
        {
            User user=userService.findByUsername(username);
            ticketRepository.save(ticket);
            if(user.getTickets()==null)
            {
                user.setTickets(new ArrayList<>());
            }
            user.getTickets().add(ticket);
            userService.save(user);
        }
        catch (Exception e)
        {
            log.error("Error while creating a ticket "+e);
        }
    }


    public Optional<Ticket> getTicketById(ObjectId id)
    {
        return ticketRepository.findById(id);
    }

    public Ticket deleteTicket(String username, ObjectId id)
    {
        User user=userService.findByUsername(username);
        if(user.getTickets().contains(ticketRepository.findById(id).get()))
        {
            Optional<Ticket> ticket=ticketRepository.findById(id);
            user.getTickets().remove(id);
            ticketRepository.deleteById(id);
            userService.save(user);
            if(ticket.isPresent())
            {
                return ticket.get();
            }
        }
        return null;

    }
}
