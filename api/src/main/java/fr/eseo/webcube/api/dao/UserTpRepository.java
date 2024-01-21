package fr.eseo.webcube.api.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.eseo.webcube.api.Details.TpDetails;
import fr.eseo.webcube.api.Response.TpResponse;
import fr.eseo.webcube.api.model.UserTP;
import fr.eseo.webcube.api.model.UserTpKey;

@Repository
public interface UserTpRepository extends JpaRepository<UserTP, UserTpKey> {

    @Query("SELECT new fr.eseo.webcube.api.Details.TpDetails(u.completion, u.tp.id, u.tp.name, u.tp.type, u.tp.gitLink) FROM UserTP u WHERE u.user.uniqueName = :uniqueName")
    List<TpDetails> findDetailsByUniqueName(@Param("uniqueName") String uniqueName);

    @Query("SELECT new fr.eseo.webcube.api.Details.TpDetails(u.completion, u.tp.id, u.tp.name, u.tp.type, u.tp.gitLink) FROM UserTP u WHERE u.user.uniqueName = :uniqueName AND u.tp.id = :tpId")
    List<TpDetails> findDetailsByUniqueNameAndTpId(@Param("uniqueName") String uniqueName, @Param("tpId") Integer tpId);

    @Query(value = "SELECT user.unique_name, user.firstname, user.surname, user_tp.completion, GROUP_CONCAT(role.role) as roles " +
        "FROM user " +
        "JOIN user_tp ON user.unique_name = user_tp.unique_name " +
        "JOIN tp ON user_tp.tp_id = tp.id " +
        "JOIN user_roles ON user.unique_name = user_roles.unique_name " +
        "JOIN role ON user_roles.role_id = role.id_role " +
        "WHERE tp.id = :tpId " +
        "GROUP BY user.unique_name, user.firstname, user.surname, user_tp.completion", nativeQuery = true)
    List<Object[]> findTpResponseByTpId(@Param("tpId") Integer tpId);

    @Modifying
    @Query("UPDATE UserTP u SET u.completion = :completion WHERE u.user.uniqueName = :uniqueName AND u.tp.id = :tpId")
    void updateCompletion(@Param("uniqueName") String uniqueName, @Param("tpId") Integer tpId, @Param("completion") Integer completion);
     
    @Modifying
    @Query("UPDATE UserTP u SET u.timeElapsed = :timeElapsed WHERE u.user.uniqueName = :uniqueName AND u.tp.id = :tpId")
    void updateTimeElapsed(@Param("uniqueName") String uniqueName, @Param("tpId") Integer tpId, @Param("timeElapsed") Integer timeElapsed);
}
