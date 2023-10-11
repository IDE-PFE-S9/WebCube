package fr.eseo.webcube.api.dao;

import org.springframework.stereotype.Repository;

import fr.eseo.webcube.api.model.Etudiant;

import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface EtudiantDAO extends JpaRepository<Etudiant, Integer>{
    
}
