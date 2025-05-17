package com.zicket.zicket.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("payments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    private ObjectId paymentId;
    @NonNull
    private String from;
    @NonNull
    private String to;
    @NonNull
    private String cardNo;
    @NonNull
    private String cvv;
}
