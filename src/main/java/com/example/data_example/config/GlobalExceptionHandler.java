package com.example.data_example.config;

import com.example.data_example.dto.ApiErrorDTO;
import com.example.data_example.exceptions.UsernameExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameExistsException.class)
    public ResponseEntity<ApiErrorDTO> handleDataIntegrityViolation(UsernameExistsException ex) {
        ApiErrorDTO errorDTO = new ApiErrorDTO(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> handleDataIntegrityViolation(MethodArgumentNotValidException ex) {
        System.out.println(Arrays.toString(ex.getDetailMessageArguments()));
        Map<String, List<String>> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldname = error instanceof FieldError fieldError
                    ? fieldError.getField()
                    : error.getObjectName();
            errors.computeIfAbsent(fieldname, k -> new ArrayList<>())
                    .add(error.getDefaultMessage());
        });
        ApiErrorDTO errorDTO = new ApiErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Some of the form values were invalid",
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

}
