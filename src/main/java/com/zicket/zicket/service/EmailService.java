package com.zicket.zicket.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Component
@Slf4j
public class EmailService {

    @Value("${sendgridApi}")
    private String key;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFsOperations gridFsOperations;


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

    public void sendEmail(String from, String subject, String to, String fileid, String body) throws RuntimeException, IOException {
        Email receiver=new Email(to);
        Email sender=new Email(from);
        Query query=new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(fileid)));
        GridFSFile file=gridFsTemplate.findOne(query);
        GridFsResource resource=gridFsOperations.getResource(file);
        if(resource==null || !resource.exists())
        {
            throw new RuntimeException("File not found");
        }
        byte filebytes[];
        InputStream inputStream=resource.getInputStream();
        filebytes=inputStream.readAllBytes();

        String base64encoded= Base64.getEncoder().encodeToString(filebytes);
        Content content=new Content("text/plain", body);
        Mail mail=new Mail(sender, subject, receiver, content);
        Attachments attachments=new Attachments();
        attachments.setContent(base64encoded);
        attachments.setType(resource.getContentType());
        attachments.setFilename(resource.getFilename());
        attachments.setDisposition("attachment");
        mail.addAttachments(attachments);
        SendGrid grid=new SendGrid(key);
        Request request=new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        Response response=grid.api(request);
    }
}
