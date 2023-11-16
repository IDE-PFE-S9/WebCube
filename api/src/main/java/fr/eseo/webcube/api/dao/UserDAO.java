package fr.eseo.webcube.api.dao;

import org.springframework.stereotype.Repository;

import fr.eseo.webcube.api.model.User;

import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface UserDAO extends JpaRepository<User, Integer>{

    User findByUniqueName(String uniqueName);
    
}
