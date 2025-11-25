package com.bookfair.stall.repository;

import com.bookfair.stall.entity.Stall;
import com.bookfair.stall.entity.StallSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StallRepository extends JpaRepository<Stall, Long> {
    Optional<Stall> findByStallName(String stallName);

    List<Stall> findByAvailable(Boolean available);

    List<Stall> findBySize(StallSize size);

    List<Stall> findBySizeAndAvailable(StallSize size, Boolean available);
}
