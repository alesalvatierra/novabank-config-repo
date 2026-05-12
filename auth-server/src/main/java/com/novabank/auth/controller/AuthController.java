package com.novabank.auth.controller;

import com.novabank.auth.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtProvider jwtProvider;

    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(@RequestParam String username, @RequestParam String password) {

        if ("admin".equals(username) && "admin".equals(password)) {
            String token = jwtProvider.createToken(username);
            return Mono.just(ResponseEntity.ok(token));
        }

        return Mono.just(ResponseEntity.status(401).body("Credenciales incorrectas"));
    }
}
