package com.zicket.zicket.cache;

import com.zicket.zicket.entity.Payment;
import com.zicket.zicket.entity.Ticket;
import com.zicket.zicket.entity.User;
import com.zicket.zicket.repository.PaymentRepository;
import com.zicket.zicket.repository.UserRepository;
import com.zicket.zicket.service.TicketService;
import com.zicket.zicket.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppCache {

    public Map<String, Object> ticketCache;

    @Value("${eventsKey}")
    private String eventsKey;
    @Value("${usersKey}")
    private String usersKey;
    @Value("${paymentsKey}")
    private String paymentsKey;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @PostConstruct
    public void initializeCache()
    {
        ticketCache=new HashMap<>();
        List<Ticket> allTickets=ticketService.getAllEvents(eventsKey);
        List<User> allUsers=userRepository.findAll();
        List<Payment> allPayments=paymentRepository.findAll();
        ticketCache.put(eventsKey, allTickets);
        ticketCache.put(usersKey, allUsers);
        ticketCache.put(paymentsKey, allPayments);
    }
}
