package com.JavaCSA.user_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private String username;
    private String email;
    private String role;
}
