package com.example.demo.repo;
import com.example.demo.model.JWTRequest;
import org.springframework.data.jpa.repository.JpaRepository;



public interface AuthorisationRepo extends JpaRepository<JWTRequest,Integer> {

    JWTRequest findByEmail(String email);
}