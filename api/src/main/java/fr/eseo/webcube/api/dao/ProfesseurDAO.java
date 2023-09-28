package fr.eseo.webcube.api.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.eseo.webcube.api.model.Professeur;

public interface ProfesseurDAO extends JpaRepository<Professeur, Integer>{
    
}
