package br.edu.utfpr.apicep.shared.exception;

import br.edu.utfpr.apicep.shared.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CepInvalidoException.class)
    public ResponseEntity<ApiErrorResponse> handleCepInvalido(
            CepInvalidoException exception,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request, null);
    }

    @ExceptionHandler(CepNaoEncontradoException.class)
    public ResponseEntity<ApiErrorResponse> handleCepNaoEncontrado(
            CepNaoEncontradoException exception,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request, null);
    }

    @ExceptionHandler(ViaCepIndisponivelException.class)
    public ResponseEntity<ApiErrorResponse> handleViaCepIndisponivel(
            ViaCepIndisponivelException exception,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.BAD_GATEWAY, exception.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        Map<String, String> violations = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                violations.putIfAbsent(error.getField(), error.getDefaultMessage())
        );

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Existem campos inválidos na requisição.",
                request,
                violations
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidBody(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "O corpo da requisição está ausente ou possui JSON inválido.",
                request,
                null
        );
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            Map<String, String> violations
    ) {
        var response = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                violations
        );

        return ResponseEntity.status(status).body(response);
    }
}
