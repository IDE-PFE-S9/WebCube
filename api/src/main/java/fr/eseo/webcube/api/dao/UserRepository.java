package fr.eseo.webcube.api.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.eseo.webcube.api.model.User;


@Repository
public interface UserRepository extends JpaRepository<User, String>{

    User findByUniqueName(String uniqueName);
    
}
