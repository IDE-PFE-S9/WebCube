package fr.eseo.webcube.api.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
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

    @Query("SELECT new fr.eseo.webcube.api.Response.TpResponse(u.user.uniqueName, u.user.firstname, u.user.surname, u.completion) " +
            "FROM UserTP u WHERE u.tp.id = :tpId")
    List<TpResponse> findTpResponseByTpId(@Param("tpId") Integer tpId);
     
}
