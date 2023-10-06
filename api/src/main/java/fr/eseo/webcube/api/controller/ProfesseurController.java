package fr.eseo.webcube.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.eseo.webcube.api.service.ProfesseurService;

@RestController
@RequestMapping("/professeur")
public class ProfesseurController {

    @Autowired
    private ProfesseurService professeurService;

    protected ProfesseurService getService() {
        return professeurService;
    }
    
}
