package com.zicket.zicket.entity;

import com.zicket.zicket.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("tickets")
public class Ticket {

    @Id
    private String ticketId;
    private String eventName;
    private EventType eventType;
    private String date;
    private String location;
    private String stand;
    private String fileId;
    private LocalDateTime time;
    private String price;
    private boolean status;

}
