package com.microservices.payment_service.repository;

import com.microservices.payment_service.model.PayLogic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PayLogic,Long> {

    @Query("SELECT p FROM PayLogic p WHERE p.id = (SELECT MAX(p2.id) FROM PayLogic p2)")
    PayLogic findMaxIdPayLogic();
}
