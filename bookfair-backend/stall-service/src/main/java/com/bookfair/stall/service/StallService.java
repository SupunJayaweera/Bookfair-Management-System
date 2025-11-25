package com.bookfair.stall.service;

import com.bookfair.stall.dto.StallRequest;
import com.bookfair.stall.dto.StallResponse;
import com.bookfair.stall.entity.Stall;
import com.bookfair.stall.entity.StallSize;
import com.bookfair.stall.repository.StallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StallService {

    private final StallRepository stallRepository;

    @Transactional(readOnly = true)
    public List<StallResponse> getAllStalls() {
        return stallRepository.findAll().stream()
                .map(this::mapToStallResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StallResponse> getAvailableStalls() {
        return stallRepository.findByAvailable(true).stream()
                .map(this::mapToStallResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StallResponse getStallById(Long id) {
        Stall stall = stallRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stall not found"));
        return mapToStallResponse(stall);
    }

    @Transactional(readOnly = true)
    public List<StallResponse> getStallsBySize(StallSize size) {
        return stallRepository.findBySize(size).stream()
                .map(this::mapToStallResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateStallAvailability(Long stallId, Boolean available) {
        Stall stall = stallRepository.findById(stallId)
                .orElseThrow(() -> new RuntimeException("Stall not found"));
        stall.setAvailable(available);
        stallRepository.save(stall);
    }

    @Transactional
    public StallResponse createStall(StallRequest request) {
        // Check if stall name already exists
        if (stallRepository.findByStallName(request.getStallName()).isPresent()) {
            throw new RuntimeException("Stall with name '" + request.getStallName() + "' already exists");
        }

        Stall stall = new Stall();
        stall.setStallName(request.getStallName());
        stall.setSize(request.getSize());
        stall.setWidth(request.getWidth());
        stall.setLength(request.getLength());
        stall.setPricePerDay(request.getPricePerDay());
        stall.setPositionX(request.getPositionX());
        stall.setPositionY(request.getPositionY());
        stall.setAvailable(request.getAvailable() != null ? request.getAvailable() : true);
        stall.setDescription(request.getDescription());

        Stall savedStall = stallRepository.save(stall);
        return mapToStallResponse(savedStall);
    }

    @Transactional
    public StallResponse updateStall(Long id, StallRequest request) {
        Stall stall = stallRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stall not found"));

        // Check if stall name is being changed and if new name already exists
        if (!stall.getStallName().equals(request.getStallName())) {
            if (stallRepository.findByStallName(request.getStallName()).isPresent()) {
                throw new RuntimeException("Stall with name '" + request.getStallName() + "' already exists");
            }
        }

        stall.setStallName(request.getStallName());
        stall.setSize(request.getSize());
        stall.setWidth(request.getWidth());
        stall.setLength(request.getLength());
        stall.setPricePerDay(request.getPricePerDay());
        stall.setPositionX(request.getPositionX());
        stall.setPositionY(request.getPositionY());
        stall.setAvailable(request.getAvailable() != null ? request.getAvailable() : stall.getAvailable());
        stall.setDescription(request.getDescription());

        Stall updatedStall = stallRepository.save(stall);
        return mapToStallResponse(updatedStall);
    }

    @Transactional
    public void deleteStall(Long id) {
        Stall stall = stallRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stall not found"));
        
        // Check if stall is available before deleting
        if (!stall.getAvailable()) {
            throw new RuntimeException("Cannot delete stall. Stall is currently reserved.");
        }
        
        stallRepository.delete(stall);
    }

    @Transactional
    public void initializeStalls() {
        if (stallRepository.count() == 0) {
            // Create sample stalls in a grid layout
            String[] sizes = { "SMALL", "MEDIUM", "LARGE" };
            int stallCount = 0;

            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 10; col++) {
                    Stall stall = new Stall();
                    char letter = (char) ('A' + stallCount);
                    stall.setStallName(String.valueOf(letter));

                    StallSize size = StallSize.valueOf(sizes[stallCount % 3]);
                    stall.setSize(size);

                    switch (size) {
                        case SMALL:
                            stall.setWidth(3.0);
                            stall.setLength(3.0);
                            stall.setPricePerDay(5000.0);
                            break;
                        case MEDIUM:
                            stall.setWidth(5.0);
                            stall.setLength(4.0);
                            stall.setPricePerDay(8000.0);
                            break;
                        case LARGE:
                            stall.setWidth(7.0);
                            stall.setLength(6.0);
                            stall.setPricePerDay(12000.0);
                            break;
                    }

                    stall.setPositionX(col * 100);
                    stall.setPositionY(row * 100);
                    stall.setAvailable(true);
                    stall.setDescription("Stall " + letter + " - " + size.name());

                    stallRepository.save(stall);
                    stallCount++;

                    if (stallCount >= 26)
                        break;
                }
                if (stallCount >= 26)
                    break;
            }
        }
    }

    private StallResponse mapToStallResponse(Stall stall) {
        StallResponse response = new StallResponse();
        response.setId(stall.getId());
        response.setStallName(stall.getStallName());
        response.setSize(stall.getSize());
        response.setWidth(stall.getWidth());
        response.setLength(stall.getLength());
        response.setPricePerDay(stall.getPricePerDay());
        response.setPositionX(stall.getPositionX());
        response.setPositionY(stall.getPositionY());
        response.setAvailable(stall.getAvailable());
        response.setDescription(stall.getDescription());
        return response;
    }
}
