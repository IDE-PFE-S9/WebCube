package fr.eseo.webcube.api.dao;

import org.springframework.stereotype.Repository;

import fr.eseo.webcube.api.model.Role;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


@Repository
public interface RoleRepository extends JpaRepository<Role, Integer>{

    Optional<Role> findByRole(String role);

    @Query(nativeQuery = true, value = "SELECT * FROM role WHERE role = :role")
    Role findRoleGroupByString(String role);
    
}
