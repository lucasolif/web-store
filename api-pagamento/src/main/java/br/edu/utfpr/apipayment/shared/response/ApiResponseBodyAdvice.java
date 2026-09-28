package br.edu.utfpr.apipayment.shared.response;

import br.edu.utfpr.apipayment.shared.exception.response.ErrorResponse;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;


@RestControllerAdvice(basePackages = "br.edu.utfpr.apipayment")
public class ApiResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(
            MethodParameter returnType,
            Class<? extends HttpMessageConverter<?>> converterType
    ) {
        Class<?> parameterType = returnType.getParameterType();
        if (ResponseEntity.class.isAssignableFrom(parameterType)) {
            return true;
        }
        return !Void.TYPE.equals(parameterType) && !Void.class.equals(parameterType);
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        if (body == null) {
            return null;
        }

        if (body instanceof ApiResponse<?>
                || body instanceof PageResponse<?>
                || body instanceof ErrorResponse
                || body instanceof Resource
                || body instanceof byte[]) {
            return body;
        }

        return ApiResponse.of(body);
    }
}
