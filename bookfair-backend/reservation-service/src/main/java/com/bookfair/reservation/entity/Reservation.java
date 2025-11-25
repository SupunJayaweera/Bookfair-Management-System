package com.bookfair.reservation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @ElementCollection
    @CollectionTable(name = "reservation_stalls", joinColumns = @JoinColumn(name = "reservation_id"))
    @Column(name = "stall_id")
    private Set<Long> stallIds = new HashSet<>();

    @Column(nullable = false, unique = true)
    private String qrCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.CONFIRMED;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime reservedAt;

    @ElementCollection
    @CollectionTable(name = "reservation_genres", joinColumns = @JoinColumn(name = "reservation_id"))
    @Column(name = "genre")
    private Set<String> literaryGenres = new HashSet<>();
}
