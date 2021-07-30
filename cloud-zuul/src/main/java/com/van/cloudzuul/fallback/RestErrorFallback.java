package com.van.cloudzuul.fallback;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.exception.HystrixTimeoutException;
import com.van.internalcommon.constant.ResponseStatusEnum;
import com.van.internalcommon.dto.ResponseResult;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class RestErrorFallback implements FallbackProvider {
    @Override
    public String getRoute() {
        // 匹配所以服务
        return "*";
    }

    @Override
    public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
        ResponseResult<?> error = ResponseResult.error(ResponseStatusEnum.INTERNAL_SERVER_EXCEPTION.getCode());

        if (cause instanceof HystrixTimeoutException) {
            error.setMessage("后端服务访问异常");
            return new GatewayClientResponse(HttpStatus.INTERNAL_SERVER_ERROR, error);
        } else {
            error.setMessage("服务内部异常");
            return new GatewayClientResponse(HttpStatus.BAD_REQUEST, error);
        }
    }

    class GatewayClientResponse implements ClientHttpResponse {

        private HttpStatus httpStatus;
        private Object message;

        GatewayClientResponse(HttpStatus httpStatus, Object message) {
            this.httpStatus = httpStatus;
            this.message = message;
        }

        @Override
        public HttpStatus getStatusCode() throws IOException {
            return httpStatus;
        }

        @Override
        public int getRawStatusCode() throws IOException {
            return httpStatus.value();
        }

        @Override
        public String getStatusText() throws IOException {
            return null;
        }

        @Override
        public void close() {

        }

        @Override
        public InputStream getBody() throws IOException {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = objectMapper.writeValueAsString(message);
            return new ByteArrayInputStream(jsonBody.getBytes());
        }

        @Override
        public HttpHeaders getHeaders() {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return headers;
        }
    }
}
