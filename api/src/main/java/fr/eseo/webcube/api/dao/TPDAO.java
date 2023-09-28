package fr.eseo.webcube.api.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.eseo.webcube.api.model.TP;

public interface TPDAO extends JpaRepository<TP, Integer>{
    
}
