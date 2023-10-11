package fr.eseo.webcube.api.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class TP implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idTp")
    private Integer idTp;

    @Column(name = "avancement")
    private Integer avancement;

    @ManyToOne
    @JoinColumn(name = "idEtudiant")
    private Etudiant etudiant;

    @Column(name = "isExam")
    private boolean isExam;
    
}
