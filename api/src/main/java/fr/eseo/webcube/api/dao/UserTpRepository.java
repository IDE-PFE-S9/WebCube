package fr.eseo.webcube.api.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.eseo.webcube.api.model.UserTP;
import fr.eseo.webcube.api.model.UserTpKey;

@Repository
public interface UserTpRepository extends JpaRepository<UserTP, UserTpKey> {
    
}
