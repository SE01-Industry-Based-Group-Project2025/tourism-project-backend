package com.sl_tourpal.backend.config;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sl_tourpal.backend.domain.Privilege;
import com.sl_tourpal.backend.domain.Role;
import com.sl_tourpal.backend.repository.PrivilegeRepository;
import com.sl_tourpal.backend.repository.RoleRepository;

import jakarta.annotation.PostConstruct;

@Component
public class DataInitializer {

    private final PrivilegeRepository privilegeRepo;
    private final RoleRepository roleRepo;

    public DataInitializer(PrivilegeRepository privilegeRepo, RoleRepository roleRepo) {
        this.privilegeRepo = privilegeRepo;
        this.roleRepo = roleRepo;
    }

    @PostConstruct
    @Transactional
    public void seed() {
        // 1. Ensure privileges exist
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

        // 2. Ensure ROLE_USER exists with basic view/book privileges
        Role userRole = roleRepo.findByName("ROLE_USER")
            .orElseGet(() -> {
                Role r = new Role();
                r.setName("ROLE_USER");
                return r;
            });
        Set<Privilege> userPrivs = privileges.stream()
            .filter(p -> Set.of("VIEW_DESTINATION","VIEW_ACTIVITY","VIEW_TOUR","BOOK_TOUR")
                             .contains(p.getName()))
            .collect(Collectors.toSet());
        userRole.setPrivileges(userPrivs);
        roleRepo.save(userRole);

        // 3. Ensure ROLE_ADMIN exists with all privileges + manage_users
        Role adminRole = roleRepo.findByName("ROLE_ADMIN")
            .orElseGet(() -> {
                Role r = new Role();
                r.setName("ROLE_ADMIN");
                return r;
            });
        adminRole.setPrivileges(privileges.stream()
            .filter(p -> Set.of(
                "CREATE_DESTINATION","VIEW_DESTINATION",
                "CREATE_ACTIVITY",   "VIEW_ACTIVITY",
                "BOOK_TOUR",         "VIEW_TOUR",
                "MANAGE_USERS", "CREATE_TOUR"
              ).contains(p.getName()))
            .collect(Collectors.toSet()));
        roleRepo.save(adminRole);
    }
}
