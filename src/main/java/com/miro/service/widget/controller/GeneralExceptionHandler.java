package com.miro.service.widget.controller;

import com.miro.service.widget.exception.WidgetNotFound;
import com.miro.service.widget.model.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GeneralExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(WidgetNotFound.class)
    public final ResponseEntity<ApiError> handleWidgetNotFoundException(WidgetNotFound ex, WebRequest request) {
        final ApiError error = new ApiError(LocalDateTime.now(), ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
