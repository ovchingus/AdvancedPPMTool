package com.github.ovchingus.ppmtool.web;

import com.github.ovchingus.ppmtool.domain.User;
import com.github.ovchingus.ppmtool.payload.JWTLoginSuccessResponse;
import com.github.ovchingus.ppmtool.payload.LoginRequest;
import com.github.ovchingus.ppmtool.security.JwtTokenProvider;
import com.github.ovchingus.ppmtool.services.MapValidationErrorService;
import com.github.ovchingus.ppmtool.services.UserService;
import com.github.ovchingus.ppmtool.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.github.ovchingus.ppmtool.security.SecurityConstants.TOKEN_PREFIX;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final MapValidationErrorService mapValidationErrorService;

    private final UserService userService;

    private final UserValidator userValidator;

    private final JwtTokenProvider jwtTokenProvider;

    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserController(MapValidationErrorService mapValidationErrorService,
                          UserService userService, UserValidator userValidator,
                          JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager) {
        this.mapValidationErrorService = mapValidationErrorService;
        this.userService = userService;
        this.userValidator = userValidator;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user, BindingResult result) {
        //TODO: validate passwords match
        userValidator.validate(user, result);
        return mapValidationErrorService.MapValidationService(result).orElseGet(() -> {
            User newUser = userService.saveUser(user);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        });
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult result) {

        return mapValidationErrorService.MapValidationService(result).orElseGet(() -> {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = TOKEN_PREFIX + jwtTokenProvider.generateToken(authentication);

            return ResponseEntity.ok(new JWTLoginSuccessResponse(true, jwt));
        });
    }

}























