package com.sl_tourpal.backend.config;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sl_tourpal.backend.domain.Privilege;
import com.sl_tourpal.backend.domain.Role;
import com.sl_tourpal.backend.domain.User;
import com.sl_tourpal.backend.repository.PrivilegeRepository;
import com.sl_tourpal.backend.repository.RoleRepository;
import com.sl_tourpal.backend.repository.UserRepository;

import jakarta.annotation.PostConstruct;

@Component
public class DataInitializer {

    private final PrivilegeRepository privilegeRepo;
    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    // Default admin credentials
    private static final String ADMIN_EMAIL = "admin@sl-tourpal.com";
    private static final String ADMIN_PASSWORD = "Admin123!";
    
    // Default test user credentials
    private static final String TEST_USER_EMAIL = "sasirk1513@gmail.com";
    private static final String TEST_USER_PASSWORD = "Sasi@1234";

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
                .orElseGet(() -> {
                    Privilege privilege = new Privilege();
                    privilege.setName(name);
                    return privilegeRepo.save(privilege);
                }))
            .collect(Collectors.toList());

        // 2) ROLE_USER
        Role userRole = roleRepo.findByName("ROLE_USER")
            .orElseGet(() -> {
                Role role = new Role();
                role.setName("ROLE_USER");
                return roleRepo.save(role);
            });
        Set<Privilege> userPrivs = privileges.stream()
            .filter(p -> Set.of("VIEW_DESTINATION","VIEW_ACTIVITY","VIEW_TOUR","BOOK_TOUR")
                             .contains(p.getName()))
            .collect(Collectors.toSet());
        userRole.setPrivileges(userPrivs);
        roleRepo.save(userRole);

        // 3) ROLE_ADMIN
        Role adminRole = roleRepo.findByName("ROLE_ADMIN")
            .orElseGet(() -> {
                Role role = new Role();
                role.setName("ROLE_ADMIN");
                return roleRepo.save(role);
            });
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
        
        // 5) Default Test User
        if (userRepo.findByEmail(TEST_USER_EMAIL).isEmpty()) {
            User testUser = new User();
            testUser.setEmail(TEST_USER_EMAIL);
            testUser.setPassword(passwordEncoder.encode(TEST_USER_PASSWORD));
            testUser.setFirstName("Sasi");
            testUser.setLastName("RK");
            testUser.setPhone("+94123456789");
            testUser.setEnabled(true);
            testUser.setRoles(Set.of(userRole));
            userRepo.save(testUser);
            System.out.println("=== Default TEST USER created: " + TEST_USER_EMAIL + " / " + TEST_USER_PASSWORD);
        }
    }
}
