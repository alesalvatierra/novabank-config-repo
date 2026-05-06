package com.novabank.auth.controller;

import com.novabank.auth.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtProvider jwtProvider;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {

        if ("admin".equals(username) && "admin".equals(password)) {
            String token = jwtProvider.createToken(username);
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.status(401).body("Credenciales incorrectas");
    }
}
