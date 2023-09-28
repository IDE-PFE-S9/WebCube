package fr.eseo.webcube.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import fr.eseo.webcube.api.service.TPService;

@RestController
@RequestMapping("/tp")
public class TPController {

    @Autowired
    private TPService tpService;

    protected TPService getTpService() {
        return tpService;
    }
    
}
