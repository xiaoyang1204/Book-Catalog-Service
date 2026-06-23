package com.example.bookcatalog.controller;

import com.example.bookcatalog.domain.exception.BookNotFoundException;
import com.example.bookcatalog.domain.exception.DuplicateIsbnException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler 单元测试")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("BookNotFoundException应返回404")
    void shouldHandleBookNotFoundException() {
        BookNotFoundException ex = new BookNotFoundException(1L);

        ResponseEntity<?> response = handler.handleBookNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("DuplicateIsbnException应返回409")
    void shouldHandleDuplicateIsbnException() {
        DuplicateIsbnException ex = new DuplicateIsbnException("978-0134685991");

        ResponseEntity<?> response = handler.handleDuplicateIsbn(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("MethodArgumentNotValidException应返回400")
    void shouldHandleValidationException() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "testObject");
        bindingResult.addError(new FieldError("testObject", "title", "标题不能为空"));

        Method method = GlobalExceptionHandler.class.getDeclaredMethod("handleValidationErrors", MethodArgumentNotValidException.class);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
                new org.springframework.core.MethodParameter(method, -1), bindingResult);

        ResponseEntity<?> response = handler.handleValidationErrors(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("通用异常应返回500")
    void shouldHandleGenericException() {
        Exception ex = new RuntimeException("Unexpected error");

        ResponseEntity<?> response = handler.handleGenericException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
