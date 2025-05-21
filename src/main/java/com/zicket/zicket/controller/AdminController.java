package com.zicket.zicket.controller;

import com.zicket.zicket.cache.AppCache;
import com.zicket.zicket.entity.Payment;
import com.zicket.zicket.entity.Ticket;
import com.zicket.zicket.entity.User;
import com.zicket.zicket.repository.UserRepository;
import com.zicket.zicket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Value("${eventsKey}")
    private String eventsKey;

    @Value("${usersKey}")
    private String usersKey;

    @Value("${paymentsKey}")
    private String paymentsKey;
    @Autowired
    private UserService userService;

    @Autowired
    private AppCache appCache;

    @GetMapping("/get-all-users")
    public List<User> getAllUsers()
    {
        List<User> allUsers=(List<User>) appCache.ticketCache.get(usersKey);
        return allUsers;
    }
    @GetMapping("/get-all-tickets")
    public List<Ticket> getAllTickets()
    {
        List<Ticket> allTickets=(List<Ticket>) appCache.ticketCache.get(eventsKey);
        return allTickets;
    }
    @GetMapping("/get-all-payments")
    public List<Payment> getAllPayments()
    {
        List<Payment> allPayments=(List<Payment>) appCache.ticketCache.get(paymentsKey);
        return allPayments;
    }

    @PostMapping("add-user")
    public void addUser(@RequestBody User user)
    {
        userService.saveNewUserByAdmin(user);
    }
}
