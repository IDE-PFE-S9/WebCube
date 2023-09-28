package fr.eseo.webcube.api.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class TP implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idTp")
    private Integer idTp;

    @Column(name = "avancement")
    private Integer avancement;

    @Column(name = "etudiant")
    private Etudiant etudiant;
    
}
