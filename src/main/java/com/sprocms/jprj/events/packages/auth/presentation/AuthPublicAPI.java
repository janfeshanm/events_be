package com.sprocms.jprj.events.packages.auth.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sprocms.jprj.events.packages.auth.application.UseAuth;
import com.sprocms.jprj.events.packages.auth.domain.SignupResquest;
import com.sprocms.jprj.events.packages.auth.domain.User;

@RestController
public class AuthPublicAPI {
    @Autowired
    private UseAuth useAuth;

    @GetMapping("/p/auth/op/token")
    ResponseEntity<String> opToken(Authentication authentication) {
        if (authentication != null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(useAuth.getToken(authentication.getName()));
        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("");
    }

    @PostMapping("/p/auth/op/signup")
    public ResponseEntity<String> signup(@RequestBody SignupResquest account) {
        useAuth.addUser(new User(account.getUsername(),
                account.getPassword(), null));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("User Added");
    }
}
