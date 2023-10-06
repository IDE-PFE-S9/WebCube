package fr.eseo.webcube.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import fr.eseo.webcube.api.dao.ProfesseurDAO;
import fr.eseo.webcube.api.model.Professeur;

@Service
public class ProfesseurService {

    @Autowired
    private ProfesseurDAO professeurDAO;

    protected JpaRepository<Professeur, Integer> getDao() {
		return professeurDAO;
	}
    
}
