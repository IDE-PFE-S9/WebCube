package fr.eseo.webcube.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import fr.eseo.webcube.api.dao.EtudiantDAO;
import fr.eseo.webcube.api.model.Etudiant;

public class EtudiantService {

    @Autowired
    private EtudiantDAO etudiantDAO;

    protected JpaRepository<Etudiant, Integer> getDao() {
		return etudiantDAO;
	}
    
}
