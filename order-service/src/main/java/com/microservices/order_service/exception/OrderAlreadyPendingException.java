package com.microservices.order_service.exception;

public class OrderAlreadyPendingException extends RuntimeException {
    public OrderAlreadyPendingException(String message) {
        super(message);
    }
}
