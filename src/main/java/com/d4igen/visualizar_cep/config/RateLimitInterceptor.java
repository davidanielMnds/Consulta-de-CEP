package com.d4igen.visualizar_cep.config;

import com.d4igen.visualizar_cep.service.RateLimitService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitService rateLimitService;

    public RateLimitInterceptor(RateLimitService rateLimitService) {
        this.rateLimitService=rateLimitService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws IOException {

        String ip = request.getRemoteAddr();
        Bucket balde = rateLimitService.getBalde(ip);
        ConsumptionProbe probe = balde.tryConsumeAndReturnRemaining(1);

        if(probe.isConsumed()) {
            return true;
        }
        Long segundos = Math.ceilDiv(probe.getNanosToWaitForRefill(), 1_000_000_000L);

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(segundos));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        String corpo = "{\"mensagem\":\"Muitas requisições, tente novamente em " + segundos + " segundos\"}";
        response.getWriter().write(corpo);

        return false;
    }
}
