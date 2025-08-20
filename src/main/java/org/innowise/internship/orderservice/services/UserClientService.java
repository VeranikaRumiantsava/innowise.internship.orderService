package org.innowise.internship.orderservice.services;

import lombok.RequiredArgsConstructor;
import org.innowise.internship.orderservice.dto.user.UserResponseDTO;
import org.innowise.internship.orderservice.jwt.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserClientService {

    private final WebClient webClient;
    private final JwtUtil jwtUtil;

    public UserResponseDTO getUserById(Long userId) {
        return webClient.get()
                .uri("/user/{id}", userId)
                .header("Authorization", "Bearer " + jwtUtil.generateAccessToken(userId))
                .retrieve()
                .bodyToMono(UserResponseDTO.class)
                .block();
    }
}
