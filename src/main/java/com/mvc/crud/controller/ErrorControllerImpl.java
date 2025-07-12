package com.mvc.crud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ErrorControllerImpl implements org.springframework.boot.web.servlet.error.ErrorController {

    @GetMapping("/error")
    public String handleError(HttpServletRequest request) {
        // Let Spring handle based on status code
        return "error/default";
    }
}

