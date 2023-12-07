package fr.eseo.webcube.api.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User implements Serializable {

    @Id
    @Column(name = "uniqueName")
    private String uniqueName;

    @Column(name = "surname")
    private String surname;

    @Column(name = "firstname")
    private String firstname;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "uniqueName"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;
}
