package com.zicket.zicket.service;

import com.zicket.zicket.entity.Payment;
import com.zicket.zicket.entity.Ticket;
import com.zicket.zicket.entity.User;
import com.zicket.zicket.repository.PaymentRepository;
import com.zicket.zicket.repository.TicketRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private PasswordEncoder cvvEncoder;

    @Transactional
    public void savePaymentInfo(Payment payment, String username, String ticketId) throws Exception
    {
        User user=userService.findByUsername(username);
        if(payment.getCardNo().length()==12 && payment.getCvv().length()==3)
        {
            String from=payment.getFrom();
            User owner =userService.findByUsername(from);
            Optional<Ticket> ticket = ticketRepository.findById(ticketId);
            if(owner!=null && owner.getMyTickets().contains(ticket.get()) && ticket.get().isStatus())
            {
                payment.setTo(user.getUsername());
                payment.setTimestamp(LocalDateTime.now());
                user.getPayments().add(payment);
                payment.setCvv(cvvEncoder.encode(payment.getCvv()));
                paymentRepository.save(payment);
                userService.save(user);
            }
            else {
                throw new Exception("Ticket Already sold or Ticket no found");
            }
        }
        else
        {
            throw new Exception("Card No or CVV is incorrect");
        }
    }

    public String fetchPaymentInfo(String username)
    {
        try {
            User user=userService.findByUsername(username);
            List<Payment> paymentList=user.getPayments();
            Payment mostRecentPayment=paymentList.get(paymentList.size()-1);
            return "Payment id=" + mostRecentPayment.getPaymentId() + "\nCard No=" + mostRecentPayment.getCardNo() + "\nDate and time of transaction: " + mostRecentPayment.getTimestamp() + "\n\nTicket transferred from " + mostRecentPayment.getFrom() + " to " + mostRecentPayment.getTo();
        }
        catch (Exception e)
        {
            log.error("Error fetching payment info", e);
        }
        return null;
    }
}
