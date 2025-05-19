package com.zicket.zicket.controller;

import com.zicket.zicket.entity.Ticket;
import com.zicket.zicket.entity.User;
import com.zicket.zicket.service.TicketService;
import com.zicket.zicket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public")
public class PublicController {


    @Autowired
    private UserService userService;

    @Autowired
    private TicketService ticketService;

    @PostMapping("/signup")
    public ResponseEntity<?> createNewUser(@RequestBody User user)
    {
        userService.saveNewUser(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public List<Ticket> getAllEvents()
    {
        return ticketService.getAllEvents("allEvents");
    }
}
