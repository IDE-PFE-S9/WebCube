package fr.eseo.webcube.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.eseo.webcube.api.dao.TPDAO;

@Service
public class TPService {

    @Autowired
    private TPDAO tpdao;

    //TODO regler probleme de type JpaRepository<TP,Integrer>
    protected TPDAO getDao() {
		return tpdao;
	}
    
}
