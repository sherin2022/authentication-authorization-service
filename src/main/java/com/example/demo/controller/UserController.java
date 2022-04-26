package com.example.demo.controller;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.User;
import com.example.demo.dto.UserWithOutPassword;
import com.example.demo.exception.CredentialIncorrectException;
import com.example.demo.exception.CustomFeignException;
import com.example.demo.exception.EmailAlreadyExistException;
import com.example.demo.exception.EmailNotFoundException;
import com.example.demo.fegin.UserFeign;
import com.example.demo.model.JWTRequest;
import com.example.demo.model.JWTResponse;
import com.example.demo.repo.AuthorisationRepo;
import com.example.demo.service.UserService;
import com.example.demo.utility.JWTUtility;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;

import static com.example.demo.constant.SecurityConstant.*;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtility jwtUtility;

    private final UserService userService;

    private final UserFeign userFeign;

    @Autowired
    AuthorisationRepo authorisationRepo;

    public UserController(UserService userService, UserFeign userFeign) {
        this.userService = userService;
        this.userFeign = userFeign;
    }

    @PostMapping("/login")
    public ResponseEntity<JWTResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            final UserDetails userDetails = userService.loadUserByUsername(loginRequest.getEmail());
            final String token = jwtUtility.generateToken(userDetails);
            if(!(userDetails.getPassword().equals(loginRequest.getPassword()))){
                throw new CredentialIncorrectException(CREDENTIAL_INCORRECT);
            }
            return ResponseEntity.status(HttpStatus.OK).body(new JWTResponse(token, userFeign.getUserDetailsByEmail(loginRequest.getEmail())));
        }
        catch (FeignException | HystrixRuntimeException e){
            throw new CustomFeignException(FEIGN_EXCEPTION);
        }
        catch (NullPointerException nullPointerException){
            throw new EmailNotFoundException(EMAIL_NOT_FOUND);
        }
    }
    @PostMapping("/signup")
    public ResponseEntity<JWTResponse> signup(@Valid @RequestBody User user) {
        try {
            JWTRequest jwtRequest = new JWTRequest();
            if(userService.emailIsPresent(user.getEmail())){
                throw new EmailAlreadyExistException(EMAIL_ALREADY_EXIST);
            }
            jwtRequest.setEmail(user.getEmail());
            jwtRequest.setPassword(user.getPassword());
            UserWithOutPassword userWithOutPassword = userFeign.createUser(user);
            authorisationRepo.save(jwtRequest);
            final UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
            final String token = jwtUtility.generateToken(userDetails);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new JWTResponse(token, userWithOutPassword));
        }
        catch (FeignException | HystrixRuntimeException e){
            throw new CustomFeignException(FEIGN_EXCEPTION);
        }

    }
}