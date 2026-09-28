package br.edu.utfpr.apiWebStore.shared.exception;

import br.edu.utfpr.apiWebStore.shared.exception.response.ErrorResponse;
import br.edu.utfpr.apiWebStore.shared.exception.response.FieldErrorResponse;
import br.edu.utfpr.apiWebStore.shared.exception.web.CorrelationIdFilter;
import br.edu.utfpr.apiWebStore.shared.exception.web.RequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            ValidationException exception,
            HttpServletRequest request
    ) {
        return createResponse(
                HttpStatus.BAD_REQUEST,
                exception.getCode(),
                exception.getMessage(),
                request,
                List.of(),
                null
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {
        return createResponse(
                HttpStatus.NOT_FOUND,
                exception.getCode(),
                exception.getMessage(),
                request,
                List.of(),
                null
        );
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            ConflictException exception,
            HttpServletRequest request
    ) {
        return createResponse(
                HttpStatus.CONFLICT,
                exception.getCode(),
                exception.getMessage(),
                request,
                List.of(),
                null
        );
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleExternalService(
            ExternalServiceException exception,
            HttpServletRequest request
    ) {
        LOG.warn(
                "Falha na integração externa. code={} path={} correlationId={}",
                exception.getCode(),
                request.getRequestURI(),
                getCorrelationId(request)
        );
        return createResponse(
                HttpStatus.BAD_GATEWAY,
                exception.getCode(),
                exception.getMessage(),
                request,
                List.of(),
                null
        );
    }

    @ExceptionHandler(UpstreamApiException.class)
    public ResponseEntity<ErrorResponse> handleUpstreamApiError(
            UpstreamApiException exception,
            HttpServletRequest request
    ) {
        return createResponse(
                exception.getStatus(),
                exception.getCode(),
                exception.getMessage(),
                request,
                exception.getViolations(),
                exception.getRetryAfterSeconds()
        );
    }

    @ExceptionHandler(UpstreamServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleUpstreamUnavailable(
            UpstreamServiceUnavailableException exception,
            HttpServletRequest request
    ) {
        LOG.warn(
                "API de produtos indisponível. path={} correlationId={}",
                request.getRequestURI(),
                getCorrelationId(request)
        );
        return createResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                "PRODUCT_API_UNAVAILABLE",
                exception.getMessage(),
                request,
                List.of(),
                null
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleRequestBodyValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<FieldErrorResponse> violations = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new FieldErrorResponse(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();

        return createResponse(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "Existem campos inválidos na requisição.",
                request,
                violations,
                null
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        List<FieldErrorResponse> violations = exception.getConstraintViolations()
                .stream()
                .map(violation -> new FieldErrorResponse(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()
                ))
                .toList();

        return createResponse(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "Existem parâmetros inválidos na requisição.",
                request,
                violations,
                null
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestBody(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        return createResponse(
                HttpStatus.BAD_REQUEST,
                "MALFORMED_REQUEST_BODY",
                "O corpo da requisição está inválido ou possui valores incompatíveis.",
                request,
                List.of(),
                null
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleInvalidParameter(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        FieldErrorResponse violation = new FieldErrorResponse(
                exception.getName(),
                "O valor informado possui formato inválido."
        );
        return createResponse(
                HttpStatus.BAD_REQUEST,
                "INVALID_PARAMETER",
                "Um parâmetro da requisição possui valor inválido.",
                request,
                List.of(violation),
                null
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException exception,
            HttpServletRequest request
    ) {
        FieldErrorResponse violation = new FieldErrorResponse(
                exception.getParameterName(),
                "O parâmetro é obrigatório."
        );
        return createResponse(
                HttpStatus.BAD_REQUEST,
                "MISSING_PARAMETER",
                "Um parâmetro obrigatório não foi informado.",
                request,
                List.of(violation),
                null
        );
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestPart(
            MissingServletRequestPartException exception,
            HttpServletRequest request
    ) {
        FieldErrorResponse violation = new FieldErrorResponse(
                exception.getRequestPartName(),
                "A parte é obrigatória."
        );
        return createResponse(
                HttpStatus.BAD_REQUEST,
                "MISSING_REQUEST_PART",
                "Uma parte obrigatória da requisição não foi informada.",
                request,
                List.of(violation),
                null
        );
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaximumUploadSize(
            MaxUploadSizeExceededException exception,
            HttpServletRequest request
    ) {
        return createResponse(
                HttpStatus.PAYLOAD_TOO_LARGE,
                "PRODUCT_IMAGE_TOO_LARGE",
                "A imagem do produto excede o tamanho máximo permitido.",
                request,
                List.of(),
                null
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleRouteNotFound(
            NoResourceFoundException exception,
            HttpServletRequest request
    ) {
        return createResponse(
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND",
                "O recurso solicitado não foi encontrado.",
                request,
                List.of(),
                null
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException exception,
            HttpServletRequest request
    ) {
        return createResponse(
                HttpStatus.METHOD_NOT_ALLOWED,
                "METHOD_NOT_ALLOWED",
                "O método HTTP não é permitido para este recurso.",
                request,
                List.of(),
                null
        );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException exception,
            HttpServletRequest request
    ) {
        return createResponse(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "UNSUPPORTED_MEDIA_TYPE",
                "O tipo de conteúdo enviado não é suportado.",
                request,
                List.of(),
                null
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedError(
            Exception exception,
            HttpServletRequest request
    ) {
        String correlationId = getCorrelationId(request);
        LOG.error("Erro interno não tratado. correlationId={}", correlationId, exception);

        request.setAttribute(RequestAttributes.ERROR_CODE, "INTERNAL_SERVER_ERROR");
        ErrorResponse error = new ErrorResponse(
                Instant.now(),
                "INTERNAL_SERVER_ERROR",
                "Ocorreu um erro interno. Informe o código de correlação ao suporte.",
                request.getRequestURI(),
                correlationId,
                List.of()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header(CorrelationIdFilter.CORRELATION_ID_HEADER, correlationId)
                .body(error);
    }

    private ResponseEntity<ErrorResponse> createResponse(
            HttpStatusCode status,
            String code,
            String message,
            HttpServletRequest request,
            List<FieldErrorResponse> violations,
            Integer retryAfterSeconds
    ) {
        String correlationId = getCorrelationId(request);
        request.setAttribute(RequestAttributes.ERROR_CODE, code);

        ErrorResponse error = new ErrorResponse(
                Instant.now(),
                code,
                message,
                request.getRequestURI(),
                correlationId,
                violations,
                retryAfterSeconds
        );

        ResponseEntity.BodyBuilder response = ResponseEntity
                .status(status)
                .header(CorrelationIdFilter.CORRELATION_ID_HEADER, correlationId);
        if (retryAfterSeconds != null) {
            response.header(HttpHeaders.RETRY_AFTER, String.valueOf(retryAfterSeconds));
        }
        return response.body(error);
    }

    private String getCorrelationId(HttpServletRequest request) {
        Object correlationId = request.getAttribute(RequestAttributes.CORRELATION_ID);
        if (correlationId != null) {
            return correlationId.toString();
        }

        String providedCorrelationId = request.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER);
        if (providedCorrelationId != null
                && !providedCorrelationId.isBlank()
                && providedCorrelationId.length() <= 100) {
            return providedCorrelationId.trim();
        }

        String generatedCorrelationId = UUID.randomUUID().toString();
        request.setAttribute(RequestAttributes.CORRELATION_ID, generatedCorrelationId);
        return generatedCorrelationId;
    }
}
