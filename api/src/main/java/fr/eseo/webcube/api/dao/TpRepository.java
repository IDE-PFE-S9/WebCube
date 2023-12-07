package fr.eseo.webcube.api.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.eseo.webcube.api.model.TP;

@Repository
public interface TpRepository extends JpaRepository<TP, Integer>{
    
}
