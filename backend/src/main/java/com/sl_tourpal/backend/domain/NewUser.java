package com.sl_tourpal.backend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Newusers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String contact;

    private String email;

    private String nic;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        GUIDE,
        DRIVER
    }
}
