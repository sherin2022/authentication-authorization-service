package com.example.demo.model;
import com.example.demo.dto.UserWithOutPassword;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JWTResponse {

    private String jwtToken;
    private UserWithOutPassword userWithOutPassword;

}