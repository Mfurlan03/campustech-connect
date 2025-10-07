package com.example.campustech.exceptions;

public class LaptopNotFoundException extends RuntimeException {
    public LaptopNotFoundException(Long id) {
        super("Could not find laptop with ID: " + id);
    }
}