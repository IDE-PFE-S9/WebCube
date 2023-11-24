package fr.eseo.webcube.api.dao;

import org.springframework.stereotype.Repository;

import fr.eseo.webcube.api.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface UserDAO extends JpaRepository<User, Integer>{

    Optional<User> findByUniqueName(String uniqueName);
    
}
