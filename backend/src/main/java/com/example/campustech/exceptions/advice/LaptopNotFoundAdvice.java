package com.example.campustech.exceptions.advice;

import com.example.campustech.exceptions.LaptopNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class LaptopNotFoundAdvice {
    @ResponseBody
    @ExceptionHandler(LaptopNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String laptopNotFoundHandler(LaptopNotFoundException ex) {
        return ex.getMessage();
    }
}