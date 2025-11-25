package com.bookfair.reservation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "stall-service")
public interface StallClient {

    @PutMapping("/api/stalls/{id}/availability")
    void updateStallAvailability(@PathVariable Long id, @RequestParam Boolean available);
}
