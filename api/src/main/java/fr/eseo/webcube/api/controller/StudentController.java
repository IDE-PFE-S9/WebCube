package fr.eseo.webcube.api.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.eseo.webcube.api.Response.UserResponse;
import fr.eseo.webcube.api.model.User;
import fr.eseo.webcube.api.service.UserService;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired
    private UserService userService;

    @RequestMapping("/all")
    public ResponseEntity<?> getAllUsersWithRoles() {

        try{
            List<UserResponse> users = userService.findAllWithRoles();
            return ResponseEntity.status(HttpStatus.OK).body(users);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }

    }


}
