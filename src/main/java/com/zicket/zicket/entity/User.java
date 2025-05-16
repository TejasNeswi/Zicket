package com.zicket.zicket.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("users")
public class User {

    @Id
    private ObjectId objectId;
    @Indexed(unique = true)
    @NonNull
    private String username;
    @NonNull
    private String password;
    @DBRef
    private List<Ticket> tickets;

    private String traderType;
    private List<String> roles;
    @DBRef
    private List<Payment> payments;

}
