package com.example.demo.exception;


public class CredentialIncorrectException extends RuntimeException{
    public CredentialIncorrectException(String s) {
        super(s);
    }
}