package com.zicket.zicket.service;

import com.zicket.zicket.entity.Payment;
import com.zicket.zicket.entity.User;
import com.zicket.zicket.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private UserService userService;

    @Transactional
    public void savePaymentInfo(Payment payment, String username) throws Exception
    {
        User user=userService.findByUsername(username);
        if(payment.getCardNo().length()==12 && payment.getCvv().length()==3)
        {
            String from=payment.getFrom();
            User owner =userService.findByUsername(from);
            if(owner!=null)
            {
                payment.setTo(user.getUsername());
                user.getPayments().add(payment);
                paymentRepository.save(payment);
                userService.save(user);
            }
            else {
                throw new Exception("Owner not found");
            }
        }
        else
        {
            throw new Exception("Card No or CVV is incorrect");
        }

    }
}
