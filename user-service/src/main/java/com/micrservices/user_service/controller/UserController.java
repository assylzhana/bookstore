package com.micrservices.user_service.controller;

import com.micrservices.user_service.dto.UserDto;
import com.micrservices.user_service.dto.UserRequest;
import com.micrservices.user_service.model.User;
import com.micrservices.user_service.repository.UserRepository;
import com.micrservices.user_service.service.UserImplementation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/us")
@RequiredArgsConstructor
public class UserController {

    private final UserImplementation userService;
    private final UserRepository userRepository;

    @GetMapping("/{email}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setPassword(user.getPassword());
        userDto.setRole(user.getRole().name());
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/")
    public ResponseEntity<String> deleteUser(@RequestBody @Email @NotEmpty String email) {
        try {
            userService.deleteUser(email);
            return ResponseEntity.ok("User deleted successfully.");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }
    @DeleteMapping("/account")
    public ResponseEntity<String> deleteAccount() {
        try {
            userService.deleteAccount();
            return ResponseEntity.ok("Account deleted successfully.");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody UserRequest userRequest) {
        try {
            userService.changePassword(userRequest.getOldPassword(), userRequest.getNewPassword());
            return ResponseEntity.ok("Password changed successfully.");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        UserDto userDto = new UserDto(user.getId(), user.getEmail(), user.getFullName(), user.getRole().name());
        return ResponseEntity.ok(userDto);
    }
}
