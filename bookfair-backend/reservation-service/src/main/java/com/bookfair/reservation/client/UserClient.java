package com.bookfair.reservation.client;

import com.bookfair.reservation.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/users/{id}")
    UserResponse getUserById(@PathVariable Long id);

    @GetMapping("/api/users/profile")
    UserResponse getUserByEmail(@RequestHeader("X-User-Id") String email);
}
