package fr.eseo.webcube.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;


@Entity
@Getter
@Setter
public class Etudiant implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idEtudiant")
    private Integer idEtudiant;

    @Column(name = "nom")
    private String nom;

    @Column(name = "prenom")
    private String prenom;

    @Column(name = "groupe")
    private String groupe;

    @Column(name = "isRattrapage")
    private boolean isRattrage;

    @Column(name = "mail")
    private String mail;

    @Column(name = "mode")
    private String mode;

    @OneToMany(mappedBy = "etudiant")
    @JsonIgnore
    private TP tp;
    
}
