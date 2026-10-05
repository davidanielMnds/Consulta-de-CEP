package com.d4igen.visualizar_cep.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {
    private final Map<String, Bucket> baldes = new ConcurrentHashMap<>();

    public Bucket getBalde(String ip) {
        return baldes.computeIfAbsent(ip, chave -> criarBalde());
    }

    public Bucket criarBalde() {
        Bandwidth limite = Bandwidth.builder()
                .capacity(10)
                .refillIntervally(10, Duration.ofMinutes(1))
                .build();
        return Bucket.builder().addLimit(limite).build();
    }
}
