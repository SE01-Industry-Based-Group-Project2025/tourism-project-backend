package com.sl_tourpal.backend.dto;

import com.sl_tourpal.backend.domain.NewUser.Role;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewUserDTO {
    private Long id;
    private String name;
    private String contact;
    private String email;
    private String nic;
    private Role role;
}
