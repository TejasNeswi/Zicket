package com.zicket.zicket.repository;

import com.zicket.zicket.entity.Payment;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentRepository extends MongoRepository<Payment, ObjectId> {
}
