package com.zicket.zicket.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.zicket.zicket.cache.AppCache;
import com.zicket.zicket.entity.Payment;
import com.zicket.zicket.entity.Ticket;
import com.zicket.zicket.entity.User;
import com.zicket.zicket.repository.TicketRepository;
import com.zicket.zicket.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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
    private InitialService initialService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFsOperations gridFsOperations;

    @Autowired
    private AppCache appCache;

//    @Autowired
//    private RedisService redisService;


    public List<Ticket> getAllEvents(String key)
    {
//        List<Ticket> all = redisService.get(key);
//        if(all!=null)
//        {
//            return all;
//        }
        List<Ticket> tickets = ticketRepository.findAll();
//        redisService.set(key, tickets, 300l);
        return tickets;
    }

    @Transactional
    public void saveTicketDetails(String username, Ticket ticket) throws Exception
    {

            List<Ticket> tickets = (List<Ticket>) appCache.ticketCache.get("allEvents");
            for(Ticket t: tickets)
            {
                if(t.getStand().equals(ticket.getStand()))
                {
                    throw new Exception("Ticket already exists");
                }
            }
            int i=initialService.getLatestId();
            User user=userService.findByUsername(username);
            ticket.setTime(LocalDateTime.now());
            ticket.setTicketId(String.valueOf("000000"+(++i)));
            ticket.setStatus(true);
            ticketRepository.save(ticket);
            if(user.getMyTickets()==null)
            {
                user.setMyTickets(new ArrayList<>());
            }
            user.getMyTickets().add(ticket);
            userService.save(user);

    }

    public InputStreamResource getFileBody(String ticketid)throws NoSuchElementException, IOException
    {
        Optional<Ticket> ticketById = getTicketById(ticketid);
        if(ticketById==null)
        {
            throw new NoSuchElementException("Ticket not found");
        }
        String fileid=ticketById.get().getFileId();
        Query query=new Query();
        query.addCriteria(Criteria.where("_id").is(fileid));
        GridFSFile file = gridFsTemplate.findOne(query);
        GridFsResource resource= gridFsOperations.getResource(file);
        return new InputStreamResource(resource.getInputStream());
    }

    public HttpHeaders getHeaders(String ticketid)throws NoSuchElementException, IOException
    {
        Optional<Ticket> ticketById = getTicketById(ticketid);
        if(ticketById==null) {
            throw new NoSuchElementException("Ticket not found");
        }
        Query query=new Query();
        query.addCriteria(Criteria.where("_id").is(ticketById.get().getFileId()));
        GridFSFile file = gridFsTemplate.findOne(query);
        GridFsResource resource = gridFsOperations.getResource(file);
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(resource.getContentType()));
        headers.set("Content-disposition", "attachment; filename: "+resource.getFilename());
        return headers;
    }

    public boolean hasTicketFile(String username, String ticketid)
    {
        User user=userService.findByUsername(username);
        Optional<Ticket> ticketById = getTicketById(ticketid);
        if(user.getMyTickets().contains(ticketById.get()) || user.getPurchasedTickets().contains(ticketById.get()))
        {
            return true;
        }
        return false;
    }

    public  void updateTicketFile(Ticket ticket, MultipartFile file) throws IOException {
        String fileid=ticket.getFileId();
        Query query=new Query();
        query.addCriteria(Criteria.where("_id").is(fileid));
        gridFsTemplate.delete(query);
        ObjectId storedId = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType());
        ticket.setFileId(storedId.toHexString());
        ticketRepository.save(ticket);
    }

    public void saveTicketFile(Ticket ticket, MultipartFile file) throws IOException {
        ObjectId fileId=gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType());
        ticket.setFileId(fileId.toHexString());
        ticketRepository.save(ticket);
    }
    public Optional<Ticket> getTicketById(String id)
    {
        return ticketRepository.findById(id);
    }


    @Transactional
    public Ticket deleteTicket(String username, String id)
    {
        User user=userService.findByUsername(username);
        Optional<Ticket> ticket=ticketRepository.findById(id);
        if(ticket.isPresent() && user.getMyTickets().contains(ticket.get()))
        {
            Query query=new Query();
            query.addCriteria(Criteria.where("_id").is(ticket.get().getFileId()));
            gridFsTemplate.delete(query);
            user.getMyTickets().remove(ticket.get());
            ticketRepository.deleteById(id);
            userService.save(user);
            return ticket.get();
        }
        return null;
    }

    @Transactional
    public void transferTicket(String from, String to, String id) throws IOException {
        User owner=userService.findByUsername(from);
        User buyer=userService.findByUsername(to);
        Optional<Ticket> ticket = ticketRepository.findById(id);
        if(ticket.isPresent() && owner.getMyTickets().contains(ticket.get()))
        {
            owner.getMyTickets().remove(ticket.get());
            buyer.getPurchasedTickets().add(ticket.get());
            ticket.get().setStatus(false);
            ticketRepository.save(ticket.get());
            userService.save(buyer);
            userService.save(owner);
            String paymentInfo=paymentService.fetchPaymentInfo(buyer.getUsername());
            emailService.sendEmail(emailAddress, "Payment Status and Ticket Info", buyer.getEmail() , ticket.get().getFileId(), "Payment of " + ticket.get().getPrice() + " successful.\n\nYour payment info: \n" + paymentInfo + "\n\nYour ticket id is " + ticket.get().getTicketId() + ".\n\nYour ticket details are:\n" + "Ticket id=" + ticket.get().getTicketId() + "\nEvent Name=" + ticket.get().getEventName() + "\nEvent Type=" + ticket.get().getEventType() + "\nDate=" + ticket.get().getDate() + "\nLocation=" + ticket.get().getLocation() + "\nStand=" + ticket.get().getStand());
        }
    }
}
