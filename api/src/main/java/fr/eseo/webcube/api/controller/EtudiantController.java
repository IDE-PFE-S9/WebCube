package fr.eseo.webcube.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.eseo.webcube.api.service.UserService;

@RestController
@RequestMapping("/etudiant")
public class EtudiantController {

    @Autowired
    private UserService userService;

    protected UserService getService() {
        return userService;
    }
}
