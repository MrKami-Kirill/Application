package main.rest.controllers;


import main.rest.api.response.AuthUserResponse;
import main.rest.api.response.Response;
import main.rest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/auth/")
public class ApiAuthController {

    @Autowired
    private UserService userService;

    public ApiAuthController() {
    }


    @GetMapping(value = "check")
    private ResponseEntity<Response> checkAuth(HttpServletRequest request) {
        return userService.checkAuth(request.getSession());
    }
}

