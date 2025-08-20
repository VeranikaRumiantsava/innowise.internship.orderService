package org.innowise.internship.orderservice.exceptions;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(String message) {
        super(message);
    }
}
