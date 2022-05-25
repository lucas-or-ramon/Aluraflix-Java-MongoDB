package br.com.devcanoa.aluraflix.exception.video;

import br.com.devcanoa.aluraflix.exception.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class VideoExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoExceptionHandler.class);

    @ExceptionHandler(value = {VideoNotFoundException.class, CategoryNotFoundException.class})
    public ResponseEntity<ErrorResponse> handlerException(VideoNotFoundException e) {
        LOGGER.error(e.getMessage());
        var errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {VideoAlreadyExistsException.class})
    public ResponseEntity<ErrorResponse> handlerException(VideoAlreadyExistsException e) {
        LOGGER.error(e.getMessage());
        var errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ErrorResponse> handlerException(Exception e) {
        LOGGER.error(e.getMessage());
        var errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
