package br.com.stoom.controller;

import br.com.stoom.exception.AddressNotFoundException;
import br.com.stoom.exception.GoogleApiInvalidAddressInformationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {AddressNotFoundException.class, EmptyResultDataAccessException.class})
    protected ResponseEntity<Object> handleNotFound(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(
                ex,
                "Address not found",
                new HttpHeaders(),
                HttpStatus.NOT_FOUND,
                request);
    }

    @ExceptionHandler(value = {GoogleApiInvalidAddressInformationException.class})
    protected ResponseEntity<Object> handleInvalidAddressSentToGoogle(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(
                ex,
                "Invalid Address data!",
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request);
    }

    @ExceptionHandler(value = {InvalidDataAccessApiUsageException.class})
    protected ResponseEntity<Object> invalidQueryStringField(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(
                ex,
                "Invalid field! The possible values are [streetName, number, complement, neighbourhood, city, state, country, latitude, longitude, zipcode].",
                new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request);
    }
}
