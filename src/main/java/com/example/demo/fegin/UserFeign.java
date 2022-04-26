package com.example.demo.fegin;

import com.example.demo.configuration.CustomRetryClientConfig;
import com.example.demo.dto.User;
import com.example.demo.dto.UserWithOutPassword;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "user-service", configuration = CustomRetryClientConfig.class,fallbackFactory = HystrixFallBackFactory.class)
public interface UserFeign {
    @PostMapping("/users")
    UserWithOutPassword createUser(User user);
    @GetMapping("/users/getUserByEmail/{emailId}")
    UserWithOutPassword getUserDetailsByEmail(@PathVariable("emailId") String emailId);
}
