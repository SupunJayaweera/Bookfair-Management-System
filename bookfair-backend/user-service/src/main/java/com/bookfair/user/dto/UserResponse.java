package com.bookfair.user.dto;

import com.bookfair.user.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String businessName;
    private String contactPerson;
    private String phoneNumber;
    private String address;
    private UserRole role;
    private Boolean active;
    private LocalDateTime createdAt;
}
