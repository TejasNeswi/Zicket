package com.zicket.zicket.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class EmailService {

    @Value("${sendgridApi}")
    private String key;

    @Autowired
    private JavaMailSender javaMailSender;


    public void sendMail(String to, String subject, String text)
    {
        try
        {
            SimpleMailMessage mail=new SimpleMailMessage();
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(text);
            javaMailSender.send(mail);
        }
        catch (Exception e)
        {
            log.error("Error while sending mail", e);
        }
    }

    public void sendEmail(String from, String subject, String to, String body) throws IOException {
        Email receiver=new Email(to);
        Email sender=new Email(from);

        Content content=new Content("text/plain", body);
        Mail mail=new Mail(sender, subject, receiver, content);
        SendGrid grid=new SendGrid(key);
        Request request=new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        Response response=grid.api(request);
    }
}
