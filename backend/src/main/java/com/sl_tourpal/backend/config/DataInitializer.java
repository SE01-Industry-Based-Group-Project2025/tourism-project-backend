package com.sl_tourpal.backend.config;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sl_tourpal.backend.domain.Privilege;
import com.sl_tourpal.backend.domain.Role;
import com.sl_tourpal.backend.domain.User;
import com.sl_tourpal.backend.repository.PrivilegeRepository;
import com.sl_tourpal.backend.repository.RoleRepository;
import com.sl_tourpal.backend.repository.UserRepository;

@Component
public class DataInitializer {

    private final PrivilegeRepository privilegeRepo;
    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    // Default admin credentials
    private static final String ADMIN_EMAIL = "admin@sl-tourpal.com";
    private static final String ADMIN_PASSWORD = "Admin123!";

    public DataInitializer(PrivilegeRepository privilegeRepo,
                           RoleRepository roleRepo,
                           UserRepository userRepo,
                           PasswordEncoder passwordEncoder) {
        this.privilegeRepo = privilegeRepo;
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    @Transactional
    public void seed() {
        // 1) Privileges
        List<String> privilegeNames = List.of(
            "CREATE_DESTINATION", "VIEW_DESTINATION",
            "CREATE_ACTIVITY",    "VIEW_ACTIVITY",
            "BOOK_TOUR",          "VIEW_TOUR",
            "MANAGE_USERS", "CREATE_TOUR"
        );
        List<Privilege> privileges = privilegeNames.stream()
            .map(name -> privilegeRepo.findByName(name)
                .orElseGet(() -> privilegeRepo.save(new Privilege(null, name))))
            .collect(Collectors.toList());

        // 2) ROLE_USER
        Role userRole = roleRepo.findByName("ROLE_USER")
            .orElseGet(() -> roleRepo.save(new Role(null, "ROLE_USER", Set.of())));
        Set<Privilege> userPrivs = privileges.stream()
            .filter(p -> Set.of("VIEW_DESTINATION","VIEW_ACTIVITY","VIEW_TOUR","BOOK_TOUR")
                             .contains(p.getName()))
            .collect(Collectors.toSet());
        userRole.setPrivileges(userPrivs);
        roleRepo.save(userRole);

        // 3) ROLE_ADMIN
        Role adminRole = roleRepo.findByName("ROLE_ADMIN")
            .orElseGet(() -> roleRepo.save(new Role(null, "ROLE_ADMIN", Set.of())));
        Set<Privilege> adminPrivs = privileges.stream()
            .filter(p -> Set.of(
                "CREATE_DESTINATION","VIEW_DESTINATION",
                "CREATE_ACTIVITY",   "VIEW_ACTIVITY",
                "BOOK_TOUR",         "VIEW_TOUR",
                "MANAGE_USERS", "CREATE_TOUR"
              ).contains(p.getName()))
            .collect(Collectors.toSet());
        adminRole.setPrivileges(adminPrivs);
        roleRepo.save(adminRole);

        // 4) Default Admin User
        if (userRepo.findByEmail(ADMIN_EMAIL).isEmpty()) {
            User admin = new User();
            admin.setEmail(ADMIN_EMAIL);
            admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
            admin.setFirstName("Super");
            admin.setLastName("Admin");
            admin.setPhone("+94000000000");
            admin.setEnabled(true);
            admin.setRoles(Set.of(adminRole));
            userRepo.save(admin);
            System.out.println("=== Default ADMIN user created: " + ADMIN_EMAIL + " / " + ADMIN_PASSWORD);
        }
    }
}
