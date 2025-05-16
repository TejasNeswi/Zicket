package com.zicket.zicket.entity;

import com.zicket.zicket.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("tickets")
public class Ticket {

    @Id
    private ObjectId ticketId;
    private String eventName;
    private String eventType;
    private String date;
    private String location;
    private String stand;
    private LocalDateTime time;
    private String price;
    private boolean status;

}
