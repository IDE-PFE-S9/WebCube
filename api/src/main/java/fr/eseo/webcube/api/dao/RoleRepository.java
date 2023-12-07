package fr.eseo.webcube.api.dao;

import org.springframework.stereotype.Repository;

import fr.eseo.webcube.api.model.Role;

import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface RoleRepository extends JpaRepository<Role, Integer>{
    
}
