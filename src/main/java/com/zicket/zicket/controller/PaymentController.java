package com.zicket.zicket.controller;

import com.zicket.zicket.entity.Payment;
import com.zicket.zicket.entity.Ticket;
import com.zicket.zicket.entity.User;
import com.zicket.zicket.service.PaymentService;
import com.zicket.zicket.service.TicketService;
import com.zicket.zicket.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/payments")
@Slf4j
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserService userService;

    @Autowired
    private TicketService ticketService;

    @PostMapping("/{ticketId}")
    public ResponseEntity<?> createPayment(@RequestBody Payment payment, @PathVariable String ticketId)
    {
        try{
            Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user=userService.findByUsername(username);
            paymentService.savePaymentInfo(payment, username, ticketId);
            User from=userService.findByUsername(payment.getFrom());
            Optional<Ticket> selected=ticketService.getTicketById(ticketId);
            if(selected.isPresent())
            {
                try
                {
                    Ticket ticket=selected.get();
                    ticketService.transferTicket(from.getUsername(), user.getUsername(), ticketId);
                }
                catch (IOException e)
                {
                    log.error("Error Sending mail", e);
                }

            }

            return new ResponseEntity<>(payment+"Successfull", HttpStatus.ACCEPTED);
        }
        catch (Exception e)
        {
            log.error("Invalid payment {}", String.valueOf(e));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get-payment-info/{paymentId}")
    public ResponseEntity<?> getPaymentInfo(@PathVariable ObjectId paymentId)
    {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        User user=userService.findByUsername(username);
        List<Payment> paymentList=user.getPayments();
        return new ResponseEntity<>(paymentList, HttpStatus.OK);
    }
}
