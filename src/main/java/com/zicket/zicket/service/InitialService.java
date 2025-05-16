package com.zicket.zicket.service;

import com.zicket.zicket.entity.Ticket;
import com.zicket.zicket.repository.TicketRepository;
import com.zicket.zicket.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InitialService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

//    @Scheduled(cron="0 0 1,16 * *")
    @PostConstruct
    public void deletePreviousTickets()
    {
        List<Ticket> allTickets=ticketRepository.findAll();
        List<Ticket> toBeDeleted=allTickets.stream().filter(x-> x.getTime().isBefore(LocalDateTime.now().minus(15, ChronoUnit.DAYS))).collect(Collectors.toList());
        for(Ticket ticket: toBeDeleted)
        {
            ticketRepository.delete(ticket);
        }
    }
}
