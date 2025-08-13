package com.sl_tourpal.backend.controller;

import com.sl_tourpal.backend.domain.NewUser;
import com.sl_tourpal.backend.dto.NewUserDTO;
import com.sl_tourpal.backend.service.NewUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/newusers")
@CrossOrigin(origins = "http://localhost:5173")
public class NewUserController {

    @Autowired
    private NewUserService newUserService;

    @PostMapping("/addNewUser")
    public ResponseEntity<NewUserDTO> add(@Valid @RequestBody NewUserDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(newUserService.addUser(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/allNewUsers")
    public ResponseEntity<List<NewUserDTO>> getAll() {
        return ResponseEntity.ok(newUserService.getAllUsers());
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<NewUserDTO> getUserById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(newUserService.getUserById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<NewUserDTO>> getByRole(@PathVariable NewUser.Role role) {
        return ResponseEntity.ok(newUserService.getUsersByRole(role));
    }

    @GetMapping("/guides")
    public ResponseEntity<List<NewUserDTO>> getAllGuides() {
        return ResponseEntity.ok(newUserService.getAllGuides());
    }

    @GetMapping("/drivers")
    public ResponseEntity<List<NewUserDTO>> getAllDrivers() {
        return ResponseEntity.ok(newUserService.getAllDrivers());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<NewUserDTO> update(@PathVariable Long id, @Valid @RequestBody NewUserDTO dto) {
        try {
            return ResponseEntity.ok(newUserService.updateUser(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            newUserService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/checkEmail/{email}")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        return ResponseEntity.ok(newUserService.existsByEmail(email));
    }
}
