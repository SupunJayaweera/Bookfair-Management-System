package com.bookfair.stall.controller;

import com.bookfair.stall.dto.StallRequest;
import com.bookfair.stall.dto.StallResponse;
import com.bookfair.stall.entity.StallSize;
import com.bookfair.stall.service.StallService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stalls")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174" })
public class StallController {

    private final StallService stallService;

    @GetMapping
    public ResponseEntity<List<StallResponse>> getAllStalls() {
        List<StallResponse> stalls = stallService.getAllStalls();
        return ResponseEntity.ok(stalls);
    }

    @GetMapping("/available")
    public ResponseEntity<List<StallResponse>> getAvailableStalls() {
        List<StallResponse> stalls = stallService.getAvailableStalls();
        return ResponseEntity.ok(stalls);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StallResponse> getStallById(@PathVariable Long id) {
        try {
            StallResponse stall = stallService.getStallById(id);
            return ResponseEntity.ok(stall);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/size/{size}")
    public ResponseEntity<List<StallResponse>> getStallsBySize(@PathVariable StallSize size) {
        List<StallResponse> stalls = stallService.getStallsBySize(size);
        return ResponseEntity.ok(stalls);
    }

    @PutMapping("/{id}/availability")
    public ResponseEntity<Void> updateAvailability(
            @PathVariable Long id,
            @RequestParam Boolean available) {
        try {
            stallService.updateStallAvailability(id, available);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<StallResponse> createStall(@Valid @RequestBody StallRequest request) {
        try {
            StallResponse stall = stallService.createStall(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(stall);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<StallResponse> updateStall(
            @PathVariable Long id,
            @Valid @RequestBody StallRequest request) {
        try {
            StallResponse stall = stallService.updateStall(id, request);
            return ResponseEntity.ok(stall);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStall(@PathVariable Long id) {
        try {
            stallService.deleteStall(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/initialize")
    public ResponseEntity<String> initializeStalls() {
        stallService.initializeStalls();
        return ResponseEntity.ok("Stalls initialized successfully");
    }
}
