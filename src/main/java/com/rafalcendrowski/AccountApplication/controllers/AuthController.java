package com.rafalcendrowski.AccountApplication.controllers;

import com.rafalcendrowski.AccountApplication.user.User;
import com.rafalcendrowski.AccountApplication.logging.LoggerConfig;
import com.rafalcendrowski.AccountApplication.user.UserDto;
import com.rafalcendrowski.AccountApplication.user.UserRegisterDto;
import com.rafalcendrowski.AccountApplication.user.UserService;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.*;


@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Password has been breached")
class BreachedPasswordException extends RuntimeException {
    public BreachedPasswordException() { super(); }
}


@RestController
@RequestMapping("/api/auth")
@Log4j2
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Logger secLogger;

    private final Set<String> breachedPasswords = Set.of("breachedPassword");

    @PostMapping("/signup")
    public UserDto addAccount(@Valid @RequestBody UserRegisterDto userBody, @AuthenticationPrincipal User authUser) {
        if (userService.hasUser(userBody.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists");
        } else if (isBreached(userBody.getPassword())) {
            throw new BreachedPasswordException();
        }
        User user = new User(userBody.getEmail(), passwordEncoder.encode(userBody.getPassword()),
                userBody.getName(), userBody.getLastname());
        userService.registerUser(user);
        String subject = authUser == null ? "Anonymous" : authUser.getName();
        secLogger.info(LoggerConfig.getEventLogMap(subject, user.getUsername(), "CREATE_USER", "api/auth/signup"));
        return UserDto.of(user);
    }


    @PostMapping("/changepass")
    public Map<String, String> changePassword(@RequestBody @Valid Password newPassword, @AuthenticationPrincipal User user) {
        if(isBreached(newPassword.getPassword())) {
            throw new BreachedPasswordException();
        } else if(passwordEncoder.matches(newPassword.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords must be different");
        } else {
            user.setPassword(passwordEncoder.encode(newPassword.getPassword()));
            userService.updateUser(user);
            secLogger.info(LoggerConfig.getEventLogMap(user.getName(), user.getUsername(), "CHANGE_PASSWORD", "api/auth/changepass"));
            return Map.of("email", user.getUsername(), "status", "Password has been updated successfully");
        }
    }

    public boolean isBreached(String password) {
        return breachedPasswords.contains(password);
    }

}

@Data
class Password {
    @NotEmpty
    @Size(min=12)
    String password;
}