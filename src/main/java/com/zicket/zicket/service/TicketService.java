package com.zicket.zicket.service;

import com.zicket.zicket.entity.Payment;
import com.zicket.zicket.entity.Ticket;
import com.zicket.zicket.entity.User;
import com.zicket.zicket.repository.TicketRepository;
import com.zicket.zicket.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Value("${emailid}")
    private String emailAddress;

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private EmailService emailService;

    public List<Ticket> getAllEvents()
    {

        return ticketRepository.findAll();
    }

    @Transactional
    public void saveTicketDetails(String username, Ticket ticket)
    {
        try
        {
            User user=userService.findByUsername(username);
            ticket.setTime(LocalDateTime.now());
            ticket.setStatus(true);
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
            log.error("Error while creating a ticket {}", String.valueOf(e));
        }
    }


    public Optional<Ticket> getTicketById(ObjectId id)
    {
        return ticketRepository.findById(id);
    }

    @Transactional
    public Ticket deleteTicket(String username, ObjectId id)
    {
        User user=userService.findByUsername(username);
        Optional<Ticket> ticket=ticketRepository.findById(id);
        if(ticket.isPresent() && user.getTickets().contains(ticket.get()))
        {
            user.getTickets().remove(ticket.get());
            ticketRepository.deleteById(id);
            userService.save(user);
            return ticket.get();
        }
        return null;
    }

    @Transactional
    public void transferTicket(String from, String to, ObjectId id) throws IOException {
        User owner=userService.findByUsername(from);
        User buyer=userService.findByUsername(to);
        Optional<Ticket> ticket = ticketRepository.findById(id);
        if(ticket.isPresent() && owner.getTickets().contains(ticket.get()))
        {
            owner.getTickets().remove(ticket.get());
            buyer.getTickets().add(ticket.get());
            ticket.get().setStatus(false);
            ticketRepository.save(ticket.get());
            userService.save(buyer);
            userService.save(owner);
            Payment payment=paymentService.fetchPaymentInfo(buyer.getUsername());
            emailService.sendEmail(emailAddress, "Payment Status and Ticket Info", buyer.getEmail() , "Payment of "+ticket.get().getPrice()+" successful.\n\nYour payment info: \n"+paymentService.fetchPaymentInfo(buyer.getUsername()).toString()+"\n\nYour ticket id is "+ticket.get().getTicketId()+".\n\nYour ticket details are:\n"+ticket.get().toString());
        }
    }
}
