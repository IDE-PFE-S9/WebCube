package fr.eseo.webcube.api.dao;

import java.util.List;
import java.util.Optional;

import javax.swing.text.html.Option;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.eseo.webcube.api.model.User;


@Repository
public interface UserRepository extends JpaRepository<User, String>{

    Optional<User> findByUniqueName(String uniqueName);

    @Query(nativeQuery = true, value = "SELECT user.unique_name, user.firstname, user.surname, " +
    "GROUP_CONCAT(role.id_role) AS role_ids, GROUP_CONCAT(role.role) AS roles " +
    "FROM user " +
    "JOIN user_roles ON user.unique_name = user_roles.unique_name " +
    "JOIN role ON role.id_role = user_roles.role_id " +
    "GROUP BY user.unique_name, user.firstname, user.surname;")
    List<Object[]> findAllWithRoles();

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM user_roles WHERE unique_name = :uniqueName AND role_id = :roleId", nativeQuery = true)
    void removeRole(@Param("uniqueName") String uniqueName, @Param("roleId") Integer roleId);


    
}
